package raven.game;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.management.RuntimeErrorException;

import raven.game.navigation.NavGraphEdge;
import raven.game.navigation.NavGraphNode;
import raven.game.triggers.Trigger;
import raven.game.triggers.TriggerHealthGiver;
import raven.game.triggers.TriggerSoundNotify;
import raven.game.triggers.TriggerSystem;
import raven.game.triggers.TriggerWeaponGiver;
import raven.math.CellSpacePartition;
import raven.math.Vector2D;
import raven.math.Wall2D;
import raven.math.graph.GraphNode;
import raven.math.graph.SparseGraph;
import raven.script.RavenScript;
import raven.ui.GameCanvas;
import raven.utils.Pair;
import raven.utils.StreamUtils;

public class RavenMap {
	
	/** the walls that comprise the current map's architecture. */
	private ArrayList<Wall2D> walls;
	
	/** trigger are objects that define a region of space. When a raven bot
	 * enters that area, it 'triggers' an event. That event may be anything
	 * from increasing a bot's health to opening a door or requesting a lift.
	 */
	private TriggerSystem<Trigger<RavenBot>> triggerSystem;
	
	/** this holds a number of spawn positions. When a bot is instantiated it
	 * will appear at a randomly selected point chosen from this vector */
	private ArrayList<Vector2D> spawnPoints;
	
	/** a map may contain a number of sliding doors. */
	private ArrayList<RavenDoor> doors;
	
	/** this map's accompanying navigation graph */
	private SparseGraph<NavGraphNode<Trigger<RavenBot>>, NavGraphEdge> navGraph;
	
	/** the graph nodes will be partitioned enabling fast lookup */
	private CellSpacePartition<NavGraphNode<Trigger<RavenBot>>> spacePartition;
	
	/** the size of the search radius the cellspace partition uses when
	 * looking for neighbors */
	double cellSpaceNeighborhoodRange;
	
	int sizeX;
	int sizeY;
	
	private void partitionNavGraph() {
		spacePartition = new CellSpacePartition<NavGraphNode<Trigger<RavenBot>>>(sizeX, sizeY,
				RavenScript.getInt("NumCellX"), RavenScript.getInt("NumCellsY"),
				navGraph.numNodes());
		
		// add the graph nodes to the space partition
		for (NavGraphNode<Trigger<RavenBot>> node : navGraph) {
			spacePartition.addEntity(node);
		}
	}
	
	/* this will hold a pre-calculated lookup table of the cost to travel
	 * from */
	private Map<Pair<Integer, Integer>, Double> pathCosts;
	

	// stream constructors for loading from a file
	private void addWall(Reader in) throws IOException {
		walls.add(new Wall2D(in));
	}
	
	private void addSpawnPoint(Reader reader) {
		double x, y, dummy;
		
		dummy = (Double)StreamUtils.getValueFromStream(reader);
		x = (Double)StreamUtils.getValueFromStream(reader);
		y = (Double)StreamUtils.getValueFromStream(reader);
		dummy = (Double)StreamUtils.getValueFromStream(reader);
		dummy = (Double)StreamUtils.getValueFromStream(reader);
	}
	
	private void addHealthGiver(Reader reader) {
		TriggerHealthGiver healthGiver = new TriggerHealthGiver(reader);
		
		triggerSystem.register(healthGiver);
		
		// Let the corresponding NavGraphNode point to this object
		NavGraphNode<Trigger<RavenBot>> node = navGraph.getNode(healthGiver.graphNodeIndex());
		node.setExtraInfo(healthGiver);
		
		// register the entity
		EntityManager.registerEntity(healthGiver);
	}
	
	private void addWeaponGiver(RavenObject typeOfWeapon, Reader reader) {
		TriggerWeaponGiver weaponGiver = new TriggerWeaponGiver(reader);
		
		weaponGiver.setEntityType(typeOfWeapon);
		
		// add it to the appropriate vectors
		triggerSystem.register(weaponGiver);
		
		// let the corresponding navgraph node point to this object
		NavGraphNode<Trigger<RavenBot>> node = navGraph.getNode(weaponGiver.graphNodeIndex());
		
		node.setExtraInfo(weaponGiver);
	}
	
	private void addDoor(Reader reader) {
		RavenDoor door = new RavenDoor(this, reader);
		
		doors.add(door);
		
		// register the entity
		EntityManager.registerEntity(door);
	}
	
	private void addDoorTrigger(Reader reader) {
		TriggerOnBottonSendMsg<RavenBot> trigger = new TriggerOnButtonSendMsg<RavenBot>(reader);
		
		triggerSystem.register(trigger);
		
		// register the entity
		EntityManager.registerEntity(trigger);
	}
	
	public void clear() {
		// delete the triggers
		triggerSystem.clear();
		
		// delete the doors
		doors.clear();
		
		walls.clear();
		
		spawnPoints.clear();
		
		// delete the navgraph
		navGraph = null;
		
		// delete the partition info
		spacePartition = null;
	}
	
	public RavenMap() {
		sizeX = sizeY = 0;
		
		cellSpaceNeighborhoodRange = 0.0;
	}
	
	/**
	 * loads an environment from a file
	 * @param filename the filename to load
	 * @return true only if the file loaded successfully
	 * @throws IOException 
	 */
	public boolean loadMap(String filename) throws IOException {
		FileReader reader = new FileReader(filename);
		BufferedReader buffered = new BufferedReader(reader);
		
		clear();
		
		BaseGameEntity.resetNextValidID();
		
		// first of all read and create the navgraph. This must be done before
		// the entities are read from the map file because many of the
		// entities will be linked to a graph node (the graph node will own a
		// pointer to an instance of the entity)
		navGraph = new SparseGraph<NavGraphNode<Trigger<RavenBot>>, NavGraphEdge>(false, new NavGraphNode<Trigger<RavenBot>>(), new NavGraphEdge());
		
		navGraph.load(reader);
		
		// determine the average distance between graph nodes so that we can
		// partition them efficiently
		cellSpaceNeighborhoodRange = navGraph.calculateAverageGraphEdgeLength() + 1;
		
		// load in the map size and adjust the client window accordingly
		sizeX = (Integer)StreamUtils.getValueFromStream(reader);
		sizeY = (Integer)StreamUtils.getValueFromStream(reader);
		
		// partition the graph nodes
		partitionNavGraph();
		
		// now create the environment entities
		while (reader.ready()) {
			// get the type of next map object
			int entityType = (Integer)StreamUtils.getValueFromStream(reader);
			
			// create the object
			
			switch (RavenObject.resolveType(entityType)) {
			case WALL:
				addWall(reader);
				break;
			case SLIDING_DOOR:
				addDoor(reader);
				break;
			case DOOR_TRIGGER:
				addDoorTrigger(reader);
				break;
			case SPAWN_POINT:
				addSpawnPoint(reader);
				break;
			case HEALTH:
				addHealthGiver(reader);
				break;
			case SHOTGUN:
				addWeaponGiver(RavenObject.SHOTGUN, reader);
				break;
			case RAIL_GUN:
				addWeaponGiver(RavenObject.RAIL_GUN, reader);
				break;
			case ROCKET_LAUNCHER:
				addWeaponGiver(RavenObject.ROCKET_LAUNCHER, reader);
				break;
			default:
				throw new RuntimeErrorException(new Error("Map error: Attempted to load an undefined object"));
			}
		}
		
		pathCosts = navGraph.createAllPairsCostsTable();
		
		return true;
	}
	
	/**
	 * adds a wall and returns a pointer to that wall. (this method can be
	 * used by objects such as doors to add walls to the environment)
	 * @param from wall's starting point
	 * @param to wall's ending point
	 * @return the new wall created
	 */
	public Wall2D addWall(Vector2D from, Vector2D to) {
		Wall2D wall = new Wall2D(from, to);
		walls.add(wall);
		return wall;
	}
	
	public void addSoundTrigger(RavenBot soundSource, double range) {
		triggerSystem.register(new TriggerSoundNotify(soundSource, range));
	}
	
	public double calculateCostToTravelBetweenNodes(int node1, int node2) {
		if (node1 < 0 || node2 < 0 || node1 >= navGraph.numNodes() || node2 >= navGraph.numNodes())
			throw new IndexOutOfBoundsException("Invalid node index: " + node1 + " to " + node2);
		
		return pathCosts.get(new Pair<Integer,Integer>(node1, node2));
	}
	
	/** returns the position of a graph node selected at random */
	public Vector2D getRandomNodeLocation() {
		int randIndex = (int)(Math.random() * navGraph.numActiveNodes());
		
		GraphNode node = null;
		for (int i = 0; i < navGraph.numNodes(); i++) {
			node = navGraph.getNode(i);
			if (node.index() != GraphNode.INVALID_NODE_INDEX) {
				randIndex--;
			}
			if (randIndex < 0) {
				break;
			}
		}
		
		return node.pos();
		
	}
	
	public void updateTriggerSystem(double delta, List<RavenBot> bots) {
		triggerSystem.update(delta, bots);
	}
	
	// Accessors
	
	public List<Trigger<RavenBot>> getTriggers() {
		return triggerSystem.getTriggers();
	}
	
	public List<Wall2D> getWalls() {
		return walls;
	}
	
	public SparseGraph<NavGraphNode<Trigger<RavenBot>>, NavGraphEdge> getNavGraph() {
		return navGraph;
	}
	
	public List<RavenDoor> getDoors() {
		return doors;
	}
	
	public List<Vector2D> getSpawnPoints() {
		return spawnPoints;
	}
	
	public CellSpacePartition getCellSpace() {
		return spacePartition;
	}
	
	public Vector2D getRandomSpawnPoint() {
		return spawnPoints.get((int)(Math.random() * spawnPoints.size()));
	}
	
	public int getSizeX() { 
		return sizeX;
	}
	
	public int getSizeY() {
		return sizeY;
	}
	
	public int getMaxDimension() { 
		return Math.max(sizeX, sizeY);
	}
	
	public double getCellSpaceNeighborhoodRange() {
		return cellSpaceNeighborhoodRange;
	}

	public void render() {
		// render the navgraph
		if (RavenUserOptions.showGraph) {
//			navGraph.render(RavenUserOptions.showNodeIndices);
		}
		
		// render any doors
		for (RavenDoor door : doors) {
			door.render();
		}
		
		// render all triggers
		triggerSystem.render();
		
		// render all walls
		for (Wall2D wall : walls) {
			GameCanvas.thickBlackPen();
			wall.render();
		}
		
		// render spawn points
		for (Vector2D point : spawnPoints) {
			GameCanvas.greyBrush();
			GameCanvas.greyPen();
			GameCanvas.circle(point, 7);
		}
		
	}
	
}
