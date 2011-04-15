package raven.script;

import java.io.FileNotFoundException;

import javax.script.ScriptException;

public class RavenScript extends GameScript {
	private static class RavenScriptHolder {
		public static final RavenScript INSTANCE = new RavenScript();
	}

	public static RavenScript getInstance() {
		return RavenScriptHolder.INSTANCE;
	}
	
	private RavenScript() {
		try {
			load("params.js");
		} catch (FileNotFoundException e) {
			System.err.println("Unable to find params.js! Exiting...");
			System.exit(1);
		} catch (ScriptException e) {
			System.err.println("Unable to load params.js! Reason:");
			System.err.println(e.getLocalizedMessage());
			System.exit(1);
		}
		System.out.println("Loaded parameters.");
	}
	
	public static String getString(String name) {
		return getInstance().get(name).toString();
	}

	public static int getInt(String name) {
		Object result = getInstance().get(name);
		if (result == null)
			throw new RavenScriptException("Unable to find script parameter \"" + name + "\"");

		return ((Number)result).intValue();
	}

	public static double getDouble(String name) {
		Object result = getInstance().get(name);
		if (result == null)
			throw new RavenScriptException("Unable to find script parameter \"" + name + "\"");

		return ((Number)result).doubleValue();
	}
}
