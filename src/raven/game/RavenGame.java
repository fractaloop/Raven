package raven.game;

import java.util.*;

import raven.game.armory.RavenProjectile;
import raven.game.navigation.PathManager;
import raven.game.navigation.RavenPathPlanner;
import raven.math.*;

public class RavenGame {
	/** the current game map */
	private RavenMap map;
	
	/** bots that inhabit the current map */
	private ArrayList<RavenBot> bots;
	
	/** A user may control a bot manually. This is that bot */
	private RavenBot selectedBot;
	
	/** contains any active projectiles (slugs, rockets, shotgun pellets, etc) */
	private ArrayList<RavenProjectile> projectiles;
	
	/** manages all the path planning requests */
	PathManager<RavenPathPlanner> pathManager;
	
	/** true if the game is paused */
	boolean paused;
	
	/** true if a bot is removed from the game */
	boolean removeBot;
	
	/** When a bot is killed a "grave" is displayed for a few seconds. This
	 * class manages the graves. */
	GraveMarkers graveMarkers;
	
	private void updateTriggers() {
		
	}
	
	private boolean attemptToAddBot(RavenBot bot) {
		return false;
	}
	
	private void notifyAllBotsOfRemoval(RavenBot bot) {
		
	}
	
	///////////////////
	// Public methods
	
	public RavenGame() {
		
	}
	
	/** The usual suspects */
	public void render() {
		
	}
	
	public void update() {
		
	}
	
	/** Loads an environment from a file */
	public boolean loadMap(String fileName) {
		return false;
	}
	
	public void addBots(int numBotsToAdd) {
		
	}
	
	public void addRocket(RavenBot shooter, Vector2D target) {
		
	}
	
	public void addRailGunSlug(RavenBot shooter, Vector2D target) {
		
	}
	
	public void addShotGunPellet(RavenBot shooter, Vector2D target) {
		
	}
	
	public void addBolt(RavenBot shooter, Vector2D target) {
		
	}
	
	/** removes the last bot to be added */
	public void removeBot() {
		
	}
	
	/** returns true if a bot of size BoundingRadius cannot move from A to B
	 *  without bumping into world geometry */
	public boolean isPathObstructed(Vector2D a, Vector2D b, double boundingRadius) {
		return false;
	}
	
	/** returns of bots in the FOV of the given bot */
	public List<RavenBot> getAllBotsInFOV(final RavenBot bot) {
		return Collections.emptyList();
	}
	
	/** returns true if the second bot is unobstructed by walls and in the field
	 * of view of the first. */
	public boolean isSecondVisibleToFirst(final RavenBot first, final RavenBot second) {
		return false;
	}
	
	/** returns true if the ray between A and B is unobstructed. */
	public boolean isLOSOkay(final Vector2D A, final Vector2D B) {
		return false;		
	}
	
	/** starting from the given origin and moving in the direction Heading this
	 * method returns the distance to the closest wall */
	public double getDistanceToClosestWall(Vector2D origin, Vector2D heading) {
		return 0;
	}
	
	/** returns the position of the closest visible switch that triggers the
	 * door of the specified ID */
	public Vector2D getPosOfClosestSwitch(Vector2D botPos, int doorID) {
		return null;
	}
	/**
	 * given a position on the map this method returns the bot found with its
	 * bounding radius of that position.If there is no bot at the position the
	 * method returns null
	 * 
	 * @param cursorPos
	 * @return
	 */
	public RavenBot getBotAtPosition(Vector2D cursorPos) {
		return null;
	}
	
	public void togglePause() {
		paused = !paused;
	}
	
	/**
	 * this method is called when the user clicks the right mouse button.
	 * The method checks to see if a bot is beneath the cursor. If so, the bot
	 * is recorded as selected.If the cursor is not over a bot then any selected
	 * bot/s will attempt to move to that position.
	 * @param p the location clicked
	 */
	public void clickRightMouseButton(Vector2D p) {
		
	}
	
	/**
	 * this method is called when the user clicks the left mouse button. If there
	 * is a possessed bot, this fires the weapon, else does nothing
	 * @param p the location clicked
	 */
	public void clickLeftMouseButton(Vector2D p) {
		
	}
	
	/** when called will release any possessed bot from user control */
	public void exorciseAnyPossessedBot() {
		
	}
	
	/** if a bot is possessed the keyboard is polled for user input and any 
	 * relevant bot methods are called appropriately */
	public void getPlayerInput() {
		
	}
	public RavenBot possessedBot() { return selectedBot; }
	public void changeWeaponOfPossessedBot(int weapon) {
		
	}

	//////////////
	// Accessors
	
	public RavenMap getMap() {
		return map;
	}

	public ArrayList<RavenBot> getBots() {
		return bots;
	}

	public PathManager<RavenPathPlanner> getPathManager() {
		return pathManager;
	}
	
	public int getNumBots() {
		return bots.size();
	}
	
}
