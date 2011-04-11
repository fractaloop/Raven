package raven.game;

import java.util.List;
import java.util.Vector;

import raven.game.RavenGame;
import raven.math.*;
import raven.math.Vector2D;

//------------------------------------------------------------------------


public class RavenSteering {

	//--------------------------- Constants ----------------------------------

	//the radius of the constraining circle for the wander behavior
	public static final double WanderRad = 1.2;
	//distance the wander circle is projected in front of the agent
	public static final double WanderDist   = 2.0;
	//the maximum amount of displacement along the circle each frame
	public static final double WanderJitterPerSec = 40.0;

	//used in path following
	//public static final double WaypointSeekDist   = 20;                                          

	
	public enum SummingMethod{
		weightedAverage, 
		prioritized, 
		dithered
	};
	
	private enum BehaviorType{
		none(1), 
		seek(2),
		arrive(4),
		wander(8),
		separation(16),
		wallAvoidance(32);
		
		private int value;
		private BehaviorType(int i) {value = i;}
		public int getValue() {return value;}
	};
	
	private BehaviorType behaviorType;
	private RavenBot ravenBot;
	//pointer to the world data
	private RavenGame world;
	
	//the steering force created by the combined effect of all
	//the selected behaviors
	private Vector2D steeringForce;
	 
	  //these can be used to keep track of friends, pursuers, or prey
	private RavenBot targetAgent1;
	private RavenBot targetAgent2;

	  //the current target
	private Vector2D target;


	  //a vertex buffer to contain the feelers rqd for wall avoidance  
	  private Vector<Vector2D> feelers;
	  
	  //the length of the 'feeler/s' used in wall detection
	  private double wallDetectionFeelerLength;


	  //the current position on the wander circle the agent is
	  //attempting to steer towards
	  private Vector2D     wanderTarget; 

	  //explained above
	  private double wanderJitter;
	  private double wanderRadius;
	  private double wanderDistance;


	  //multipliers. These can be adjusted to effect strength of the  
	  //appropriate behavior.
	  private double        weightSeparation;
	  private double        weightWander;
	  private double        weightWallAvoidance;
	  private double        weightSeek;
	  private double        weightArrive;


	  //how far the agent can 'see'
	  private double        wiewDistance;

	  //binary flags to indicate whether or not a behavior should be active
	  private int flags;

	  
	  //Arrive makes use of these to determine how quickly a Raven_Bot
	  //should decelerate to its target
	  private enum Deceleration{fast, normal, slow};
	  //TODO
	  //default
	  private Deceleration deceleration;

	  //is cell space partitioning to be used or not?
	  private boolean cellSpaceOn;
	  

	  //what type of method is used to sum any active behavior
	  private SummingMethod  summingMethod;


	  //this function tests if a specific bit of m_iFlags is set
	  private boolean On(BehaviorType bt){
		  //TODO
		  return (flags & bt.getValue()) == bt.getValue();}

	  public boolean AccumulateForce(Vector2D runningTot, Vector2D forceToAdd){
		//calculate how much steering force the vehicle has used so far
		  double magnitudeSoFar = runningTot.length();

		  //calculate how much steering force remains to be used by this vehicle
		  double magnitudeRemaining = ravenBot.maxForce() - magnitudeSoFar;

		  //return false if there is no more force left to use
		  if (magnitudeRemaining <= 0.0) return false;

		  //calculate the magnitude of the force we want to add
		  double magnitudeToAdd = forceToAdd.length();
		  
		  //if the magnitude of the sum of ForceToAdd and the running total
		  //does not exceed the maximum force available to this vehicle, just
		  //add together. Otherwise add as much of the ForceToAdd vector is
		  //possible without going over the max.
		  if (magnitudeToAdd < magnitudeRemaining)
		  {
		    runningTot.add(forceToAdd);
		  }

		  else
		  {
		    magnitudeToAdd = magnitudeRemaining;

		    //add it to the steering force
		    //TODO didn't make it normalize
		    runningTot.add(forceToAdd.mul(magnitudeToAdd)); 
		  }

		  return true;
		}
	

	  //creates the antenna utilized by the wall avoidance behavior
	  void      CreateFeelers(){
		  //TODO
	}



	   /* .......................................................

	                    BEGIN BEHAVIOR DECLARATIONS

	      .......................................................*/


	  //this behavior moves the agent towards a target position
	  private Vector2D Seek(final Vector2D target){
		
		  return null;
	}

	  //this behavior is similar to seek but it attempts to arrive 
	  //at the target with a zero velocity
	  private Vector2D Arrive(final Vector2D target, final Deceleration deceleration){
		//TODO
		  return null;
	}

	  //this behavior makes the agent wander about randomly
	  private Vector2D Wander(){
		//TODO
		  return null;
	}

	  //this returns a steering force which will keep the agent away from any
	  //walls it may encounter
	  private Vector2D WallAvoidance(final List<Wall2D> walls){
		//TODO
		  return null;
	}

	  
	  private Vector2D Separation(final List<RavenBot> agents){
		//TODO
		  return null;
	}


	    /* .......................................................

	                       END BEHAVIOR DECLARATIONS

	      .......................................................*/

	  //calculates and sums the steering forces from any active behaviors
	//---------------------- CalculatePrioritized ----------------------------
	  //
	  //  this method calls each active steering behavior in order of priority
	  //  and acumulates their forces until the max steering force magnitude
	  //  is reached, at which time the function returns the steering force 
	  //  accumulated to that  point
	  //------------------------------------------------------------------------

	  private Vector2D CalculatePrioritized(){
		//TODO
		  Vector2D force;

		  if (On(BehaviorType.wallAvoidance))
		  {
		    force = WallAvoidance(world.getMap().getWalls()).mul(weightWallAvoidance);

		    if (!AccumulateForce(steeringForce, force)) return steeringForce;
		  }

		 
		  //these next three can be combined for flocking behavior (wander is
		  //also a good behavior to add into this mix)

		    if (On(BehaviorType.separation))
		    {
		      force = Separation(world.getBots()).mul(weightSeparation);

		      if (!AccumulateForce(steeringForce, force)) return steeringForce;
		    }


		  if (On(BehaviorType.seek))
		  {
		    force = Seek(target).mul(weightSeek);

		    if (!AccumulateForce(steeringForce, force)) return steeringForce;
		  }


		  if (On(BehaviorType.arrive))
		  {
		    force = Arrive(target, deceleration).mul(weightArrive);

		    if (!AccumulateForce(steeringForce, force)) return steeringForce;
		  }

		  if (On(BehaviorType.wander))
		  {
		    force = Wander().mul(weightWander);

		    if (!AccumulateForce(steeringForce, force)) return steeringForce;
		  }


		  return steeringForce;
		}


	  public RavenSteering(RavenGame world, RavenBot ravenBot) {
		// TODO Auto-generated constructor stub
	}

	  //calculates and sums the steering forces from any active behaviors
	  public Vector2D Calculate(){
		//TODO
		  return null;
	}

	  //calculates the component of the steering force that is parallel
	  //with the Raven_Bot heading
	  public double    ForwardComponent(){
		  return ravenBot.heading().dot(steeringForce);
		  
	}

	  //calculates the component of the steering force that is perpendicuar
	  //with the Raven_Bot heading
	  public double    SideComponent(){
		  return ravenBot.side().dot(steeringForce);
	}


	  public void SetTarget(Vector2D t){target = t;}
	  public final Vector2D  Target(){return target;}

	  public void SetTargetAgent1(RavenBot Agent){targetAgent1 = Agent;}
	  public void SetTargetAgent2(RavenBot Agent){targetAgent2 = Agent;}


	  public final Vector2D  Force(){return steeringForce;}

	  public void SetSummingMethod(SummingMethod sm){summingMethod = sm;}

	  public void SeekOn(){flags |= BehaviorType.seek.getValue();}
	  public void ArriveOn(){flags |= BehaviorType.arrive.getValue();}
	  public void WanderOn(){flags |= BehaviorType.wander.getValue();}
	  public void SeparationOn(){flags |= BehaviorType.separation.getValue();}

	  
	public void wallAvoidanceOn() {
		// TODO Auto-generated method stub
		
	}

	public void separationOn() {
		// TODO Auto-generated method stub
		
	}
	
	public void SeekOff()  {if(On(BehaviorType.seek))   flags ^=BehaviorType.seek.getValue();}
	public void ArriveOff(){if(On(BehaviorType.arrive)) flags ^=BehaviorType.arrive.getValue();}
	public void WanderOff(){if(On(BehaviorType.wander)) flags ^=BehaviorType.wander.getValue();}
	public void SeparationOff(){if(On(BehaviorType.separation)) flags ^=BehaviorType.separation.getValue();}
	public void WallAvoidanceOff(){if(On(BehaviorType.wallAvoidance)) flags ^=BehaviorType.wallAvoidance.getValue();}
    
	public boolean SeekIsOn(){return On(BehaviorType.seek);}
	public boolean ArriveIsOn(){return On(BehaviorType.arrive);}
	public boolean WanderIsOn(){return On(BehaviorType.wander);}
	public boolean SeparationIsOn(){return On(BehaviorType.separation);}
	public boolean WallAvoidanceIsOn(){return On(BehaviorType.wallAvoidance);}
    
	public final Vector<Vector2D> GetFeelers(){return feelers;}
	
	public final double WanderJitter(){return wanderJitter;}
	public final double WanderDistance(){return wanderDistance;}
	public final double WanderRadius(){return wanderRadius;}
    
	public final double SeparationWeight(){return weightSeparation;}

}
