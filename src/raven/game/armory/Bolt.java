/**
 * 
 */
package raven.game.armory;

import com.sun.org.apache.bcel.internal.generic.NEW;

import raven.game.RavenBot;
import raven.game.messaging.Dispatcher;
import raven.game.messaging.RavenMessage;
import raven.math.Vector2D;
import raven.script.RavenScript;
import raven.ui.GameCanvas;

/**
 * @author chester
 *
 */
public class Bolt extends RavenProjectile {
	
	private static double boltMaxSpeed = RavenScript.getDouble("Bolt_MaxSpeed");
	private static int boltMass = RavenScript.getInt("bolt_mass");
	private static double boltMaxForce = RavenScript.getDouble("bolt_maxforce");
	private static Vector2D boltScale = new Vector2D(RavenScript.getDouble("bolt_scale"), RavenScript.getDouble("bolt_scale"));
	private static int boltDamage = RavenScript.getInt("bolt_damage");
	private static double boltBlastRadius = 1; //TODO: Bolt has no blast radius, what is default?
	
	
	public Bolt(RavenBot shooter, Vector2D target)
	{
		super(target, //Where we are pointing
				0.0, // bounding radius
				shooter.velocity(), //inherited velocity
				boltMaxSpeed, //max speed
				shooter.heading(),
				boltMass,  //bolt mass
				boltScale, // bolt scale
				shooter.maxTurnRate(),
				boltMaxForce, //bolt max force
				boltBlastRadius,
				boltDamage,
				shooter.getWorld()
			);	
	}

	/* (non-Javadoc)
	 * @see raven.game.BaseGameEntity#render()
	 */
	@Override
	public void render() {
		GameCanvas.thickGreenPen();
		GameCanvas.line(this.pos(), this.pos().sub(this.velocity()));
	}
	
	public void update()
	{
		if(!HasImpacted())
		{
			this.setVelocity(this.heading().mul(this.maxSpeed()));
			this.velocity().truncate(this.maxSpeed());
			
			RavenBot hit = GetClosestIntersectingBot(pos().sub(velocity), pos());
			if(hit != null)
			{
				this.setDead(true);
				this.setImpacted(true);
				Dispatcher.dispatchMsg(Dispatcher.SEND_MSG_IMMEDIATELY, this.getShooterID(), hit.ID(), RavenMessage.MSG_TAKE_THAT_MF, boltDamage);
			}
			
			double dist = ((RavenProjectile)this).GetWorld().getDistanceToClosestWall(pos().sub(velocity), pos());
			if(dist == 0)
			{
				setDead(true);
				setImpacted(true);
				
				return;
			}
			
		}
	}
}
