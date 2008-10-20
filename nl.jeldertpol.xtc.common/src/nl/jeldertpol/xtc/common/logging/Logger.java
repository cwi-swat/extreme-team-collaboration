package nl.jeldertpol.xtc.common.logging;

import java.util.logging.Level;

public interface Logger {

	/**
	 * Log an error.
	 * 
	 * @param level
	 *            Severity level of error.
	 * @param message
	 *            The message to be logged.
	 */
	public abstract void log(final Level level, final String message);

	/**
	 * Log an error. Will log stacktrace if level > {@link Level#INFO}, else
	 * only logs message.
	 * 
	 * @param level
	 *            Severity level of error.
	 * @param exception
	 *            Exception to be logged. Will call
	 *            {@link Exception#getMessage()} and {@link #log(Level, String)}
	 *            .
	 */
	public abstract void log(final Level level, final Exception exception);

	/**
	 * Log an error. Will log stacktrace if level > {@link Level#INFO}, else
	 * only logs message.
	 * 
	 * @param level
	 *            Severity level of error.
	 * @param message
	 *            The message to be logged.
	 * @param exception
	 *            Exception to be logged. Will call
	 *            {@link Exception#getMessage()} and {@link #log(Level, String)}
	 *            .
	 */
	public abstract void log(final Level level, final String message,
			final Exception exception);

}