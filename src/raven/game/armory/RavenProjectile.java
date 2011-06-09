package raven.game.armory;

import java.util.ArrayList;
import java.util.List;

import raven.game.MovingEntity;
import raven.game.RavenBot;
import raven.game.RavenGame;
import raven.game.RavenObject;
import raven.game.interfaces.IRavenBot;
import raven.math.Geometry;
import raven.math.Vector2D;

public abstract class RavenProjectile extends MovingEntity {

	protected int shooterID;
	protected Vector2D vTarget;
	protected Vector2D origin;
	protected boolean isDead;
	protected boolean isImpacted;
	protected Vector2D impactPoint;
	protected RavenGame world;
	protected int damageInflicted;
	protected double timeSinceCreation;
	
	public RavenProjectile(Vector2D target,
						RavenGame world,
						int shooterID,
						Vector2D origin,
						Vector2D heading,
						int damage,
						double scale,
						double maxSpeed,
						double mass,
						double maxForce)
	{
		super(origin, scale, new Vector2D(), maxSpeed, heading, mass, new Vector2D(scale, scale), 0, maxForce);
		this.vTarget = target;
		this.isDead = false;
		this.isImpacted = false;
		this.impactPoint = new Vector2D();
		this.world = world;
		this.damageInflicted = damage;
		this.origin = origin;
		this.shooterID = shooterID;
		this.timeSinceCreation = 0.0;
	}

	protected IRavenBot GetClosestIntersectingBot(Vector2D from, Vector2D to)
	{
		IRavenBot closest = null;
		double closestDistance = Double.MAX_VALUE;
		for(IRavenBot bot : world.getBots())
		{
			// Make sure to not process this projectile's owner.
			if(bot.ID() != this.shooterID)
			{
				// Collision = Distance < botRadius
				if(Geometry.distToLineSegment(from, to, bot.pos()) < bot.getBRadius())
				{
					// See if this bot is closer than the current record holder.
					double distance = bot.pos().distanceSq(origin);
					if(distance < closestDistance)
					{
						closestDistance = distance;
						closest = bot;
					}
				}
			}
		}
		return closest;
	}
	
	protected List<IRavenBot> GetListOfIntersectingBots(Vector2D from, Vector2D to)
	{
		ArrayList<IRavenBot> bots = new ArrayList<IRavenBot>();
		for(IRavenBot bot : world.getBots())
		{
			if(bot.ID() != shooterID)
			{
				if(Geometry.distToLineSegment(from, to, bot.pos()) < bot.getBRadius())
				{
					bots.add(bot);
				}
			}
		}
		return bots;
	}

	public boolean IsDead() { return isDead; }
	public boolean HasImpacted() { return isImpacted; }
}
