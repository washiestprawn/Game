package com.flipflop.game;

import static org.lwjgl.opengl.GL11.*;

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

/**
 * 
 * @author Joseph Gilley
 * 
 *         The GameComponent is a rudimentary game engine combining the built-in
 *         capabilities of the java {@link Canvas} and the open source LWJGL
 *         libraries. This abstract class must be extended by a class that
 *         wishes to use the abilities provided.
 */
public abstract class GameComponent extends Canvas implements Runnable, WindowListener {
	private static final long serialVersionUID = 1L; // This is meaningless.
														// Don't worry about it.

	private static final Logger logger = Logger.getLogger(GameComponent.class.getName());
	private static GameComponent instance;
	private boolean running = false; // GameLoop controller.
	private String appName = "Game"; // Game's name. Should be initialized in a
										// constructor.
	private JFrame mainWindow; // The main window in which a OpenGL context will
								// be created.
	protected int width = 640; // The default width of the main window. Can be
								// overridden in constructor.
	protected int height = 400; // The default height of the main window. Can be
								// overridden in constructor.
	protected boolean isFullscreen = false; // Whether the main window should be
											// created in fullscreen mode.
	protected Mouse mouse; // Mouse interface
	protected Keyboard keyboard; // Keyboard interface
	protected DisplayMode[] modesAvailable; // DisplayModes supported by the
											// graphics card
	protected DisplayMode currentDisplayMode; // The DisplayMode we are
												// currently using
	protected DisplayMode desktopDisplayMode; // The desktop's display mode.
												// Used to get monitor settings
												// such as refresh rate, bits
												// per pixel, and dimensions.
	private Thread gameLoop; // The thread executing the GameLoop. Held so we
								// can join on it after we tell it to stop
								// running.
	private boolean loopStarted = false; // Controller for the GameLoop so we
											// can't close the window BEFORE the
											// GameLoop even starts.

	/**
	 * Constructs the {@link GameComponent}.
	 * 
	 * <p>
	 * If <code>fullscreen</code> is <code>true</code>, then the width
	 * <code>width</code> and height <code>height</code> are the desired
	 * fullscreen resolution dimensions. The best resolution supported by the
	 * graphics will be chosen based on these values, exact resolution is not
	 * guaranteed. This value is <code>false</code> by default.
	 * </p>
	 * 
	 * @param name
	 *            The title of your game. This name will be the title of the
	 *            generated window.
	 * @param width
	 *            The width of the main window. If <code>fullscreen</code> is
	 *            <code>true</code>, then it will be the desired starting width
	 *            component of the resolution used.
	 * @param height
	 *            The height of the main window. Arbitrary if
	 *            <code>fullscreen</code> is <code>true</code>, then it will be
	 *            the desired starting width component of the resolution used.
	 * @param fullscreen
	 *            <code>true</code> if the game should start up in fullscreen
	 *            mode. <code>false</code> to start in windowed mode.
	 * @throws LWJGLException
	 */
	public GameComponent(String name, int width, int height, boolean fullscreen) throws LWJGLException {
		init(name, width, height, fullscreen);
	}

	/**
	 * Constructs the {@link GameComponent} in windowed mode with the dimensions
	 * <code>width</code>x<code>height</code>.
	 * 
	 * @param name
	 *            The title of your game. This name will be the title of the
	 *            generated window.
	 * @param width
	 *            The width of the main window.
	 * @param height
	 *            The height of the main window.
	 * @throws LWJGLException
	 */
	public GameComponent(String name, int width, int height) throws LWJGLException {
		init(name, width, height, this.isFullscreen);
	}

	/**
	 * Constructs the {@link GameComponent} in windowed mode with the dimensions
	 * 640x400.
	 * 
	 * @param name
	 *            The title of your game. This name will be the title of the
	 *            generated window.
	 * @param width
	 *            The width of the main window.
	 * @param height
	 *            The height of the main window.
	 * @throws LWJGLException
	 */
	public GameComponent(String name) throws LWJGLException {
		init(name, this.width, this.height, this.isFullscreen);
	}

	/**
	 * Constructs the {@link GameComponent}.
	 * 
	 * <p>
	 * If <code>fullscreen</code> is <code>true</code>, then the width
	 * <code>width</code> and height <code>height</code> are the desired
	 * fullscreen resolution dimensions. The best resolution supported by the
	 * graphics will be chosen based on these values, exact resolution is not
	 * guaranteed. This value is <code>false</code> by default.
	 * </p>
	 * 
	 * <p>
	 * Game will have the default name of "Game".
	 * </p>
	 * 
	 * @param width
	 *            The width of the main window. If <code>fullscreen</code> is
	 *            <code>true</code>, then it will be the desired starting width
	 *            component of the resolution used.
	 * @param height
	 *            The height of the main window. Arbitrary if
	 *            <code>fullscreen</code> is <code>true</code>, then it will be
	 *            the desired starting width component of the resolution used.
	 * @param fullscreen
	 *            <code>true</code> if the game should start up in fullscreen
	 *            mode. <code>false</code> to start in windowed mode.
	 * @throws LWJGLException
	 */
	public GameComponent(int width, int height, boolean fullscreen) throws LWJGLException {
		init(this.appName, width, height, fullscreen);
	}

	/**
	 * Constructs the {@link GameComponent} in windowed mode with the dimensions
	 * <code>width</code>x<code>height</code>.
	 * 
	 * <p>
	 * Game will have the default name of "Game".
	 * </p>
	 * 
	 * @param width
	 *            The width of the main window.
	 * @param height
	 *            The height of the main window.
	 * @throws LWJGLException
	 */
	public GameComponent(int width, int height) throws LWJGLException {
		init(this.appName, width, height, this.isFullscreen);
	}

	/**
	 * Constructs the {@link GameComponent} in windowed mode with the dimensions
	 * <code>640</code>x<code>400</code>.
	 * 
	 * <p>
	 * Game will have the default name of "Game".
	 * </p>
	 * 
	 * @param width
	 *            The width of the main window.
	 * @param height
	 *            The height of the main window.
	 * @throws LWJGLException
	 */
	public GameComponent() throws LWJGLException {
		init(this.appName, this.width, this.height, this.isFullscreen);
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

	/**
	 * Allows global access to the GameComponent engine.
	 * 
	 * @return {@link GameComponent} the current instantiated
	 *         {@link GameComponent}.
	 */
	public static GameComponent getInstance() {
		return instance;
	}

	/**
	 * Start the game and create the GameLoop thread (identified by the name
	 * "GameLoop") and kicks off the game loop. The game loop will call
	 * {@link GameComponent#tick()} every 60th of a second.
	 * 
	 * @throws LWJGLException
	 */
	public void start() {
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
			// Give the game loop a chance to start before trying to stop it.
			while (!loopStarted) {
				Thread.sleep(200);
			}
			logger.info("Game Over, man.");
			this.running = false;

			// Only try to join if it's still alive. Joining a dead thread is no
			// good.
			if (this.gameLoop.isAlive()) {
				this.gameLoop.join();
				logger.fine(this.gameLoop.getName() + " came home.");
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
			if (!loopStarted)
				loopStarted = true;

			// Synchronize to 60 fps.
			// TODO Eventually we'll need a higher resolution ticker for physics
			// and game logic, but this blocks all activity. Maybe another
			// thread for physics resolutions and game logic...
			Display.sync(60);
			
			// Clear buffer for redrawing.
			glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
			
			// Let subclass know it's time to draw.
			this.tick();
			
			// Swap buffers.  Display is by default a double-buffer configuration.
			Display.update();
		}
		logger.fine("Leaving loop...");
	}

	/**
	 * Initializes the Java window and handles some platform specific
	 * alterations.
	 */
	protected void initWindow() {
		int x = this.desktopDisplayMode.getWidth() / 2 - this.width / 2;
		int y = this.desktopDisplayMode.getHeight() / 2 - this.height / 2;
		final Dimension dim = new Dimension(this.width, this.height);
		try {
			System.setProperty("com.apple.macos.useScreenMenuBars", "false");
			System.setProperty("com.apple.mrj.application.apple.menu.about.name", this.appName);
			UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
		mainWindow = new JFrame(this.appName);
		JMenuBar menuBar = new JMenuBar();
		JMenu gameMenu = new JMenu("Game");
		JMenuItem quitItem = new JMenuItem("Quit");
		quitItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					GameComponent.getInstance().stop();
				} catch (LWJGLException e1) {
				}
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
		// mainWindow.add(menuBar, BorderLayout.NORTH);
		mainWindow.pack();
		mainWindow.setVisible(true);
		this.setIgnoreRepaint(true);
		this.setFocusable(true);
		this.requestFocus();
	}

	/**
	 * Initializes the LWJGL utilities used by this engine.
	 * 
	 * @throws LWJGLException
	 */
	private void initLWJGL() throws LWJGLException {
		Display.setParent(this);
		Display.setTitle(this.appName);
		DisplayMode targetMode = null;
		if (this.isFullscreen) {
			targetMode = DisplayUtil.getBestFullScreenMode(this.modesAvailable, this.desktopDisplayMode.getWidth(),
					this.desktopDisplayMode.getHeight());
		} else {
			targetMode = new DisplayMode(this.width, this.height);
		}
		if (DisplayUtil.tryDisplayChange(targetMode, this.isFullscreen)) {
			this.currentDisplayMode = targetMode;
		}
	}

	/**
	 * Initializes the OpenGL states and matrices. Must be called from the
	 * thread that created the OpenGL context. In this case, the GameLoop should
	 * be the only thread to call this method.
	 * 
	 * @throws LWJGLException
	 */
	private void initOpenGL() throws LWJGLException {
		logger.config("OpenGL Version: " + glGetString(GL_VERSION));
		glViewport(0, 0, this.currentDisplayMode.getWidth(), this.currentDisplayMode.getHeight());
		glEnable(GL_DEPTH_TEST);
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		glHint(GL_POLYGON_SMOOTH_HINT, GL_DONT_CARE);
		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();
		// glOrtho(0, this.currentDisplayMode.getWidth(), 0,
		// this.currentDisplayMode.getHeight(), 1, -1);
		glFrustum(-1.0d, 1.0d, -1.0d, 1.0d, 1.5d, 20.0d);
		glMatrixMode(GL_MODELVIEW);
		// set clear color to black
		glClearColor(1.0f, 1.0f, 1.0f, 0.0f);
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
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
	public void windowActivated(WindowEvent e) {
	}

	@Override
	public void windowClosed(WindowEvent e) {
	}

	@Override
	public void windowDeactivated(WindowEvent e) {
	}

	@Override
	public void windowDeiconified(WindowEvent e) {
	}

	@Override
	public void windowIconified(WindowEvent e) {
	}

	@Override
	public void windowOpened(WindowEvent e) {
	}

	/**
	 * 
	 * @author joma
	 * 
	 *         Utility class regarding the {@link Display} class from LWJGL
	 * 
	 */
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

		public static DisplayMode getBestFullScreenMode(DisplayMode[] modesAvailable, int width, int height)
				throws LWJGLException {
			int bestIndex = -1;
			int currWd = Display.getDesktopDisplayMode().getWidth();
			int currHt = Display.getDesktopDisplayMode().getHeight();
			int currHz = Display.getDesktopDisplayMode().getFrequency();
			int currBpp = Display.getDesktopDisplayMode().getBitsPerPixel();

			for (int i = 0; i < modesAvailable.length; i++) {
				DisplayMode mode = modesAvailable[i];
				logger.finest(String.format("Checking: W: %d\tH: %d\tHz: %d\tBpp: %d", mode.getWidth(),
						mode.getHeight(), mode.getFrequency(), mode.getBitsPerPixel()));
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
