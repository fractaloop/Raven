package raven.game;

import raven.math.Vector2D;

public class RavenBot extends MovingEntity {

	private static double botScale = .8;
	private static double botMaxSpeed = 1;
	private static double botMass = 1;
	private static double botMaxForce = 1.0;
	private static double botMaxHeadTurnRate = .2;
	private static int botMaxHealth = 100;
	
	
	public RavenBot(RavenGame world, Vector2D position) {
		super(position, botScale, new Vector2D(0, 0),
				botMaxSpeed, new Vector2D(1, 0), 
				botMass, new Vector2D(botScale, botScale),
				botMaxHeadTurnRate, botMaxForce, world);
		// TODO Auto-generated constructor stub
	}

	public void increaseHealth(int healthGiven) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void render() {
		// TODO Auto-generated method stub
		
	}

}
