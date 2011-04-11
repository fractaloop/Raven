package raven.game;

import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JPanel;

import raven.game.RavenGame;
import raven.game.RavenObject;
import raven.math.Vector2D;
import raven.ui.GameCanvas;
import raven.ui.KeyState;
import java.util.List;
import raven.math.*;
import raven.script.RavenScript;
import sun.font.Script;
import java.util.Random;
import java.lang.Math;





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

	
	public enum SummingMethod{weightedAverage, prioritized, dithered};
	//a pointer to the owner of this instance
	private enum BehaviorType{none, seek, arrive, wander, separation, wallAvoidance};
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
	  private double        viewDistance;

	  //binary flags to indicate whether or not a behavior should be active
	  private int           flags;

	  
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
		  return (flags & bt) == bt;}

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
		  
		  Vector2D desiredVelocity = (target.sub(ravenBot.pos()).mul(ravenBot.maxSpeed));

          return (desiredVelocity.sub(ravenBot.velocity()));

	}

	  //this behavior is similar to seek but it attempts to arrive 
	  //at the target with a zero velocity
	  private Vector2D Arrive(final Vector2D target, final Deceleration deceleration){
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
		//TODO    double speed =  dist / (deceleration* decelerationTweaker);     
		    double speed;
		    //make sure the velocity does not exceed the max
		    speed = MinOf(speed, ravenBot.maxSpeed());

		    //from here proceed just like Seek except we don't need to normalize 
		    //the ToTarget vector because we have already gone to the trouble
		    //of calculating its length: dist. 
		    Vector2D DesiredVelocity =  toTarget.mul(speed / dist);

		    return (DesiredVelocity.sub(ravenBot.velocity()));
		  }

		  return new Vector2D(0,0);

	}

	  //this behavior makes the agent wander about randomly
	  private Vector2D Wander(){

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
		  Vector2D Target = pointToWorldSpace(target, ravenBot.heading(), ravenBot.side(), ravenBot.pos());

		  //and steer towards it
		  return Target.sub(ravenBot.pos()); 

	}

	  //this returns a steering force which will keep the agent away from any
	  //walls it may encounter
	  private Vector2D WallAvoidance(final List<Wall2D> walls){
		  //the feelers are contained in a std::vector, m_Feelers
		  CreateFeelers();
		  
		  double DistToThisIP    = 0.0;
		  double DistToClosestIP = Double.MAX_VALUE;

		  //this will hold an index into the vector of walls
		  int ClosestWall = -1;

		  Vector2D SteeringForce,
		            point,         //used for storing temporary info
		            ClosestPoint;  //holds the closest intersection point

		  //examine each feeler in turn
		  for (int flr=0; flr<feelers.size(); ++flr)
		  {
		    //run through each wall checking for any intersection points
		    for (int w=0; w<walls.size(); ++w)
		    {
		      if (lineIntersection2D(ravenBot.pos(),
		                             feelers.get(flr),
		                             walls.get(w).from(),
		                             walls.get(w).to()))
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

		  if (On(behaviorType.wallAvoidance))
		  {
		    force = WallAvoidance(world.getMap().getWalls()).mul(weightWallAvoidance);

		    if (!AccumulateForce(steeringForce, force)) return steeringForce;
		  }

		 
		  //these next three can be combined for flocking behavior (wander is
		  //also a good behavior to add into this mix)

		    if (On(behaviorType.separation))
		    {
		      force = Separation(world.getBots()).mul(weightSeparation);

		      if (!AccumulateForce(steeringForce, force)) return steeringForce;
		    }


		  if (On(behaviorType.seek))
		  {
		    force = Seek(target).mul(weightSeek);

		    if (!AccumulateForce(steeringForce, force)) return steeringForce;
		  }


		  if (On(behaviorType.arrive))
		  {
		    force = Arrive(target, deceleration).mul(weightArrive);

		    if (!AccumulateForce(steeringForce, force)) return steeringForce;
		  }

		  if (On(behaviorType.wander))
		  {
		    force = Wander().mul(weightWander);

		    if (!AccumulateForce(steeringForce, force)) return steeringForce;
		  }


		  return steeringForce;
		}


	  public RavenSteering(RavenGame world, RavenBot ravenBot) {

          //world(world);
          //ravenBot(agent);
		  
          flags=0;
          RavenScript script;
          weightSeparation=RavenScript.getDouble("SeparationWeight");
          weightWander=RavenScript.getDouble("WanderWeight");
          weightWallAvoidance=RavenScript.getDouble("WallAvoidanceWeight");
          viewDistance=RavenScript.getDouble("ViewDistance");
          wallDetectionFeelerLength=RavenScript.getDouble("WallDetectionFeelerLength");
          feelers=3;
          deceleration=Deceleration.normal;
          targetAgent1=null;
          targetAgent2=null;
          wanderDistance=WanderDist;
          wanderJitter=WanderJitterPerSec;
          wanderRadius=WanderRad;
          weightSeek=RavenScript.getDouble("SeekWeight");
          weightArrive=RavenScript.getDouble("ArriveWeight");
          cellSpaceOn=false;
          summingMethod= SummingMethod.prioritized;
          
          //stuff for the wander behavior
          double theta = new Random().nextFloat() * (2* Math.PI);

          //create a vector to a target position on the wander circle
          wanderTarget = new Vector2D(wanderRadius * Math.cos(theta),
                           wanderRadius * Math.sin(theta));


	}

	  //calculates and sums the steering forces from any active behaviors
	  public Vector2D Calculate(){
		  //reset the steering force
		  steeringForce.Zero();

		  //tag neighbors if any of the following 3 group behaviors are switched on
		  if (On(behaviorType.separation))
		  {
		    world.tagRavenBotsWithinViewRange(ravenBot, viewDistance);
		  }

		  steeringForce = CalculatePrioritized();

		  return steeringForce;

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

	  public void SeekOn(){flags |= behaviorType.seek;}
	  public void ArriveOn(){flags |= behaviorType.arrive;}
	  public void WanderOn(){flags |= behaviorType.wander;}
	  public void SeparationOn(){flags |= behaviorType.separation;}

	  
	public void wallAvoidanceOn() {
		// TODO Auto-generated method stub
		
	}

	public void separationOn() {
		// TODO Auto-generated method stub
		
	}
	  public void SeekOff()  {if(On(behaviorType.seek))flags ^= behaviorType.seek;}
	  public void ArriveOff(){if(On(behaviorType.arrive)) flags ^=behaviorType.arrive;}
	  public void WanderOff(){if(On(behaviorType.wander)) flags ^=behaviorType.wander;}
	  public void SeparationOff(){if(On(behaviorType.separation)) flags ^=behaviorType.separation;}
	  public void WallAvoidanceOff(){if(On(behaviorType.wallAvoidance)) flags ^=behaviorType.wallAvoidance;}

	  public boolean SeekIsOn(){return On(behaviorType.seek);}
	  public boolean ArriveIsOn(){return On(behaviorType.arrive);}
	  public boolean WanderIsOn(){return On(behaviorType.wander);}
	  public boolean SeparationIsOn(){return On(behaviorType.separation);}
	  public boolean WallAvoidanceIsOn(){return On(behaviorType.wallAvoidance);}

	 public final Vector<Vector2D> GetFeelers(){return feelers;}
	  
	  public final double WanderJitter(){return wanderJitter;}
	  public final double WanderDistance(){return wanderDistance;}
	  public final double WanderRadius(){return wanderRadius;}

	  public final double SeparationWeight(){return weightSeparation;}
	  
}
