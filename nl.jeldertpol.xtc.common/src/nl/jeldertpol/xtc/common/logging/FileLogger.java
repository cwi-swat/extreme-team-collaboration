package nl.jeldertpol.xtc.common.logging;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.SimpleFormatter;
import java.util.logging.XMLFormatter;

/**
 * A logger that log to a file.
 * 
 * @author Jeldert Pol
 */
public class FileLogger implements Logger {

	private final java.util.logging.Logger log;

	private final static String logName = "nl.jeldertpol.xtc";

	/**
	 * Name of file.
	 */
	private final String logFile = "XTC.log";

	/**
	 * Defines the types of log file.
	 */
	public static enum LogType {
		PLAIN, XML
	};

	/**
	 * Creates a new logger. Writes log messages to a file.
	 * 
	 * @param logType
	 *            The format of the log file.
	 */
	public FileLogger(final LogType logType) {
		log = java.util.logging.Logger.getLogger(logName);
		log.setLevel(Level.ALL);

		try {
			boolean append = true;
			FileHandler fileHandler = new FileHandler(logFile, append);
			log.addHandler(fileHandler);

			Formatter formatter;
			if (logType == LogType.XML) {
				formatter = new XMLFormatter();
			} else {
				formatter = new SimpleFormatter();
			}

			fileHandler.setFormatter(formatter);
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * nl.jeldertpol.xtc.common.logging.LoggerI#log(java.util.logging.Level,
	 * java.lang.String)
	 */
	public void log(final Level level, final String message) {
		log.log(level, message);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * nl.jeldertpol.xtc.common.logging.LoggerI#log(java.util.logging.Level,
	 * java.lang.Exception)
	 */
	public void log(final Level level, final Exception exception) {
		if (level.intValue() > Level.INFO.intValue()) {
			log(level, exception.getMessage(), exception);
		} else {
			log(level, exception.getMessage());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * nl.jeldertpol.xtc.common.logging.LoggerI#log(java.util.logging.Level,
	 * java.lang.String, java.lang.Exception)
	 */
	public void log(final Level level, final String message,
			final Exception exception) {
		if (level.intValue() > Level.INFO.intValue()) {
			log.log(level, message, exception);
		} else {
			log(level, message + "\n" + exception.getMessage());
		}
	}

}