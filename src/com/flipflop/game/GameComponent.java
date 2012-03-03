package com.flipflop.game;

import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_MODELVIEW;
import static org.lwjgl.opengl.GL11.GL_PROJECTION;
import static org.lwjgl.opengl.GL11.GL_VERSION;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glGetString;
import static org.lwjgl.opengl.GL11.glLoadIdentity;
import static org.lwjgl.opengl.GL11.glMatrixMode;
import static org.lwjgl.opengl.GL11.glViewport;
import static org.lwjgl.util.glu.GLU.gluOrtho2D;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.logging.Logger;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;

public abstract class GameComponent extends Canvas implements Runnable, WindowListener {
	private static final long serialVersionUID = 1L;

	private static final Logger logger = Logger.getLogger(GameComponent.class.getName());
	private static GameComponent instance;
	private boolean running = false;
	private String appName;
	private JFrame mainWindow;
	protected int width = 600;
	protected int height = 400;
	protected boolean isFullscreen = false;
	protected Mouse mouse;
	protected Keyboard keyboard;
	protected DisplayMode[] modesAvailable;
	protected DisplayMode currentDisplayMode;
	protected DisplayMode desktopDisplayMode;
	private Thread gameLoop;
	private boolean loopStarted = false;

	public GameComponent(String name, int width, int height, boolean fullscreen) throws LWJGLException {
		init(name, width, height, fullscreen);
	}

	public GameComponent(String name, int width, int height) throws LWJGLException {
		init(name, width, height, this.isFullscreen);
	}

	public GameComponent(String name) throws LWJGLException {
		init(name, this.width, this.height, this.isFullscreen);
	}
	
	private void init(String name, int width, int height, boolean isFullscreen) throws LWJGLException {
		logger.fine("Initializing GameComponent...");
		instance = this;
		this.width = width;
		this.height = height;
		this.isFullscreen = isFullscreen;
		this.appName = name;
		this.modesAvailable = Display.getAvailableDisplayModes();
		this.desktopDisplayMode = Display.getDesktopDisplayMode();
		logger.finer(String.format("Found %d acceptable Display Modes...", this.modesAvailable.length));
		logger.info("Initialized GameComponent.");
	}

	public static GameComponent getInstance() {
		return instance;
	}

	public void start() throws LWJGLException {
		logger.info("Initializing Canvas...");
		this.initWindow();
		logger.info("Starting game!");
		this.running = true;
		this.gameLoop = new Thread(this, "GameLoop");
		// this.gameLoop.setDaemon(true);
		this.gameLoop.start();
	}

	public void stop() throws LWJGLException {
		try {
			while (!loopStarted) {Thread.sleep(1000);}
			logger.info("Game Over, man.");
			this.running = false;
			if (this.gameLoop.isAlive()) {
				this.gameLoop.join();
				logger.fine(this.gameLoop.getName()+" came home.");
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		this.cleanUpWindow();
	}

	@Override
	public void run() {
		try {
			initLWJGL();
			initOpenGL();
			enterLoop();
			cleanUpLWJGL();
		} catch (LWJGLException e) {
			e.printStackTrace();
		}
		logger.finer(Thread.currentThread().getName() + " exiting.");
	}

	abstract protected void tick();

	private void enterLoop() {
		logger.fine("Starting loop...");
		while (this.running) {
			if (!loopStarted) loopStarted = true;
			Display.sync(60);
			this.tick();
			Display.update();
		}
		logger.fine("Leaving loop...");
	}

	protected void initWindow() {
		int x = this.desktopDisplayMode.getWidth()/2 - this.width/2;
		int y = this.desktopDisplayMode.getHeight()/2 - this.height/2;
		final Dimension dim = new Dimension(this.width, this.height);
		try {
			System.setProperty("com.apple.macos.useScreenMenuBars", "false");
			System.setProperty("com.apple.mrj.application.apple.menu.about.name", this.appName);
			UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
		} catch (ClassNotFoundException e) {e.printStackTrace();}
		catch (InstantiationException e) {e.printStackTrace();} 
		catch (IllegalAccessException e) {e.printStackTrace();}
		catch (UnsupportedLookAndFeelException e) {e.printStackTrace();}
		mainWindow = new JFrame(this.appName);
		JMenuBar menuBar = new JMenuBar();
		JMenu gameMenu = new JMenu("Game");
		JMenuItem quitItem = new JMenuItem("Quit");
		quitItem.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					GameComponent.getInstance().stop();
				} catch (LWJGLException e1) {}
			}
		});
		gameMenu.add(quitItem);
		menuBar.add(gameMenu);
		mainWindow.setMinimumSize(dim);
		mainWindow.setMaximumSize(dim);
		mainWindow.setSize(dim);
		mainWindow.setLocation(x, y);
		mainWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainWindow.addWindowListener(this);
		mainWindow.getContentPane().add(this, BorderLayout.CENTER);
		mainWindow.add(menuBar, BorderLayout.NORTH);
		mainWindow.pack();
		mainWindow.setVisible(true);
		this.setIgnoreRepaint(true);
		this.setFocusable(true);
		this.requestFocus();
	}

	private void initLWJGL() throws LWJGLException {
		Display.setParent(this);
		Display.setTitle(this.appName);
		DisplayMode targetMode = null;
		if (this.isFullscreen) {
			targetMode = DisplayUtil.getBestFullScreenMode(this.modesAvailable, this.desktopDisplayMode.getWidth(), this.desktopDisplayMode.getHeight());
		} else {
			targetMode = new DisplayMode(this.width, this.height);
		}
		if (DisplayUtil.tryDisplayChange(targetMode, this.isFullscreen)) {
			this.currentDisplayMode = targetMode;
		}
	}

	private void initOpenGL() throws LWJGLException {
		logger.config("OpenGL Version: " + glGetString(GL_VERSION));
		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();
		gluOrtho2D(0, this.currentDisplayMode.getWidth(), 0, this.currentDisplayMode.getHeight());
		glMatrixMode(GL_MODELVIEW);
		glLoadIdentity();
		glViewport(0, 0, this.currentDisplayMode.getWidth(), this.currentDisplayMode.getHeight());
		// set clear color to black
		glClearColor(1.0f, 1.0f, 1.0f, 0.0f);
		glClear(GL_COLOR_BUFFER_BIT);
	}

	protected void cleanUpWindow() {
		this.mainWindow.dispose();
	}

	private void cleanUpLWJGL() {
		Display.destroy();
	}
	
	@Override
	public void windowClosing(WindowEvent e) {
		try {
			this.stop();
		} catch (LWJGLException e1) {
			e1.printStackTrace();
			this.cleanUpWindow();
		}
	}
	
	@Override
	public void windowActivated(WindowEvent e) {}
	@Override
	public void windowClosed(WindowEvent e) {}
	@Override
	public void windowDeactivated(WindowEvent e) {}
	@Override
	public void windowDeiconified(WindowEvent e) {}
	@Override
	public void windowIconified(WindowEvent e) {}
	@Override
	public void windowOpened(WindowEvent e) {}


	public static class DisplayUtil {
		public static boolean tryDisplayChange(DisplayMode dm, boolean isFullscreen) {
			boolean success = false;
			try {
				Display.setDisplayMode(dm);
				Display.setFullscreen(isFullscreen);
				Display.create();
				success = true;
			} catch (LWJGLException e) {
				e.printStackTrace();
			}
			
			return success;
		}
		public static DisplayMode getBestFullScreenMode(DisplayMode[] modesAvailable, int width, int height) throws LWJGLException {
			int bestIndex = -1;
			int currWd = Display.getDesktopDisplayMode().getWidth();
			int currHt = Display.getDesktopDisplayMode().getHeight();
			int currHz = Display.getDesktopDisplayMode().getFrequency();
			int currBpp = Display.getDesktopDisplayMode().getBitsPerPixel();
			
			for (int i = 0; i < modesAvailable.length; i++) {
				DisplayMode mode = modesAvailable[i];
				logger.finest(String.format("Checking: W: %d\tH: %d\tHz: %d\tBpp: %d", mode.getWidth(), mode.getHeight(),
						mode.getFrequency(), mode.getBitsPerPixel()));
				if (currHz == mode.getFrequency() && currBpp == mode.getBitsPerPixel() && currWd == mode.getWidth()
						&& currHt == mode.getHeight()) {
					bestIndex = i;
					logger.finest("Chosen!");
					break;
				}
				
			}
			
			if (bestIndex != -1) {
				DisplayMode targetMode = modesAvailable[bestIndex];
				logger.fine(String.format("Using: W: %d\tH: %d\tHz: %d\tBpp: %d", targetMode.getWidth(),
						targetMode.getHeight(), targetMode.getFrequency(), targetMode.getBitsPerPixel()));
				return targetMode;
			} else {
				return null;
			}
		}
	}
}
