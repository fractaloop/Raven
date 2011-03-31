package raven.script;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

public class GameScript {
	Map<String, Object> parameters = new HashMap<String, Object>();
	
	protected Object get(String key) {
		return parameters.get(key);
	}
	
	protected void put(String key, Object value) {
		parameters.put(key, value);
	}
	
	public void load(String filename) throws FileNotFoundException, ScriptException {
		// Get a JavaScript interpreter
		ScriptEngineManager manager = new ScriptEngineManager();
		ScriptEngine engine = manager.getEngineByExtension("js");
		
		engine.eval(new BufferedReader(new FileReader(filename)));
		
		// Deep copy the bindings into the parameters
		Bindings bindings = engine.getBindings(ScriptContext.ENGINE_SCOPE);
		for (String key : bindings.keySet()) {
			parameters.put(key, bindings.get(key));
		}
	}
}
