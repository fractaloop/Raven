package raven.game.armory;

import java.util.ArrayList;

import javax.swing.text.html.HTMLDocument.HTMLReader.IsindexAction;

import raven.game.RavenBot;
import raven.game.messaging.Dispatcher;
import raven.game.messaging.RavenMessage;
import raven.math.Vector2D;
import raven.script.RavenScript;
import raven.ui.GameCanvas;

public class Rocket extends RavenProjectile {

	private static double rocketMaxSpeed = RavenScript.getDouble("rocket_MaxSpeed");
	private static int rocketMass = RavenScript.getInt("rocket_mass");
	private static double rocketMaxForce = RavenScript.getDouble("rocket_maxforce");
	private static Vector2D rocketScale = new Vector2D(RavenScript.getDouble("rocket_scale"), RavenScript.getDouble("rocket_scale"));
	private static int rocketDamage = RavenScript.getInt("rocket_damage");
	private static double rocketBlastRadius = 1; 
	private static double currentBlastRadius;
	
	public Rocket(RavenBot shooter, Vector2D target) {
		super(target, 0.0, shooter.velocity(), rocketMaxSpeed, shooter.heading(), rocketMass, rocketScale, shooter.maxTurnRate(), rocketMaxForce, rocketBlastRadius, rocketDamage, shooter.getWorld());
		currentBlastRadius = 0.0;
	}
	
	private void InflictDamageOnBotsWithinBlastRadius(){
		  ArrayList<RavenBot> allBots = GetWorld().getBots();

		  for (RavenBot curBot : allBots)
		  {
		    if (pos().distanceSq(curBot.pos()) < (getBlastRadius() + curBot.getBRadius()))
		    {
		      //send a message to the bot to let it know it's been hit, and who the
		      //shot came from
		      Dispatcher.dispatchMsg(Dispatcher.SEND_MSG_IMMEDIATELY,
		                              getShooterID(),
		                              curBot.ID(),
		                              RavenMessage.MSG_TAKE_THAT_MF,
		                              rocketDamage);
		      
		    }
		  }  
	}
	
	private void TestForImpact(){
		  
	    //if the projectile has reached the target position or it hits an entity
	    //or wall it should explode/inflict damage/whatever and then mark itself
	    //as dead


	    //test to see if the line segment connecting the rocket's current position
	    //and previous position intersects with any bots.
	    RavenBot hit = GetClosestIntersectingBot(pos().sub(velocity), pos());
	    
	    //if hit
	    if (hit != null)
	    {
	      setImpacted(true);

	      //send a message to the bot to let it know it's been hit, and who the
	      //shot came from
	      Dispatcher.dispatchMsg(Dispatcher.SEND_MSG_IMMEDIATELY,
	                              getShooterID(),
	                              hit.ID(),
	                              RavenMessage.MSG_TAKE_THAT_MF,
	                              rocketDamage);

	      //test for bots within the blast radius and inflict damage
	      InflictDamageOnBotsWithinBlastRadius();
	    }

	    //test for impact with a wall
	    
	    double dist = GetWorld().getDistanceToClosestWall(getOrigin(), pos());
	    if( dist == 0)
	     {
	        setImpacted(true);
	      
	        //test for bots within the blast radius and inflict damage
	        InflictDamageOnBotsWithinBlastRadius();

	        setPos(getImpactPoint());

	        return;
	    }
	    
	    //test to see if rocket has reached target position. If so, test for
	     //all bots in vicinity
	    final double tolerance = 5.0;   
	    if (pos().distanceSq(getTarget()) < tolerance*tolerance)
	    {
	      setImpacted(true);

	      InflictDamageOnBotsWithinBlastRadius();
	    }

	}
	
	public void render(){
		  GameCanvas.redPen();
		  GameCanvas.orangeBrush();
		  GameCanvas.circle(pos(), 2);

		  if (HasImpacted())
		  {
		    GameCanvas.hollowBrush();
		    GameCanvas.circle(pos(), currentBlastRadius);
		  }
	}
	
	public void update(){
		  if (!HasImpacted())
		  {
		    setVelocity(heading().mul(maxSpeed()));

		    //make sure vehicle does not exceed maximum velocity
		    velocity().truncate(maxSpeed());

		    //update the position
		    setPos(pos().add(velocity));

		    TestForImpact();  
		  }

		  else
		  {
			  currentBlastRadius += RavenScript.getDouble("Rocket_ExplosionDecayRate");
		    
			//when the rendered blast circle becomes equal in size to the blast radius
		    //the rocket can be removed from the game
		    if (currentBlastRadius > getBlastRadius())
		    {
		      setDead(true);
		    }
		  }
	}
	

}
