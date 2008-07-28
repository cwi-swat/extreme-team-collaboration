package nl.jeldertpol.xtc.client.startup;

import org.eclipse.ui.IStartup;

/**
 * Thic class ensures the plug-in gets loaded when Eclipse is loaded. This is
 * needed because some listeners need to be registered.
 * 
 * @author Jeldert Pol
 */
public final class Startup implements IStartup {

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.IStartup#earlyStartup()
	 */
	@Override
	public void earlyStartup() {
		// Ensures the plug-in gets loaded when Eclipse is loaded.
	}

}
