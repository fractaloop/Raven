/**
 * Static class that serializes and deserializes maps for our use.
 */
package raven.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import raven.game.RavenBot;
import raven.game.RavenDoor;
import raven.game.RavenMap;
import raven.game.navigation.NavGraphEdge;
import raven.game.navigation.NavGraphNode;
import raven.game.triggers.Trigger;
import raven.game.triggers.TriggerSystem;
import raven.math.CellSpacePartition;
import raven.math.Vector2D;
import raven.math.Wall2D;
import raven.math.graph.SparseGraph;

import com.thoughtworks.xstream.XStream;


/**
 * @author chester
 *
 */
public class MapSerializer {
	
	protected static XStream initXStream() {
		XStream streamer = new XStream();
		
		//setup aliases to prevent fully qualified autogens, this is a purely cosmetic change
		streamer.alias("RavenMap", RavenMap.class);
		streamer.alias("Wall2D", Wall2D.class);
		streamer.alias("RavenBot", RavenBot.class);
		streamer.alias("Vector2D", Vector2D.class);
		streamer.alias("RavenDoor", RavenDoor.class);
		streamer.alias("SparseGraph", SparseGraph.class);
		streamer.alias("CellSpacePartition", CellSpacePartition.class);
		streamer.alias("Trigger", Trigger.class);
		streamer.alias("TriggerSystem", TriggerSystem.class);
		streamer.alias("NavGraphNode", NavGraphNode.class);
		streamer.alias("NavGraphEdge", NavGraphEdge.class);
		
		return streamer;
	}
	
	public static String serializeMap(RavenMap map) {
		return initXStream().toXML(map);
	}
	
	public static boolean serializeMapToFile(RavenMap map, File file) throws IOException {
		FileWriter writer = new FileWriter(file);
		writer.write(initXStream().toXML(map));
		writer.close();
		return true;
	}
	
	public static boolean serializeMapToPath(RavenMap map, String filePath) throws IOException {
		FileWriter writer = new FileWriter(filePath);
		writer.write(initXStream().toXML(map));
		writer.close();
		return true;
	}
	
	public static RavenMap deserializeMapFromXML(String xml){
		return (RavenMap) initXStream().fromXML(xml);
	}
	
	public static RavenMap deserializeMapFromFile(File file) throws IOException {
		FileReader reader = new FileReader(file);
		RavenMap result = (RavenMap)initXStream().fromXML(reader);
		reader.close();
		return result;
	}
	
	public static RavenMap deserializeMapFromPath(String filePath) throws IOException {
		FileReader reader = new FileReader(filePath);
		RavenMap result = (RavenMap)initXStream().fromXML(reader);
		reader.close();
		return result;
	}
}
