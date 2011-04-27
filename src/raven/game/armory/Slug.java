package raven.game.armory;

import java.util.List;

import raven.game.RavenBot;
import raven.game.RavenObject;
import raven.game.messaging.Dispatcher;
import raven.game.messaging.RavenMessage;
import raven.math.Vector2D;
import raven.script.RavenScript;
import raven.ui.GameCanvas;

public class Slug extends RavenProjectile {

	private static double slugMaxSpeed = RavenScript.getDouble("Slug_MaxSpeed");
	private static int slugMass = RavenScript.getInt("Slug_Mass");
	private static double slugMaxForce = RavenScript.getDouble("Slug_MaxForce");
	private static Vector2D slugScale = new Vector2D(RavenScript.getDouble("Slug_Scale"), RavenScript.getDouble("Slug_Scale"));
	private static int slugDamage = RavenScript.getInt("Slug_Damage");
	private static double slugBlastRadius = 1;
	private static double slugTimePersist = RavenScript.getDouble("Slug_Persistance");
	
	public Slug(RavenBot shooter, Vector2D target) {
		super(target, 0.0, shooter.velocity(), 
				slugMaxSpeed, shooter.heading(), 
				slugMass, slugScale, 
				shooter.maxTurnRate(), slugMaxForce, 
				slugBlastRadius,
				shooter.getWorld(),
				RavenObject.PROJECTILE);
	}
	
	private void TestForImpact()
	{
		  // a rail gun slug travels VERY fast. It only gets the chance to update once 
		  setImpacted(true);

		  //first find the closest wall that this ray intersects with. Then we
		  //can test against all entities within this range.
		  //double DistToClosestImpact = GetWorld().getDistanceToClosestWall(pos().sub(velocity), pos());
		  
		  //test to see if the ray between the current position of the slug and 
		  //the start position intersects with any bots.
		  List<RavenBot> hits = GetListOfIntersectingBots(pos().sub(velocity), pos());

		  //if no bots hit just return;
		  if (hits.isEmpty()) return;

		  //give some damage to the hit bots
		  for (RavenBot bot : hits)
		  {
		    //send a message to the bot to let it know it's been hit, and who the
		    //shot came from
		    Dispatcher.dispatchMsg(Dispatcher.SEND_MSG_IMMEDIATELY,
		                            getShooterID(),
		                            bot.ID(),
		                            RavenMessage.MSG_TAKE_THAT_MF,
		                            slugDamage);
		    
		  }
	}
	
	//These projectiles are visible if the current time is less than OriginTime + timePersist
	private boolean IsVisibleToPlayer()
	{
		if(slugTimePersist > 0)
		{
			slugTimePersist -= System.nanoTime(); 
		}
		
		return slugTimePersist > 0;
		
	}
	
	
	
	public void render()
	{
		if(IsVisibleToPlayer() && HasImpacted())
		{
			GameCanvas.greenPen();
			GameCanvas.line(getOrigin(), getImpactPoint());
		}
	}
	
	public void update()
	{
		  if (!HasImpacted())
		  {
		     //calculate the steering force
		    Vector2D DesiredVelocity = (getTarget().sub(pos()));
		    DesiredVelocity.normalize();
		    DesiredVelocity = DesiredVelocity.mul(maxSpeed());	

		    Vector2D sf = DesiredVelocity.sub(velocity());

		    //update the position
		    Vector2D accel = sf.div(mass());

		    setVelocity(velocity().add(accel));
		    //make sure the slug does not exceed maximum velocity
		    velocity.truncate(maxSpeed());

		    //update the position
		    pos().add(velocity()); 

		    TestForImpact();
		  }
		  else if (!IsVisibleToPlayer())
		  {
		    setDead(true);
		  }
	}
	
	

}
