package raven.game;

import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;

import raven.game.messaging.Telegram;
import raven.goals.GoalThink;
import raven.math.Vector2D;

public class RavenBot extends MovingEntity {
	private enum Status {
		ALIVE,
		DEAD,
		SPAWNING
	}
	
	/** alive, dead or spawning? */
	private Status status;
	
	/** a pointer to the world data */
	private RavenGame world;
	
	/** this object handles the arbitration and processing of high level goals
	 */
	private GoalThink brain;
	
	/** this is a class that acts as the bots sensory memory. Whenever this
	 * bot sees or hears an opponent, a record of the event is updated in the
	 * memory. */
	private RavenSensoryMemory sensoryMem;
	
	/** the bot uses this object to steer */
	private RavenSteering steering;
	
	/** the bot uses this object to plan paths */
	private RavenPathPlanner pathPlanner;
	
	/** this is responsible for choosing the bot's current target */
	private RavenTargetingSystem targSys;
	
	/** this handles all the weapons. and has methods for aiming, selecting
	 * and shooting them */
	private RavenWeaponSystem weaponSys;
	
	/** A regulator object limits the update frequency of a specific AI
	 * component */
	private Regulator weaponSelectionRegulator;
	private Regulator goalArbitrationRegulator;
	private Regulator targetSelectionRegulator;
	private Regulator triggerTestRegulator;
	private Regulator visionUpdateRegulator;
	
	/** the bot's health. Every time the bot is shot this value is decreased.
	 * If it reaches zero then the bot dies (and respawns) */
	private int health;
	
	/** the bot's maximum health value. It starts its life with health at this
	 * value */
	private int maxHealth;
	
	/** each time this bot kills another this value is incremented */
	private int score;
	
	/** the direction the bot is facing (and therefore the direction of aim).
	 * Note that this may not be the same as the bot's heading, which always
	 * points in the direction of the bot's movement */
	private Vector2D facing;
	
	/** a bot only perceives other bots within this field of view */
	private double fieldOfView;
	
	/** to show that a player has been hit it is surrounded by a thick red
	 * circle for a fraction of a second. This variable represents
	 * the number of update-steps the circle gets drawn */
	private int numSecondsHitPersistant;
	
	/** set to true when the bot is hit, and remains true until
	 * numSecondsHitPersistant becomes zero. (used by the render method to
	 * draw a thick red circle around a bot to indicate it's been hit) */
	private boolean hit;

	/** set to true when a human player takes over control of the bot */
	private boolean possessed;
	
	/** a vertex buffer containing the bot's geometry */
	private ArrayList<Vector2D> vecBotVB;
	
	/** the buffer for the transformed vertices */
	private ArrayList<Vector2D> vecBotVBTrans;
	
	/** this method is called from the update method. It calculates and
	 * applies the steering force for this time-step. */
	private void updateMovement() {
		
	}
	
	/** initializes the bot's VB with its geometry */
	private void setUpVertexBuffer() {
		
	}
	
	//////////////////
	// Pulic methods
	
	public RavenBot(RavenGame world, Vector2D position) {
		// TODO Auto-generated constructor stub
	}

	// The usual suspects
	
	@Override
	public void render() {
		// TODO Auto-generated method stub
	}
	
	@Override
	public void update(double delta) {
		
	}
	
	@Override
	public boolean handleMessage(Telegram msg) {
		
	}
	
	@Override
	public void write(Writer writer) {
		
	}
	
	@Override
	public void read(Reader reader) {
		
	}
	
	/**
	 * this rotates the bot's heading until it is facing directly at the
	 * target position. Returns false if not facing at the target.
	 * @param target the target to face
	 * @return
	 */
	public boolean rotateFacingTowardPosition(Vector2D target) {
		
	}
	
	// Attribute access
	
	public int health() { return health; }
	public int maxHealth() { return maxHealth; }
	public void reduceHealth(int amount) {
		
	}
	public void increaseHealth(int amount) {
		
	}
	public void restoreHealthToMaximum() {
		
	}
	
	public int score() { return score; }
	public void incrementScore() { ++score; }
	
	public Vector2D facing() { return facing; }
	public double fieldOfView() { return fieldOfView; }
	
	public boolean isPossessed() { return possessed; }
	public boolean isDead() { return status == Status.DEAD; }
	public boolean isAlive() { return status == Status.ALIVE; }
	public boolean isSpawning() { return status == Status.SPAWNING; }
	
	public void setSpawning() { status = Status.SPAWNING; }
	public void setDead() { status = Status.DEAD; }
	public void setAlive() { status = Status.ALIVE; }
	
	/**
	 * returns a value indicating the time in seconds it will take the bot to
	 * reach the given position at its current speed.
	 * @param pos position to reach
	 * @return seconds until arrival
	 */
	public double calculateTimeToReachPosition(Vector2D pos) {
		
	}
	
	/**
	 * determines if the bot is close to the given location
	 * @param pos the position to check
	 * @return true if the bot is close
	 */
	public boolean isAtPosition(Vector2D pos) {
		
	}

	// Interface for human player
	
	public void fireWeapon(Vector2D pos) {
		
	}
	
	public void changeWeapon(RavenObject type) {
		
	}
	
	public void takePossession() {
		
	}
	
	public void exorcise() {
		
	}
	
	/** spawns the bot at the given position */
	public void spawn(Vector2D pos) {
		
	}
	
	/** returns true if this bot is ready to test against all triggers */
	public boolean isReadyForTriggerUpdate() {
		
	}
	
	/** returns true if the bot has line of sight to the given position. */
	public boolean hasLOSto(Vector2D pos) {
		
	}
	
	/** returns true if this bot can move directly to the given position
	 * without bumping into any walls */
	public boolean canWalkTo(Vector2D pos) {
		
	}

	/** similar to above. Returns true if the bot can move between the two
	 * given positions without bumping into any walls */
	public boolean canWalkBetween(Vector2D from, Vector2D to) {
		
	}
	
	// returns true if there is space enough to step in the indicated direction
	// If true PositionOfStep will be assigned the offset position
	public boolean canStepLeft(Vector2D positionOfStep) {
		
	}
	public boolean canStepRight(Vector2D positionOfStep) {
		
	}
	public boolean canStepForward(Vector2D positionOfStep) {
		
	}
	public boolean canStepBackward(Vector2D positionOfStep) {
		
	}
	
	// Generic accessors
	
	public RavenGame getWorld() { return world; }
	public RavenSteering getSteering() { return steering; }
	public RavenPathPlanner getPathPlanner() { return pathPlanner; }
	public GoalThink getBrain() { return brain; }
	public RavenTargetingSystem getTargetSys() { return targSys; }
	public RavenBot getTargetBot() { return targSys.getTarget(); }
	public RavenWeaponSystem getWeaponSys() { return weaponSys; }
	public RavenSensoryMemory getSensoryMem() { return sensoryMem; }
}
