package raven.game.armory;

import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.List;

import raven.game.MovingEntity;
import raven.game.RavenBot;
import raven.game.RavenGame;
import raven.math.Vector2D;

public class RavenProjectile extends MovingEntity {

	private double blastRadius;
	private int shooterID;
	private Vector2D vTarget;
	private Vector2D origin;
	private int damageInflicted;
	private boolean isDead;
	private boolean isImpacted;
	private Vector2D impactPoint;
	private double creationTime;
	private RavenGame game;
	
	public RavenProjectile(Vector2D position,
						double radius,
						Vector2D velocity,
						double maxSpeed,
						Vector2D heading,
						double mass,
						Vector2D scale,
						double turnRate,
						double maxForce,
						double blastRad,
						int damage,
						RavenGame world) 
	{
		super(position, radius, velocity, maxSpeed, heading, mass, scale, turnRate, maxForce);
		this.damageInflicted = damage;
		this.blastRadius = blastRad;	
		this.game = world;
	}

	// Fill these in later
	public void Write(){//TODO: autogen
	
	}
	
	public void Read(){//TODO: autogen
		
	}

	@Override
	public void render() {//TODO: autogen
	}
	
	public void update() { 
		//TODO: autogen
	}
	
	
	public void setDead(boolean death) { isDead = death; }
	public boolean IsDead()
	{
		return isDead;
	}

	public void setImpacted(boolean impacted) { isImpacted = impacted; }
	public boolean HasImpacted()
	{
		return isImpacted;
	}

	public RavenBot GetClosestIntersectingBot(Vector2D from, Vector2D to)
	{
		RavenBot closest = null;
		double closestDistance = Double.MAX_VALUE;
		for(RavenBot bot : game.getBots())
		{
			// Make sure to not process this projectile's owner.
			if(bot.ID() != this.shooterID)
			{
				// Collision = Distance < botRadius
				if(DistanceToLineSegment(from, to, bot.pos()) < bot.getBRadius())
				{
					// See if this bot is closer than the current record holder.
					double distance = VectorDistanceSquared(bot.pos(), origin);
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
	
	public List<RavenBot> GetListOfIntersectingBots(Vector2D from, Vector2D to)
	{
		ArrayList<RavenBot> bots = new ArrayList<RavenBot>();
		for(RavenBot bot : game.getBots())
		{
			if(bot.ID() != shooterID)
			{
				if(DistanceToLineSegment(from, to, bot.pos()) < bot.getBRadius())
				{
					bots.add(bot);
				}
			}
		}
		return bots;
	}
	
	// This call should be imported from elsewhere....
	private double DistanceToLineSegment(Vector2D from, Vector2D to, Vector2D position)
	{
		return 0.0;
	}
	
	// This call should be imported from elsewhere....
	private double VectorDistanceSquared(Vector2D position, Vector2D origin)
	{
		return 0.0;
	}
	
	public RavenGame GetWorld()
	{
		return game;
	}
	
	public int getShooterID()
	{
		return shooterID;
	}
	
	public Vector2D getOrigin()
	{
		return origin;
	}
	
	public void setImpactPoint(Vector2D iPoint)
	{
		impactPoint = iPoint;
	}
	
	public Vector2D getImpactPoint()
	{
		return impactPoint;
	}
	
	public double getBlastRadius(){
		return blastRadius;
	}
	
	public Vector2D getTarget(){
		return vTarget;
	}
}
