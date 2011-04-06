package raven.game.armory;

import raven.game.RavenBot;
import raven.game.messaging.Dispatcher;
import raven.game.messaging.RavenMessage;
import raven.math.Vector2D;
import raven.script.RavenScript;
import raven.ui.GameCanvas;

public class Pellet extends RavenProjectile {

	private static double pelletMaxSpeed = RavenScript.getDouble("pellet_MaxSpeed");
	private static int pelletMass = RavenScript.getInt("pellet_mass");
	private static double pelletMaxForce = RavenScript.getDouble("pellet_maxforce");
	private static Vector2D pelletScale = new Vector2D(RavenScript.getDouble("pellet_scale"), RavenScript.getDouble("pellet_scale"));
	private static int pelletDamage = RavenScript.getInt("pellet_damage");
	private static double pelletBlastRadius = 1; //TODO: Pellet has no blast radius, what is default?
	private static double pelletTimePersist = RavenScript.getDouble("pellet_persistance");
	
		
	public Pellet(RavenBot shooter, Vector2D target) {
		super(target, 0.0, shooter.velocity(), 
				pelletMaxSpeed, shooter.heading(), 
				pelletMass, pelletScale, 
				shooter.maxTurnRate(), pelletMaxForce, 
				pelletBlastRadius, pelletDamage, 
				shooter.getWorld());
	}

	
	private boolean isVisibleToPlayer()
	{
		if(pelletTimePersist > 0)
		{
			pelletTimePersist -= System.nanoTime(); 
		}
		
		return pelletTimePersist > 0;
	}
	
	public void render()
	{
		  if (isVisibleToPlayer() && HasImpacted()) {
		    GameCanvas.yellowPen();
		    GameCanvas.line(getOrigin(), getImpactPoint());

		    GameCanvas.brownBrush();
		    GameCanvas.circle(getImpactPoint(), 3);
		  }
	}
	
	public void update()
	{
		  if (!HasImpacted())
		  {
		     //calculate the steering force
		    Vector2D DesiredVelocity = this.velocity().mul(this.maxSpeed());
		    	
		    Vector2D sf = DesiredVelocity.sub(this.velocity());

		    //update the position
		    Vector2D accel = sf.div(this.mass());

		    this.setVelocity(this.velocity().add(accel));

		    //make sure vehicle does not exceed maximum velocity
		    this.velocity().truncate(this.maxSpeed());
		    
		    //update the position
		    this.setPos(this.pos().add(this.velocity()));

		    TestForImpact();
		  }
		  else if (!isVisibleToPlayer())
		  {
		   this.setDead(true);
		  }
	}
	
	private void TestForImpact()
	{
		//a shot gun shell is an instantaneous projectile so it only gets the chance
		  //to update once 
		  this.setImpacted(true);

		  //first find the closest wall that this ray intersects with. Then we
		  //can test against all entities within this range.
		  double distToClosestImpact = this.GetWorld().getDistanceToClosestWall(pos().sub(velocity), pos());

		  //test to see if the ray between the current position of the shell and 
		  //the start position intersects with any bots.
		  RavenBot hit = GetClosestIntersectingBot(pos().sub(velocity), pos());
		  
		  //if no bots hit just return;
		  if (hit == null) return;

		  //TODO: determine the impact point with the bot's bounding circle so that the
		  //shell can be rendered properly
		  /*GetLineSegmentCircleClosestIntersectionPoint(m_vOrigin,
		                                               m_vImpactPoint,
		                                               hit->Pos(),
		                                               hit->BRadius(),
		                                               m_vImpactPoint);
		                                               */

		  //send a message to the bot to let it know it's been hit, and who the
		  //shot came from
		  Dispatcher.dispatchMsg(Dispatcher.SEND_MSG_IMMEDIATELY,
		                              getShooterID(),
		                              hit.ID(),
		                              RavenMessage.MSG_TAKE_THAT_MF,
		                              pelletDamage);
	}
	
	
}
