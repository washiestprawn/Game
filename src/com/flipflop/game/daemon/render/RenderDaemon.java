package com.flipflop.game.daemon.render;

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

import java.awt.Canvas;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.util.glu.Project;

import com.flipflop.game.DisplayUtil;
import com.flipflop.game.daemon.Daemon;

public class RenderDaemon extends Daemon {

	private DisplayMode currentDisplayMode;
	private DisplayMode targetDisplayMode;
	private String appName;
	private Canvas canvas;
	private Renderer renderer;
	
	public RenderDaemon(Canvas canvas, DisplayMode targetDisplayMode, Renderer renderer, String appName) {
		super("RenderLoop");
		this.canvas = canvas;
		this.appName = appName;
		this.targetDisplayMode = targetDisplayMode;
		this.renderer = renderer;
		super.timeSync.setTargetTPS(60.0f);
	}

	public void init() {
		while(this.canvas.isDisplayable() == false) {
			logger.info("Waiting for window to finish initializing...");
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {}
		}
		try {
			initLWJGL();
			initOpenGL();
		} catch (LWJGLException e) {
			e.printStackTrace();
		}
	}

	public void stop() {
		// TODO Implement clean up.
		// cleanUpLWJGL();
	}

	@Override
	public void execute() {
		Display.sync(60);
		// Clear buffer for redrawing.
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

		// Let Renderer know it's time to draw.
		this.renderer.render();

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
		Display.setParent(this.canvas);
		Display.setTitle(this.appName);
		// TODO How to do fullscreen?
		if (DisplayUtil.tryDisplayChange(targetDisplayMode, false)) {
			this.currentDisplayMode = targetDisplayMode;
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
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		glCullFace(GL_BACK);
		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();
		float aspect = this.currentDisplayMode.getWidth() / this.currentDisplayMode.getHeight();
		Project.gluPerspective(45.0f, aspect, 0.1f, 25.0f);
		glMatrixMode(GL_MODELVIEW);
		glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
	}
}
