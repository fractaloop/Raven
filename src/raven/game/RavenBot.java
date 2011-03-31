package raven.game;

import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;

import raven.game.messaging.Dispatcher;
import raven.game.messaging.RavenMessage;
import raven.game.messaging.Telegram;
import raven.game.navigation.RavenPathPlanner;
import raven.goals.GoalThink;
import raven.math.C2DMatrix;
import raven.math.Transformations;
import raven.math.Vector2D;
import raven.script.RavenScript;
import raven.ui.GameCanvas;
import raven.utils.Regulator;

public class RavenBot extends MovingEntity {
	private enum Status {
		ALIVE, DEAD, SPAWNING
	}

	/** alive, dead or spawning? */
	private Status status;

	/** a pointer to the world data */
	private RavenGame world;

	/**
	 * this object handles the arbitration and processing of high level goals
	 */
	private GoalThink brain;

	/**
	 * this is a class that acts as the bots sensory memory. Whenever this bot
	 * sees or hears an opponent, a record of the event is updated in the
	 * memory.
	 */
	private RavenSensoryMemory sensoryMem;

	/** the bot uses this object to steer */
	private RavenSteering steering;

	/** the bot uses this object to plan paths */
	private RavenPathPlanner pathPlanner;

	/** this is responsible for choosing the bot's current target */
	private RavenTargetingSystem targSys;

	/**
	 * this handles all the weapons. and has methods for aiming, selecting and
	 * shooting them
	 */
	private RavenWeaponSystem weaponSys;

	/**
	 * A regulator object limits the update frequency of a specific AI component
	 */
	private Regulator weaponSelectionRegulator;
	private Regulator goalArbitrationRegulator;
	private Regulator targetSelectionRegulator;
	private Regulator triggerTestRegulator;
	private Regulator visionUpdateRegulator;

	/**
	 * the bot's health. Every time the bot is shot this value is decreased. If
	 * it reaches zero then the bot dies (and respawns)
	 */
	private int health;

	/**
	 * the bot's maximum health value. It starts its life with health at this
	 * value
	 */
	private int maxHealth;

	/** each time this bot kills another this value is incremented */
	private int score;

	/**
	 * the direction the bot is facing (and therefore the direction of aim).
	 * Note that this may not be the same as the bot's heading, which always
	 * points in the direction of the bot's movement
	 */
	private Vector2D facing;

	/** a bot only perceives other bots within this field of view */
	private double fieldOfView;

	/**
	 * to show that a player has been hit it is surrounded by a thick red circle
	 * for a fraction of a second. This variable represents the number of
	 * update-steps the circle gets drawn
	 */
	private double numSecondsHitPersistant;

	/**
	 * set to true when the bot is hit, and remains true until
	 * numSecondsHitPersistant becomes zero. (used by the render method to draw
	 * a thick red circle around a bot to indicate it's been hit)
	 */
	private boolean hit;

	/** set to true when a human player takes over control of the bot */
	private boolean possessed;

	/** a vertex buffer containing the bot's geometry */
	private ArrayList<Vector2D> vecBotVB;

	/** the buffer for the transformed vertices */
	private ArrayList<Vector2D> vecBotVBTrans;

	/**
	 * this method is called from the update method. It calculates and applies
	 * the steering force for this time-step.
	 */
	private void updateMovement() {

	}

	/** initializes the bot's VB with its geometry */
	private void setUpVertexBuffer() {

	}

	// ////////////////
	// Pulic methods

	public RavenBot(RavenGame world, Vector2D position) {
		super(position, RavenScript.getDouble("Bot_Scale"), new Vector2D(0, 0),
				RavenScript.getDouble("Bot_MaxSpeed"), new Vector2D(1, 0),
				RavenScript.getDouble("Bot_Mass"), new Vector2D(
						RavenScript.getDouble("Bot_Scale"),
						RavenScript.getDouble("Bot_Scale")), RavenScript
						.getDouble("Bot_MaxHeadTurnRate"), RavenScript
						.getDouble("Bot_MaxForce"));
		maxHealth = RavenScript.getInt("Bot_MaxHealth");
		health = RavenScript.getInt("Bot_MaxHealth");
		this.world = world;
		numSecondsHitPersistant = RavenScript.getDouble("HitFlashTime");
		hit = false;
		score = 0;
		status = Status.SPAWNING;
		possessed = false;
		fieldOfView = Math.toRadians(RavenScript.getDouble("Bot_FOV"));

		setEntityType(RavenObject.BOT);

		setUpVertexBuffer();

		// a bot starts off facing in the direction it is heading
		facing = heading;

		// create the navigation module
		pathPlanner = new RavenPathPlanner(this);

		// create the steering behavior class
		steering = new RavenSteering(world, this);

		// create the regulators
		weaponSelectionRegulator = new Regulator(
				RavenScript.getDouble("Bot_WeaponSelectionFrequency"));
		goalArbitrationRegulator = new Regulator(
				RavenScript.getDouble("Bot_GoalAppraisalUpdateFreq"));
		targetSelectionRegulator = new Regulator(
				RavenScript.getDouble("Bot_TargetingUpdateFreq"));
		triggerTestRegulator = new Regulator(
				RavenScript.getDouble("Bot_TriggerUpdateFreq"));
		visionUpdateRegulator = new Regulator(
				RavenScript.getDouble("Bot_VisionUpdateFreq"));

		// create the goal queue
		brain = new GoalThink(this);

		// create the targeting system
		targSys = new RavenTargetingSystem(this);

		weaponSys = new RavenWeaponSystem(this,
				RavenScript.getDouble("Bot_ReactionTime"),
				RavenScript.getDouble("Bot_AimAccuracy"),
				RavenScript.getDouble("Bot_AimPersistance"));

		sensoryMem = new RavenSensoryMemory(this,
				RavenScript.getDouble("Bot_MemorySpan"));
	}

	// The usual suspects

	@Override
	public void render() {
		// when a bot is hit by a projectile this value is set to a constant
		// user defined value which dictates how long the bot should have a
		// thick red circle drawn around it (to indicate it's been hit) The
		// circle is drawn as long as this value is positive. (see Render)

		if (isDead() || isSpawning()) {
			return;
		}

		GameCanvas.bluePen();

		vecBotVBTrans = new ArrayList<Vector2D>(Transformations.WorldTransform(
				vecBotVB, pos(), facing(), facing().perp(), scale()));

		GameCanvas.closedShape(vecBotVBTrans);

		// draw the head
		GameCanvas.brownBrush();
		GameCanvas.circle(pos(), 6.0 * scale().x);

		// render the bot's weapon
		weaponSys.renderCurrentWeapon();

		// render a thick red circle if the bot gets hit by a weapon
		if (hit) {
			GameCanvas.thickRedPen();
			GameCanvas.hollowBrush();
			GameCanvas.circle(position, boundingRadius + 1);

			if (numSecondsHitPersistant <= 0) {
				hit = false;
			}
		}

		GameCanvas.redPen();

		if (RavenUserOptions.showBotIDs) {
			GameCanvas.textAtPos(pos().x - 10, pos().y - 20,
					Integer.toString(ID()));
		}

		if (RavenUserOptions.showBotHealth) {
			GameCanvas.textAtPos(pos().x - 40, pos().y - 5,
					"H:" + Integer.toString(health()));
		}

		if (RavenUserOptions.showScore) {
			GameCanvas.textAtPos(pos().x - 40, pos().y + 10,
					"Scr:" + Integer.toString(score()));
		}
	}

	@Override
	public void update(double delta) {
		// Moved from render() since this is time dependent!
		numSecondsHitPersistant -= delta;

		// process the currently active goal. Note this is required even if
		// the bot is under user control. This is because a goal is created
		// whenever a user clicks on an area of the map that necessitates a
		// path planning request.
		brain.process();

		// Calculate the steering force and update the bot's velocity and
		// position
		updateMovement();

		// if the bot is under AI control but not scripted
		if (!isPossessed()) {
			// examine all the opponents in the bots sensory memory and select
			// one to be the current target
			if (targetSelectionRegulator.isReady()) {
				targSys.update();
			}

			// appraise and arbitrate between all possible high level goals
			if (goalArbitrationRegulator.isReady()) {
				brain.arbitrate();
			}

			// update the sensory memory with any visual stimulus
			if (visionUpdateRegulator.isReady()) {
				sensoryMem.updateVision();
			}

			// select the appropriate weapon to use from the weapons currently
			// in the inventory
			if (weaponSelectionRegulator.isReady()) {
				weaponSys.selectWeapon();
			}

			// this method aims the bot's current weapon at the current target
			// and takes a shot if a shot is possible
			weaponSys.takeAimAndShoot();
		}

	}

	@Override
	public boolean handleMessage(Telegram msg) {
		// first see if the current goal accepts the message
		if (getBrain().handleMessage(msg)) {
			return true;
		}

		// handle any messages not handles by the goals
		switch (msg.msg) {
		case MSG_TAKE_THAT_MF:
			// just return if already dead or spawning
			if (isDead() || isSpawning()) {
				return true;
			}

			// the extra info field of the telegram carries the amount of dmg
			reduceHealth((Integer) msg.extraInfo);

			// if this bot is now dead let the shooter know
			if (isDead()) {
				Dispatcher.dispatchMsg(Dispatcher.SEND_MSG_IMMEDIATELY, ID(),
						msg.senderID, RavenMessage.MSG_YOU_GOT_ME_YOU_SOB,
						Dispatcher.NO_ADDITIONAL_INFO);
			}

			return true;
		
		default:
			return false;
		}
	}

	@Override
	public void write(Writer writer) {
		// TODO I hate these
	}

	@Override
	public void read(Reader reader) {
		// TODO I hate these
	}

	/**
	 * this rotates the bot's heading until it is facing directly at the target
	 * position. Returns false if not facing at the target.
	 * 
	 * @param target
	 *            the target to face
	 * @return
	 */
	public boolean rotateFacingTowardPosition(Vector2D target) {
		Vector2D toTarget = target.sub(position);
		toTarget.normalize();

		double dot = facing.dot(toTarget);

		// Clamp to rectify any rounding errors
		dot = Math.min(1, Math.max(-1, dot));
		
		// determine the angle between the heading vector and the target
		double angle = Math.acos(dot);
		
		// return true if the bot's facing is within WeaponAimTolerance degs
		// of facing the target
		final double weaponAimTolerance = 0.01; // approx 2 degrees
		
		if (angle < weaponAimTolerance) {
			facing = toTarget;
			return true;
		}
		
		// clamp the amount to turn to the max turn rate
		if (angle > maxTurnRate) {
			angle = maxTurnRate;
		}
		
		// The next few lines use a rotation matrix to rotate the player's
		// facing vector accordingly
		C2DMatrix rotationMatrix = new C2DMatrix();
		
		// notice how the direction of rotation has to be determined when creating
		// the rotation matrix
		rotationMatrix.rotate(angle * facing.sign(toTarget));
		rotationMatrix.transformVector2Ds(facing);
		
		return false;
	}

	// Attribute access

	public int health() {
		return health;
	}

	public int maxHealth() {
		return maxHealth;
	}

	public void reduceHealth(int amount) {
		health -= amount;
		
		if (health <= 0) {
			setDead();
		}
		
		hit = true;
		
		numSecondsHitPersistant = RavenScript.getDouble("HitFlashTime");
	}

	public void increaseHealth(int amount) {
		health += amount;
		health = Math.min(Math.max(0, health), maxHealth);
	}

	public void restoreHealthToMaximum() { health = maxHealth; }

	public int score() {
		return score;
	}

	public void incrementScore() {
		++score;
	}

	public Vector2D facing() {
		return facing;
	}

	public double fieldOfView() {
		return fieldOfView;
	}

	public boolean isPossessed() {
		return possessed;
	}

	public boolean isDead() {
		return status == Status.DEAD;
	}

	public boolean isAlive() {
		return status == Status.ALIVE;
	}

	public boolean isSpawning() {
		return status == Status.SPAWNING;
	}

	public void setSpawning() {
		status = Status.SPAWNING;
	}

	public void setDead() {
		status = Status.DEAD;
	}

	public void setAlive() {
		status = Status.ALIVE;
	}

	/**
	 * returns a value indicating the time in seconds it will take the bot to
	 * reach the given position at its current speed.
	 * 
	 * @param pos
	 *            position to reach
	 * @return seconds until arrival
	 */
	public double calculateTimeToReachPosition(Vector2D pos) {
		return position.distance(pos) / maxSpeed;
	}

	/**
	 * determines if the bot is close to the given location
	 * 
	 * @param pos
	 *            the position to check
	 * @return true if the bot is close
	 */
	public boolean isAtPosition(Vector2D pos) {
		final double tolerance = 10.0;
		
		return position.distanceSq(pos) < tolerance * tolerance;
	}

	// Interface for human player

	public void fireWeapon(Vector2D pos) {
		weaponSys.shootAt(pos);
	}

	public void changeWeapon(RavenObject type) {
		weaponSys.changeWeapon(type);
	}

	public void takePossession() {
		if (!(isSpawning() || isDead())) {
			possessed = true;
		}
	}

	public void exorcise() {
		possessed = false;
		
		brain.addGoal_Explore();
	}

	/** spawns the bot at the given position */
	public void spawn(Vector2D pos) {
		setAlive();
		brain.removeAllSubgoals();
		targSys.clearTarget();
		setPos(pos);
		weaponSys.initialize();
		restoreHealthToMaximum();
	}

	/** returns true if this bot is ready to test against all triggers */
	public boolean isReadyForTriggerUpdate() {
		return triggerTestRegulator.isReady();
	}

	/** returns true if the bot has line of sight to the given position. */
	public boolean hasLOSto(Vector2D pos) {
		return world.isLOSOkay(pos(), pos);
	}

	/**
	 * returns true if this bot can move directly to the given position without
	 * bumping into any walls
	 */
	public boolean canWalkTo(Vector2D pos) {
		return !world.isPathObstructed(pos(), pos, getBRadius());
	}

	/**
	 * similar to above. Returns true if the bot can move between the two given
	 * positions without bumping into any walls
	 */
	public boolean canWalkBetween(Vector2D from, Vector2D to) {
		return !world.isPathObstructed(from, to, getBRadius());
	}

	// returns true if there is space enough to step in the indicated direction
	// If true PositionOfStep will be assigned the offset position
	public boolean canStepLeft(Vector2D positionOfStep) {
		final double stepDistance = getBRadius() * 2;
		
		positionOfStep = pos().sub(facing().perp().mul(stepDistance)).sub(facing().perp().mul(getBRadius()));
		
		return canWalkTo(positionOfStep);
	}

	public boolean canStepRight(Vector2D positionOfStep) {
		final double stepDistance = getBRadius() * 2;
		
		positionOfStep = pos().add(facing().perp().mul(stepDistance)).add(facing().perp().mul(getBRadius()));
		
		return canWalkTo(positionOfStep);
	}

	public boolean canStepForward(Vector2D positionOfStep) {
		final double stepDistance = getBRadius() * 2;
		
		positionOfStep = pos().add(facing().mul(stepDistance)).add(facing().mul(getBRadius()));
		
		return canWalkTo(positionOfStep);
	}

	public boolean canStepBackward(Vector2D positionOfStep) {
		final double stepDistance = getBRadius() * 2;
		
		positionOfStep = pos().sub(facing().mul(stepDistance)).sub(facing().mul(getBRadius()));
		
		return canWalkTo(positionOfStep);
	}

	// Generic accessors

	public RavenGame getWorld() {
		return world;
	}

	public RavenSteering getSteering() {
		return steering;
	}

	public RavenPathPlanner getPathPlanner() {
		return pathPlanner;
	}

	public GoalThink getBrain() {
		return brain;
	}

	public RavenTargetingSystem getTargetSys() {
		return targSys;
	}

	public RavenBot getTargetBot() {
		return targSys.getTarget();
	}

	public RavenWeaponSystem getWeaponSys() {
		return weaponSys;
	}

	public RavenSensoryMemory getSensoryMem() {
		return sensoryMem;
	}
}
