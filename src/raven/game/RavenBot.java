package raven.game;

import java.util.ArrayList;

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
	
	
	
	public RavenBot(RavenGame world, Vector2D position) {
		super(position, botScale, new Vector2D(0, 0),
				botMaxSpeed, new Vector2D(1, 0), 
				botMass, new Vector2D(botScale, botScale),
				botMaxHeadTurnRate, botMaxForce, world);
		// TODO Auto-generated constructor stub
	}

	public void increaseHealth(int healthGiven) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void render() {
		// TODO Auto-generated method stub
		
	}

}
