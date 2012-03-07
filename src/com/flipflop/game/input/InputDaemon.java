package com.flipflop.game.input;

import java.util.logging.Logger;

import com.flipflop.game.daemon.Daemon;

public class InputDaemon extends Daemon {

	private static final Logger logger = Logger.getLogger(InputDaemon.class.getName());
	private boolean initSuccess = false;
	
	
	public InputDaemon (){
		super("InputDaemon");
		super.timeSync.setTargetTPS(30);
	}
	
	public void init() {
			initSuccess = true;
	}
	
	@Override
	public void execute() {
		// TODO Auto-generated method stub
		
	}
	
	
}
