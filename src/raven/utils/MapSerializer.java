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

	
	private static XStream streamer = new XStream();
	private static MapSerializer instance;
	
	public MapSerializer(){
		
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
		
	}
	
	public static String DeserializeMap(RavenMap map){
		CheckInstance();
		return streamer.toXML(map);
	}
	
	public static boolean DeserializeMapToFile(RavenMap map, File file) throws IOException{
		FileWriter writer = new FileWriter(file);
		writer.write(streamer.toXML(map));
		return true;
	}
	
	public static boolean DeserializeMapToPath(RavenMap map, String filePath) throws IOException{
		FileWriter writer = new FileWriter(filePath);
		writer.write(streamer.toXML(map));
		return true;
	}
	
	public static RavenMap SerializeMapFromXML(String xml){
		return (RavenMap) streamer.fromXML(xml);
	}
	
	public static RavenMap SerializeMapFromFile(File file) throws FileNotFoundException{
		FileReader reader = new FileReader(file);
		return (RavenMap)streamer.fromXML(reader);
	}
	
	public static RavenMap SerializeMapFromPath(String filePath) throws FileNotFoundException{
		FileReader reader = new FileReader(filePath);
		return (RavenMap)streamer.fromXML(reader);
	}
	
	private static void CheckInstance(){
		if(instance == null){
			instance = new MapSerializer();
		}
	}
}
