package raven.game;

import java.util.List;
import java.util.Random;
import java.util.Vector;

import raven.game.interfaces.IRavenBot;
import raven.math.Geometry;
import raven.math.Transformations;
import raven.math.Vector2D;
import raven.math.Wall2D;
import raven.script.RavenScript;
import raven.ui.GameCanvas;

//------------------------------------------------------------------------


public class RavenSteering {

	//--------------------------- Constants ----------------------------------

	/** the radius of the constraining circle for the wander behavior */
	public static final double wanderRad = 1.2;
	/** distance the wander circle is projected in front of the agent */
	public static final double wanderDist   = 2.0;
	/** the maximum amount of displacement along the circle each frame */
	public static final double wanderJitterPerSec = 40.0;

	//used in path following
	//public static final double WaypointSeekDist   = 20;                                          


	public enum SummingMethod{
		WEIGHTED_AVERAGE, 
		PRIORITIZED, 
		DITHERED
	};

	private enum BehaviorType{
		NONE(1), 
		SEEK(2),
		ARRIVE(4),
		WANDER(8),
		SEPARATION(16),
		WALL_AVOIDANCE(32);

		private int value;
		private BehaviorType(int i) {value = i;}
		public int getValue() {return value;}
	};

	private RavenBot ravenBot;
	/** the world data */
	private RavenGame world;

	/** the steering force created by the combined effect of all the selected
	 * behaviors */
	private Vector2D steeringForce;

	/** these can be used to keep track of friends, pursuers, or prey */
	private RavenBot targetAgent1;
	
	/** the current target */
	private Vector2D target;


	/** a vertex buffer to contain the feelers rqd for wall avoidance */  
	private Vector<Vector2D> feelers;

	/** the length of the 'feeler/s' used in wall detection */
	private double wallDetectionFeelerLength;


	/** the current position on the wander circle the agent is attempting to
	 * steer towards */
	private Vector2D     wanderTarget; 

	/** @see {@link wanderTarget} */
	private double wanderJitter;
	private double wanderRadius;
	private double wanderDistance;


	/** multipliers. These can be adjusted to effect strength of the
	 * appropriate behavior. */
	private double        weightSeparation;
	private double        weightWander;
	private double        weightWallAvoidance;
	private double        weightSeek;
	private double        weightArrive;


	/** how far the agent can 'see' */
	private double        viewDistance;

	/** binary flags to indicate whether or not a behavior should be active */
	private int flags;


	/** Arrive makes use of these to determine how quickly a Raven_Bot should
	 * decelerate to its target */
	private enum Deceleration {
		FAST,
		NORMAL,
		SLOW
	};
	/** default */
	private Deceleration deceleration;

	/** is cell space partitioning to be used or not? */
	private boolean cellSpaceOn;

	private BehaviorType behaviorType;
	
	/** what type of method is used to sum any active behavior */
	private SummingMethod  summingMethod;


	/** this function tests if a specific bit of flags is set */
	private boolean On(BehaviorType bt) {
		return (flags & bt.getValue()) == bt.getValue();
	}

	/**
	 * Handles the max speed of the bot.
	 * @param runningTot how fast the bot is going so far.
	 * @param forceToAdd how much velocity to add.
	 * @return A true if force was added successfully to the bot, false if bot is going max speed.
	 */
	public boolean accumulateForce(Vector2D runningTot, Vector2D forceToAdd) {
		//calculate how much steering force the vehicle has used so far
		double magnitudeSoFar = runningTot.length();

		//calculate how much steering force remains to be used by this vehicle
		double magnitudeRemaining = ravenBot.maxForce() - magnitudeSoFar;

		//return false if there is no more force left to use
		if (magnitudeRemaining <= 0.0)
			return false;

		//calculate the magnitude of the force we want to add
		double magnitudeToAdd = forceToAdd.length();

		//if the magnitude of the sum of ForceToAdd and the running total
		//does not exceed the maximum force available to this vehicle, just
		//add together. Otherwise add as much of the ForceToAdd vector is
		//possible without going over the max.
		if (magnitudeToAdd < magnitudeRemaining) {
			runningTot.setValue(runningTot.add(forceToAdd));
		} else {
			magnitudeToAdd = magnitudeRemaining;

			//add it to the steering force
			forceToAdd.normalize();
			// Dirty hack due to the way it was ported.
			runningTot.setValue(runningTot.add(forceToAdd.mul(magnitudeToAdd))); 
		}

		return true;
	}





	/* 
	 * BEGIN BEHAVIOR DECLARATIONS
	 */


	/** this behavior moves the agent towards a target position */
	private Vector2D seek(final Vector2D target) {

		Vector2D desiredVelocity = target.sub(ravenBot.pos());
		desiredVelocity.normalize();
		desiredVelocity = desiredVelocity.mul(ravenBot.maxForce());

		return (desiredVelocity.sub(ravenBot.velocity()));

	}

	/** this behavior is similar to seek but it attempts to arrive at the
	 * target with a zero velocity */
	private Vector2D arrive(final Vector2D target, final Deceleration deceleration){
		Vector2D toTarget = target.sub(ravenBot.pos());

		//calculate the distance to the target
		double dist = toTarget.length();

		if (dist > 0)
		{
			//because Deceleration is enumerated as an int, this value is required
			//to provide fine tweaking of the deceleration..
			final double DecelerationTweaker = 0.3;

			//calculate the speed required to reach the target given the desired
			//deceleration
			//   double speed =  dist / (deceleration* decelerationTweaker);     
			double speed= target.distance(ravenBot.pos())/ (Double.valueOf(deceleration.toString())*DecelerationTweaker);
			//make sure the velocity does not exceed the max
			speed = Math.min(speed, ravenBot.maxSpeed());


			//from here proceed just like Seek except we don't need to normalize 
			//the ToTarget vector because we have already gone to the trouble
			//of calculating its length: dist. 
			Vector2D DesiredVelocity =  toTarget.mul(speed / dist);

			return (DesiredVelocity.sub(ravenBot.velocity()));
		}

		return new Vector2D(0,0);

	}

	/** this behavior makes the agent wander about randomly */
	private Vector2D wander() {

		//first, add a small random vector to the target's position
		wanderTarget = wanderTarget.add(new Vector2D( new Random().nextDouble()* wanderJitter,
				new Random().nextDouble() * wanderJitter));

		//reproject this new vector back on to a unit circle
		wanderTarget.normalize();

		//increase the length of the vector to the same as the radius
		//of the wander circle
		wanderTarget = wanderTarget.mul(wanderRadius);

		//move the target into a position WanderDist in front of the agent
		Vector2D target = wanderTarget.add(new Vector2D(wanderDistance, 0));

		//project the target into world space
		Vector2D Target = Transformations.pointToLocalSpace(target, ravenBot.heading(), ravenBot.side(), ravenBot.pos());

		//and steer towards it
		return Target.sub(ravenBot.pos()); 

	}

	/** this returns a steering force which will keep the agent away from any
	 * walls it may encounter */
	private Vector2D wallAvoidance(final List<Wall2D> walls) {
		//the feelers are contained in a std::vector, m_Feelers
		createFeelers();

		Double DistToThisIP    = 0.0;
		double DistToClosestIP = Double.MAX_VALUE;

		//this will hold an index into the vector of walls
		int ClosestWall = -1;

		Vector2D SteeringForce = new Vector2D();
		Vector2D point = new Vector2D();         //used for storing temporary info
		Vector2D ClosestPoint=new Vector2D();  //holds the closest intersection point

		//examine each feeler in turn
		for (int flr=0; flr<feelers.size(); ++flr)
		{
			//run through each wall checking for any intersection points
			for (int w=0; w<walls.size(); ++w)
			{
				if (Geometry.lineIntersection2D(ravenBot.pos(),
						feelers.get(flr),
						walls.get(w).from(),
						walls.get(w).to(),
						DistToThisIP,
						point))
				{
					//is this the closest found so far? If so keep a record
							if (DistToThisIP < DistToClosestIP)
							{
								DistToClosestIP = DistToThisIP;

								ClosestWall = w;

								ClosestPoint = point;
							}
				}
			}//next wall


			//if an intersection point has been detected, calculate a force  
			//that will direct the agent away
			if (ClosestWall >=0)
			{
				//calculate by what distance the projected position of the agent
				//will overshoot the wall
				Vector2D overShoot = feelers.get(flr).sub(ClosestPoint);

				//create a force in the direction of the wall normal, with a 
				//magnitude of the overshoot
				SteeringForce = walls.get(ClosestWall).normal().mul(overShoot.length());
			}

		}//next feeler

		return SteeringForce;

	}
	
	private void createFeelers(){
		feelers.clear();

		Vector2D temp;

		//feeler pointing straight in front
		temp = new Vector2D(ravenBot.pos());
		temp = temp.add(ravenBot.heading().mul(wallDetectionFeelerLength).mul(ravenBot.speed()));
		feelers.add(temp);

		//feeler to left
		temp = new Vector2D(ravenBot.heading());
		Transformations.Vec2DRotateAroundOrigin(temp, -Math.PI / 6);
		feelers.add(ravenBot.pos().add(temp.mul(wallDetectionFeelerLength/2).mul(ravenBot.speed())));

		//feeler to right
		temp = new Vector2D(ravenBot.heading());
		Transformations.Vec2DRotateAroundOrigin(temp, Math.PI / 6);
		feelers.add(ravenBot.pos().add(temp.mul(wallDetectionFeelerLength/2).mul(ravenBot.speed()))); 
	}


	private Vector2D separation(final List<IRavenBot> agents){

		//iterate through all the neighbors and calculate the vector from them
		Vector2D steeringForce = new Vector2D();
		for( IRavenBot agent : agents) {

			//make sure this agent isn't included in the calculations and that
			//the agent being examined is close enough. ***also make sure it doesn't
			//include the evade target ***
			if(ravenBot.isTagged() && agent != targetAgent1) {
				Vector2D toAgent = ravenBot.pos().sub(agent.pos());
				toAgent.normalize();

				//scale the force inversely proportional to the agents distance  
				//from its neighbor.
				steeringForce = steeringForce.add(toAgent.div(toAgent.length()));
			}
		}
		
		return steeringForce;
	}


	/* 
	 * END BEHAVIOR DECLARATIONS
	 */

	/** calculates and sums the steering forces from any active behaviors
	 * this method calls each active steering behavior in order of priority
	 * and accumulates their forces until the max steering force magnitude
	 * is reached, at which time the function returns the steering force
	 * accumulated to that point */
	private Vector2D calculatePrioritized(){

		Vector2D force = new Vector2D();

		if (On(BehaviorType.WALL_AVOIDANCE))
		{
			force = wallAvoidance(world.getMap().getWalls()).mul(weightWallAvoidance);

			if (!accumulateForce(steeringForce, force)) return steeringForce;
		}

		//these next three can be combined for flocking behavior (wander is
		//also a good behavior to add into this mix)
		if (On(BehaviorType.SEPARATION))
		{
			// HAve to tag bots that are in danger of being hit
			world.tagRavenBotsWithinViewRange(ravenBot, viewDistance);
			force = separation(world.getBots()).mul(weightSeparation);
			if (!accumulateForce(steeringForce, force)) return steeringForce;
		}

		if (On(BehaviorType.SEEK))
		{
			force = seek(target).mul(weightSeek);

			if (!accumulateForce(steeringForce, force)) return steeringForce;
		}

		if (On(BehaviorType.ARRIVE))
		{
			force = arrive(target, deceleration).mul(weightArrive);

			if (!accumulateForce(steeringForce, force)) return steeringForce;
		}

		if (On(BehaviorType.WANDER))
		{
			force = wander().mul(weightWander);

			if (!accumulateForce(steeringForce, force)) return steeringForce;
		}


		return steeringForce;
	}


	public RavenSteering(RavenGame world, RavenBot ravenBot) {

		this.world = world;
		this.ravenBot = ravenBot;

		flags						= 0;
		weightSeparation			= RavenScript.getDouble("SeparationWeight");
		weightWander				= RavenScript.getDouble("WanderWeight");
		weightWallAvoidance			= RavenScript.getDouble("WallAvoidanceWeight");
		viewDistance				= RavenScript.getDouble("ViewDistance");
		wallDetectionFeelerLength	= RavenScript.getDouble("WallDetectionFeelerLength");
		steeringForce				= new Vector2D();
		feelers						= new Vector<Vector2D>(3);
		deceleration				= Deceleration.NORMAL;
		targetAgent1				= null;
		wanderDistance				= wanderDist;
		wanderJitter				= wanderJitterPerSec;
		wanderRadius				= wanderRad;
		weightSeek					= RavenScript.getDouble("SeekWeight");
		weightArrive				= RavenScript.getDouble("ArriveWeight");
		cellSpaceOn					= false;
		summingMethod				= SummingMethod.PRIORITIZED;

		//stuff for the wander behavior
		double theta = Math.random() * (2* Math.PI);

		//create a vector to a target position on the wander circle
		wanderTarget = new Vector2D(wanderRadius * Math.cos(theta), wanderRadius * Math.sin(theta));
		
		// These defaults were put int as assumptions.  TODO: Validate my assumptions.
		cellSpaceOn = false;
		behaviorType = BehaviorType.NONE;
		summingMethod = SummingMethod.PRIORITIZED; // TODO: Implement others
	}

	/** calculates and sums the steering forces from any active behaviors */
	public Vector2D calculate(){
		//reset the steering force
		steeringForce.Zero();
		steeringForce = calculatePrioritized();

		return steeringForce;
	}

	/** calculates the component of the steering force that is parallel with
	 * the Raven_Bot heading */
	public double forwardComponent(){
		return ravenBot.heading().dot(steeringForce);
	}

	/** calculates the component of the steering force that is perpendicular
	 * with the RavenBot heading */
	public double sideComponent(){
		return ravenBot.side().dot(steeringForce);
	}

	public void setTarget(Vector2D t) { target = t; }
	public final Vector2D target() { return target; }

	public void setTargetAgent1(RavenBot Agent) { targetAgent1 = Agent; }
	public void SetTargetAgent2(RavenBot Agent) { }

	public final Vector2D force() { return steeringForce; }

	public void setSummingMethod(SummingMethod sm) { summingMethod = sm; }

	public void seekOn() { flags |= BehaviorType.SEEK.getValue(); }
	public void arriveOn() { flags |= BehaviorType.ARRIVE.getValue(); }
	public void wanderOn() { flags |= BehaviorType.WANDER.getValue(); }
	public void separationOn() { flags |= BehaviorType.SEPARATION.getValue(); }
	public void wallAvoidanceOn() { flags |= BehaviorType.WALL_AVOIDANCE.getValue(); }

	public void seekOff() { if(On(BehaviorType.SEEK)) flags ^= BehaviorType.SEEK.getValue(); }
	public void arriveOff() { if(On(BehaviorType.ARRIVE)) flags ^= BehaviorType.ARRIVE.getValue(); }
	public void wanderOff() { if(On(BehaviorType.WANDER)) flags ^= BehaviorType.WANDER.getValue(); }
	public void separationOff() { if(On(BehaviorType.SEPARATION)) flags ^= BehaviorType.SEPARATION.getValue(); }
	public void wallAvoidanceOff() { if(On(BehaviorType.WALL_AVOIDANCE)) flags ^= BehaviorType.WALL_AVOIDANCE.getValue(); }
	public boolean seekIsOn() { return On(BehaviorType.SEEK); }
	public boolean arriveIsOn() { return On(BehaviorType.ARRIVE); }
	public boolean wanderIsOn() { return On(BehaviorType.WANDER); }
	public boolean separationIsOn() { return On(BehaviorType.SEPARATION); }
	public boolean wallAvoidanceIsOn() { return On(BehaviorType.WALL_AVOIDANCE); }

	public final Vector<Vector2D> getFeelers() { return feelers; }

	public final double wanderJitter() { return wanderJitter; }
	public final double wanderDistance() { return wanderDistance; }
	public final double wanderRadius() { return wanderRadius; }

	public final double separationWeight() { return weightSeparation; }

	public void renderFeelers() {
		for (Vector2D feeler : feelers) {
			GameCanvas.thickBluePen();
			GameCanvas.line(ravenBot.pos(), feeler);
		}
	}
}
