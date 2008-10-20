package nl.jeldertpol.xtc.server;

import toolbus.Main;
import toolbus.exceptions.ToolBusException;
import toolbus.viewer.Viewer;

/**
 * Thread to start an internal ToolBus. Since running the ToolBus does not
 * return, it must be performed in a thread of its own.
 * 
 * @author Jeldert Pol
 */
public class ToolbusThread implements Runnable {

	/**
	 * The arguments for the ToolBus (port and script).
	 */
	private final String args[];

	/**
	 * Whether to start a debug ToolBus.
	 */
	private final boolean debug;

	/**
	 * Start a new Thread for an internal ToolBus.
	 * 
	 * @param args
	 *            Arguments for the ToolBus. Needs a port ("-Pxxx") and a script
	 *            ("-Sxxx.tb").
	 * @param debug
	 *            When <code>true</code>, start a ToolBus with debugging
	 *            support. When <code>false</code>, start a normal ToolBus.
	 */
	public ToolbusThread(String args[], boolean debug) {
		this.args = args;
		this.debug = debug;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		if (debug) {
			try {
				Viewer.main(args);
			} catch (ToolBusException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			try {
				Main.main(args);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
