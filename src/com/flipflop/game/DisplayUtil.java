package com.flipflop.game;

import java.util.logging.Logger;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.ContextAttribs;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.PixelFormat;

/**
 * 
 * @author Joseph Gilley
 * 
 *         Utility class regarding the {@link Display} class from LWJGL
 * 
 */
public class DisplayUtil {

	private static final Logger logger = Logger.getLogger(DisplayUtil.class.getName());

	public static boolean tryDisplayChange(DisplayMode dm, boolean isFullscreen) {
		boolean success = false;
		try {
			Display.setDisplayMode(dm);
			Display.setFullscreen(isFullscreen);
			Display.create(new PixelFormat(), new ContextAttribs(3, 2).withProfileCore(false));
			success = true;
		} catch (LWJGLException e) {
			e.printStackTrace();
		}

		return success;
	}

	public static DisplayMode getBestFullScreenMode()
			throws LWJGLException {
		int bestIndex = -1;
		int currWd = Display.getDesktopDisplayMode().getWidth();
		int currHt = Display.getDesktopDisplayMode().getHeight();
		int currHz = Display.getDesktopDisplayMode().getFrequency();
		int currBpp = Display.getDesktopDisplayMode().getBitsPerPixel();
		DisplayMode[] modesAvailable = Display.getAvailableDisplayModes();

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