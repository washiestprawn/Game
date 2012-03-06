package com.flipflop.game.daemon;

import java.util.logging.Logger;

/**
 * Abstract class to handle the unfortunate business that is thread management.
 * This class specifically handles Daemon-type threads, i.e. threads that poll
 * and sleep for extended periods of time.
 * 
 * @author joma
 * 
 */
public abstract class Daemon implements Runnable {

	protected static final Logger logger = Logger.getLogger(Daemon.class.getName());
	private String daemonName = "DaemonThread";
	private Thread daemon;
	private boolean running = false;
	private boolean isDone = false;
	private Boolean hasStarted = false;
	private TimeSync timeSync;

	public Daemon() {
	}

	public Daemon(String daemonName) {
		this.daemonName = daemonName;
	}

	public void start() {
		this.running = true;
		this.daemon = new Thread(this, daemonName);
		this.daemon.start();
	}

	public void stop() {
		this.running = false;
	}

	public boolean waitForStart(int milli) {
		synchronized (this.hasStarted) {
			if (!this.hasStarted) {
				try {
					this.hasStarted.wait(milli);
				} catch (InterruptedException e) {
				}
			}
		}
		return this.hasStarted;
	}

	public boolean isRunning() {
		return (this.hasStarted && this.running);
	}

	@Override
	public void run() {
		while (this.running) {
			if (!this.hasStarted) {
				synchronized (this.hasStarted) {
					this.hasStarted = true;
					this.hasStarted.notifyAll();
				}
			}
			execute();
		}
	}

	public abstract void execute();

}
