package nl.jeldertpol.xtc.common.logging;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.SimpleFormatter;

/**
 * 
 * @author Jeldert Pol
 */
public class Logger {

	private final java.util.logging.Logger log;

	private final static String logName = "nl.jeldertpol.xtc";

	private final String logFile = "XTC.log";

	/**
	 * Creates a new logger. Writes log messages to a file.
	 */
	public Logger() {
		log = java.util.logging.Logger.getLogger(logName);
		log.setLevel(Level.ALL);

		try {
			boolean append = true;
			FileHandler fileHandler = new FileHandler(logFile, append);
			log.addHandler(fileHandler);

			Formatter formatter = new SimpleFormatter();
			fileHandler.setFormatter(formatter);
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Log an error.
	 * 
	 * @param level
	 *            Severity level of error.
	 * @param message
	 *            The message to be logged.
	 */
	public void log(Level level, String message) {
		log.log(level, message);
	}

	/**
	 * Log an error.
	 * 
	 * @param level
	 *            Severity level of error.
	 * @param exception
	 *            Exception to be logged. Will call
	 *            {@link Exception#getMessage()} and {@link #log(Level, String)}
	 *            .
	 */
	public void log(Level level, Exception exception) {
		log(level, exception.getMessage());
	}

	/**
	 * Log an error.
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
	public void log(Level level, String message, Exception exception) {
		log(level, message + "\n" + exception.getMessage());
	}

}
