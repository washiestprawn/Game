package com.flipflop.game.input;

import java.util.logging.Logger;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

public class InputDaemon implements Runnable{

	private static final Logger logger = Logger.getLogger(InputDaemon.class.getName());
	private Thread inputDaemon;
	private boolean initSuccess = false;
	private boolean running = false;
	
	public InputDaemon (){
	}
	
	public void init() {
		try {
			Mouse.create();
			Keyboard.create();
			initSuccess = true;
		} catch (LWJGLException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void run() {
		init();
		if (initSuccess) {
			while (running){
				
			}
		}
	}
	
	public void start() {
		this.running = true;
		this.inputDaemon = new Thread(this, "InputDaemon");
		this.inputDaemon.start();
	}
	
	
}
