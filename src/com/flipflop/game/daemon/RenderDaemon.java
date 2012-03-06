package com.flipflop.game.daemon;

import static org.lwjgl.opengl.GL11.GL_BACK;
import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.GL_LINE_SMOOTH;
import static org.lwjgl.opengl.GL11.GL_LINE_SMOOTH_HINT;
import static org.lwjgl.opengl.GL11.GL_MODELVIEW;
import static org.lwjgl.opengl.GL11.GL_NICEST;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_PROJECTION;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_VERSION;
import static org.lwjgl.opengl.GL11.glBlendFunc;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glCullFace;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glGetString;
import static org.lwjgl.opengl.GL11.glHint;
import static org.lwjgl.opengl.GL11.glLoadIdentity;
import static org.lwjgl.opengl.GL11.glMatrixMode;
import static org.lwjgl.opengl.GL11.glViewport;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.util.glu.Project;

import com.flipflop.game.GameComponent.DisplayUtil;

public class RenderDaemon extends Daemon {
	
	DisplayMode currentDisplayMode;
	
	public RenderDaemon(DisplayMode targetDisplayMode) {
		super();
		this.currentDisplayMode = targetDisplayMode;
	}
	
	@Override
	public void start() {
		try {
			initLWJGL();
			initOpenGL();
			super.start();
		} catch (LWJGLException e) {
			e.printStackTrace();
		}
		logger.finer(Thread.currentThread().getName() + " exiting.");
	}
	
	public void stop() {
		cleanUpLWJGL();
	}
	
	@Override
	public void execute() {
		Display.sync(60);

		// Clear buffer for redrawing.
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

		// Let subclass know it's time to draw.
		this.render();

		// Swap buffers. Display is by default a double-buffer
		// configuration.
		Display.update();
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
		glEnable(GL_LINE_SMOOTH);
		glHint(GL_LINE_SMOOTH_HINT, GL_NICEST);
		glBlendFunc (GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		glCullFace(GL_BACK);
		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();
		// glOrtho(0, this.currentDisplayMode.getWidth(), 0,
		// this.currentDisplayMode.getHeight(), 1, -1);
		float aspect = this.width/this.height;
		//glFrustum(-aspect, aspect, -1.0d/aspect, 1.0d/aspect, 1.5d, 20.0d);
		Project.gluPerspective(45.0f, aspect, 0.1f, 25.0f);
		glMatrixMode(GL_MODELVIEW);
		// set clear color to black
		glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
	}
}
