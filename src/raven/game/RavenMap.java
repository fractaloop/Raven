package raven.game;

import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import raven.game.navigation.NavGraphNode;
import raven.game.triggers.Trigger;
import raven.game.triggers.TriggerSystem;
import raven.math.Vector2D;
import raven.math.Wall2D;
import raven.math.CellSpacePartition;

public class RavenMap {
	
	
	private ArrayList<Wall2D> walls;
	
	private TriggerSystem<Trigger<RavenBot>> triggerSystem;
	
	private ArrayList<Vector2D> spawnPoints;
	
	private ArrayList<RavenDoor> doors;
	
	private NavGraphNode<Trigger<RavenBot>> navGraph;
	
	private CellSpacePartition<NavGraphNode> spacePartition;
	
	double cellSpaceNeighborhoodRange;
	
	int sizeX;
	int sizeY;
	
	private void partitionNavGraph() {
		
	}
	
	private ArrayList<ArrayList<Double>> pathCosts;
	
	// Level manipulation
	
	private void addWall(Reader in) {
		
	}
	
	private void addSpawnPoint(Reader in) {
		
	}
	
	private void addHealthGiver(Reader in) {
		
	}
	
	private void addWeaponGiver(RavenObject typeOfWeapon, Reader in) {
		
	}
	
	private void addDoor(Reader in) {
		
	}
	
	private void addDoorTrigger(Reader in) {
		
	}
	
	public RavenMap() {
		
	}
	
	public boolean loadMap(String filename) {
		return false;
	}
	
	public Wall2D addWall(Vector2D from, Vector2D to) {
		return null;
	}
	
	public void addSoundTrigger(RavenBot soundSource, double range) {
		
	}
	
	public double calculateCostToTravelBetweenNodes(int node1, int node2) {
		return 0;
	}
	
	public Vector2D getRandomNodeLocation() {
		return null;
	}
	
	public void updateTriggerSystem() {
		
	}
	
	// Accessors
	
	public List<Trigger<RavenBot>> getTriggers() {
		return triggerSystem.getTriggers();
	}
	
	public List<Wall2D> getWalls() {
		return walls;
	}
	
	public NavGraphNode getNavGraph() {
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
		// TODO Auto-generated method stub
		
	}
	
}
