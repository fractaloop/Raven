/**
 * 
 */
package raven.utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import raven.utils.Log.Level;

/**
 * @author Chet
 * @author Logan Lowell
 *
 */
public class Log {

	/**
	 * Ordered by escalating severity
	 */
	public enum Level { INFO, DEBUG, WARN, ERROR, TRACE }; 
	
	// Singleton Dispatcher, just like the original.
	private static class LogHolder {
		public static final Log INSTANCE = new Log("raven.log");
	}

	public static Log getInstance() {
		return LogHolder.INSTANCE;
	}
	
	private File logFile;
	private Level logLevel;
	private DateFormat time;
	
	private Log(Level level, String path) {
		logLevel = level;
		logFile = new File(path);
		time = new SimpleDateFormat("HH:mm:ss> ");
		// Clear old file
		logFile.delete();
		try {
			logFile.createNewFile();
		} catch (IOException e) {
			System.err.println("Unable to create log!");
			System.err.println(e.getLocalizedMessage());			
		}
	}
	

	private Log(String path) { this(Level.INFO, path); }
	
	/**
	 * Write the given string to the log
	 * @param string the string to log
	 * @return the string written
	 */
	private String write(String string) {
		String buffer = time.format(new Date()) + string + "\n";
	
		try {
			FileWriter writer = new FileWriter(logFile, true);
			writer.write(buffer);
			writer.close();
		} catch (IOException ex) {
			System.err.println("Failed to write to log!");
		}
		
		return buffer;
	}
	
	public static void info(String zone, String message) { info("[" + zone + "] " + message); }
	public static void info(String toWrite) {
		if (Level.INFO.compareTo(getInstance().logLevel) <= 0) {
			System.out.print(getInstance().write(" INFO: " + toWrite));
		}
	}

	public static void debug(String zone, String message) { debug("[" + zone + "] " + message); }
	public static void debug(String toWrite) {
		if (Level.DEBUG.compareTo(getInstance().logLevel) <= 0) {
			System.out.print(getInstance().write("DEBUG: " + toWrite));
		}
	}
	
	public static void warn(String zone, String message) { warn("[" + zone + "] " + message); }
	public static void warn(String toWrite) {
		if (Level.WARN.compareTo(getInstance().logLevel) <= 0) {
			System.err.print(getInstance().write(" WARN: " + toWrite));
		}
	}
	
	public static void error(String zone, String message) { error("[" + zone + "] " + message); }
	public static void error(String toWrite) {
		if (Level.ERROR.compareTo(getInstance().logLevel) <= 0) {
			System.err.print(getInstance().write("ERROR: " + toWrite));
		}
	}

	public static void trace(String zone, String message) { trace("[" + zone + "] " + message); }
	public static void trace(String toWrite) {
		if (Level.TRACE.compareTo(getInstance().logLevel) <= 0) {
			System.out.print(getInstance().write("TRACE: " + toWrite));
		}
	}


	public static void setLevel(Level level) {
		getInstance().logLevel = level;
	}
}