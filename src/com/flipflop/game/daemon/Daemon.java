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
	private boolean hasStarted = false;
	private final Boolean startLock = new Boolean(true);
	protected TimeSync timeSync = new TimeSync();

	public Daemon() {
	}

	public Daemon(String daemonName) {
		this.daemonName = daemonName;
	}

	public String getDaemonName() {
		return daemonName;
	}

	public void start() {
		this.running = true;
		this.daemon = new Thread(this, daemonName);
		this.daemon.start();
	}

	public void stop() {
		this.running = false;
	}
	
	public void join(long millis) throws InterruptedException {
		this.daemon.join(millis);
	}

	public boolean waitForStart(int milli) {
		synchronized (this.startLock) {
			if (!this.hasStarted) {
				try {
					this.startLock.wait(milli);
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
		init();
		while (this.running) {
			if (!this.hasStarted) {
				synchronized (this.startLock) {
					this.hasStarted = true;
					this.startLock.notify();
				}
			}
			execute();
			this.timeSync.syncTPS();
		}
	}

	public abstract void execute();
	public abstract void init();

}
