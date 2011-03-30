/**
 * 
 */
package raven.game.armory;

import raven.game.RavenBot;
import raven.math.Vector2D;
import raven.ui.GameCanvas;

/**
 * @author chester
 *
 */
public class Bolt extends RavenProjectile {
	
	private static double boltMaxSpeed = 5.0;
	private static int boltMass = 1;
	private static double boltMaxForce = 100.0;
	private static Vector2D boltScale = new Vector2D(8, 10);
	private static int boltDamage = 1;
	private static double boltBlastRadius = 1;
	
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
				shooter.GetWorld()
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
				//TODO: Send message to let hit bot know it was hit, who hit it, and how much the hit was worth.
			}
			
			double dist = this.GetWorld().getDistanceToClosestWall(pos().sub(velocity), pos());
			if(dist == 0)
			{
				setDead(true);
				setImpacted(true);
				
				return;
			}
			
		}
	}
}
