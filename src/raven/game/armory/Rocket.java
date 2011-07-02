package raven.game.armory;

import raven.game.RavenBot;
import raven.game.interfaces.IRavenBot;
import raven.game.messaging.Dispatcher;
import raven.game.messaging.RavenMessage;
import raven.math.Geometry;
import raven.math.Vector2D;
import raven.script.RavenScript;
import raven.ui.GameCanvas;

public class Rocket extends RavenProjectile {

	private double blastRadius; 
	private double currentBlastRadius;

	public Rocket(IRavenBot shooter, Vector2D target) {
		super(target,
				shooter.getWorld(),
				shooter.ID(),
				shooter.pos(),
				shooter.facing(),
				RavenScript.getInt("Rocket_Damage"),
				RavenScript.getDouble("Rocket_Scale"),
				RavenScript.getDouble("Rocket_MaxSpeed"),
				RavenScript.getInt("Rocket_Mass"),
				RavenScript.getDouble("Rocket_MaxForce")
		);	
		currentBlastRadius = 0.0;
		blastRadius = RavenScript.getDouble("Rocket_BlastRadius");
	}

	private void InflictDamageOnBotsWithinBlastRadius(){
		for (IRavenBot curBot : world.getBots())
		{
			if (position.distanceSq(curBot.pos()) < (blastRadius + curBot.getBRadius()))
			{
				//send a message to the bot to let it know it's been hit, and who the
				//shot came from
				Dispatcher.dispatchMsg(Dispatcher.SEND_MSG_IMMEDIATELY,
						shooterID,
						curBot.ID(),
						RavenMessage.MSG_TAKE_THAT_MF,
						damageInflicted);

			}
		}  
	}

	private void TestForImpact(){

		//if the projectile has reached the target position or it hits an entity
		//or wall it should explode/inflict damage/whatever and then mark itself
		//as dead


		//test to see if the line segment connecting the rocket's current position
		//and previous position intersects with any bots.
		IRavenBot hit = GetClosestIntersectingBot(pos().sub(velocity), pos());

		//if hit
		if (hit != null)
		{
			isImpacted = true;

			//send a message to the bot to let it know it's been hit, and who the
			//shot came from
			Dispatcher.dispatchMsg(Dispatcher.SEND_MSG_IMMEDIATELY,
					shooterID,
					hit.ID(),
					RavenMessage.MSG_TAKE_THAT_MF,
					damageInflicted);

			//test for bots within the blast radius and inflict damage
			InflictDamageOnBotsWithinBlastRadius();
		}

		//test for impact with a wall

		Double dist = Geometry.FindClosestPointOfIntersectionWithWalls(position.sub(velocity), position, impactPoint, world.getMap().getWalls());
		if (dist != null)
		{
			isImpacted = true;

			//test for bots within the blast radius and inflict damage
			InflictDamageOnBotsWithinBlastRadius();

			position = impactPoint;

			return;
		}

		//test to see if rocket has reached target position. If so, test for
		//all bots in vicinity
		final double tolerance = 5.0;   
		if (pos().distanceSq(vTarget) < tolerance*tolerance)
		{
			isImpacted = true;

			InflictDamageOnBotsWithinBlastRadius();
		}

	}

	public void render(){
		GameCanvas.redPen();
		GameCanvas.orangeBrush();
		GameCanvas.circle(pos(), 2);

		if (isImpacted)
		{
			GameCanvas.hollowBrush();
			GameCanvas.circle(pos(), currentBlastRadius);
		}
	}

	public void update(){
		if (!isImpacted)
		{
			velocity = heading().mul(maxSpeed());

			//make sure vehicle does not exceed maximum velocity
			velocity.truncate(maxSpeed());

			//update the position
			position = position.add(velocity);

			TestForImpact();  
		}

		else
		{
			currentBlastRadius += RavenScript.getDouble("Rocket_ExplosionDecayRate");

			//when the rendered blast circle becomes equal in size to the blast radius
			//the rocket can be removed from the game
			if (currentBlastRadius > blastRadius)
			{
				isDead = true;
			}
		}
	}


}
