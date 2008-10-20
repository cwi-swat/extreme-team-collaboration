package nl.jeldertpol.xtc.common.logging;

import java.util.logging.Level;

/**
 * A logger that does not log.
 * 
 * @author Jeldert Pol
 */
public class NullLogger implements Logger {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * nl.jeldertpol.xtc.common.logging.LoggerI#log(java.util.logging.Level,
	 * java.lang.String)
	 */
	@Override
	public void log(Level level, String message) {
		// Nothing to do
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * nl.jeldertpol.xtc.common.logging.LoggerI#log(java.util.logging.Level,
	 * java.lang.Exception)
	 */
	@Override
	public void log(Level level, Exception exception) {
		// Nothing to do
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * nl.jeldertpol.xtc.common.logging.LoggerI#log(java.util.logging.Level,
	 * java.lang.String, java.lang.Exception)
	 */
	@Override
	public void log(Level level, String message, Exception exception) {
		// Nothing to do
	}

}
