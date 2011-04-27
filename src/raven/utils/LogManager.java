/**
 * 
 */
package raven.utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;


/**
 * @author Chet
 *
 */
public class LogManager {

	/**
	 * Ordered by escalating severity
	 */
	public enum LogLevel { INFO, DEBUG, WARN, ERROR }; 
	
	private static LogManager instance = new LogManager("raven.log");
	private static File logFile;
	public static LogManager GetInstance() {
		return instance;
	}
	
	private LogManager(String path) {
		logFile = new File(path);
		// Clear old file
		logFile.delete();
		try {
			logFile.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void Log(LogLevel level, String toWrite) {
		try {
			FileWriter writer = new FileWriter(logFile);
			writer.write(level + ":\t" + toWrite + "\n");
			writer.flush();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void Info(String toWrite) {
		try {
			FileWriter writer = new FileWriter(logFile);
			writer.write(LogLevel.INFO + ":\t" + toWrite + "\n");
			writer.flush();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void InfoException(String toWrite, Exception ex) {
		try {
			FileWriter writer = new FileWriter(logFile);
			writer.write(LogLevel.INFO + ":\t" + toWrite + "\n");
			writer.write(ex.getLocalizedMessage());
			writer.flush();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void Debug(String toWrite) {
		try {
			FileWriter writer = new FileWriter(logFile);
			writer.write(LogLevel.DEBUG + ":\t" + toWrite + "\n");
			writer.flush();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void DebugException(String toWrite, Exception ex) {
		try {
			FileWriter writer = new FileWriter(logFile);
			writer.write(LogLevel.DEBUG + ":\t" + toWrite + "\n");
			writer.write(ex.getLocalizedMessage());
			writer.flush();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void Warn(String toWrite) {
		try {
			FileWriter writer = new FileWriter(logFile);
			writer.write(LogLevel.WARN + ":\t" + toWrite + "\n");
			writer.flush();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void WarnException(String toWrite, Exception ex) {
		try {
			FileWriter writer = new FileWriter(logFile);
			writer.write(LogLevel.WARN + ":\t" + toWrite + "\n");
			writer.write(ex.getLocalizedMessage());
			writer.flush();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void Error(String toWrite) {
		try {
			FileWriter writer = new FileWriter(logFile);
			writer.write(LogLevel.WARN + ":\t" + toWrite + "\n");
			writer.flush();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void ErrorException(String toWrite, Exception ex) {
		try {
			FileWriter writer = new FileWriter(logFile);
			writer.write(LogLevel.WARN + ":\t" + toWrite + "\n");
			writer.write(ex.getLocalizedMessage());
			writer.flush();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
