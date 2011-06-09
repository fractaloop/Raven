package raven.game;

import java.util.List;

import raven.game.interfaces.IRavenBot;
import raven.math.Vector2D;

public class RavenTargetingSystem {
	
	private IRavenBot owner;
	
	private IRavenBot currentTarget;

	public RavenTargetingSystem(RavenBot owner) {
		this.owner = owner;
	}

	public void update() {
		double closestDistSoFar = Double.MAX_VALUE;
		currentTarget = null;
		
		// grab a list of all the opponents the owner can sense
		List<IRavenBot> sensedBots = owner.getSensoryMem().getListOfRecentlySensedOpponents();
		
		for (IRavenBot opponent : sensedBots) {
			if (opponent.isAlive() && !opponent.equals(owner)) {
				double dist = opponent.pos().distanceSq(owner.pos());
				
				if (dist < closestDistSoFar) {
					closestDistSoFar = dist;
					currentTarget = opponent;
				}
			}
		}
	}
	
	public boolean isTargetPresent() {
		return currentTarget != null;
	}
	
	public boolean isTargetWithinFOV() {
		return owner.getSensoryMem().isOpponentWithinFOV(currentTarget);
	}
	
	public boolean isTargetShootable() {
		return owner.getSensoryMem().isOpponentShootable(currentTarget);
	}
	
	public Vector2D getLastRecordedPosition() {
		return owner.getSensoryMem().getLastRecordedPositionOfOpponent(currentTarget);
	}
	
	public double getTimeTargetHasBeenVisible() {
		return owner.getSensoryMem().getTimeOpponentHasBeenVisible(currentTarget);
	}
	
	public double getTimeTargetHasBeenOutOfView() {
		return owner.getSensoryMem().getTimeOpponentHasBeenOutOfView(currentTarget);
	}

	public IRavenBot getTarget() {
		return currentTarget;
	}
	
	public void clearTarget() {
		currentTarget = null;
	}
}
