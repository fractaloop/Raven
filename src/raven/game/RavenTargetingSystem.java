package raven.game;

import java.util.ArrayList;
import java.util.List;

import raven.game.interfaces.IRavenBot;
import raven.game.interfaces.IRavenTargetingSystem;
import raven.math.Vector2D;

public class RavenTargetingSystem implements IRavenTargetingSystem{
	
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
		List<IRavenBot> validTargets = removeTeammatesFromSensed(sensedBots, owner.getTeam().ID());
		
		for (IRavenBot opponent : validTargets) {
			if (opponent.isAlive() && !opponent.equals(owner)) {
		//	if ((opponent.isAlive() && !opponent.equals(owner)) && opponent.getTeam().ID() != owner.getTeam().ID()) {
				double dist = opponent.pos().distanceSq(owner.pos());
				
				if (dist < closestDistSoFar) {
					closestDistSoFar = dist;
					currentTarget = opponent;
				}
			}
		}
	}
	
	private List<IRavenBot> removeTeammatesFromSensed(List<IRavenBot> sensedBots, int selfTeam) {
		// TODO Auto-generated method stub
		List<IRavenBot> returnList = new ArrayList<IRavenBot>();
		for(IRavenBot botToCheck : sensedBots)
		{
		if(botToCheck.getTeam().ID() != selfTeam){
			returnList.add(botToCheck);
		}
		}
		
		return returnList;
	}

	public boolean isTargetPresent() {
		return currentTarget != null;
	}
	
	public boolean isTargetWithinFOV() {
		return owner.getSensoryMem().isOpponentWithinFOV(currentTarget);
	}
	
	public boolean isTargetShootable() {
		///Now doesn't detects team
		//boolean isShootable = owner.getSensoryMem().isOpponentShootable(currentTarget);
		//boolean notSameTeam = (owner.getTeam() != currentTarget.getTeam());
		//return (notSameTeam && owner.getSensoryMem().isOpponentShootable(currentTarget));
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
