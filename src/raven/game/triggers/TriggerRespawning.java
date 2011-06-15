package raven.game.triggers;

import raven.game.BaseGameEntity;
import raven.game.interfaces.IRavenBot;
import raven.math.Vector2D;

public abstract class TriggerRespawning<T extends IRavenBot> extends Trigger<T> {
	protected double numSecondsBetweenRespawns;
	transient protected double numSecondsRemainingUntilRespawn;
	
	private Object readResolve() {
		numSecondsRemainingUntilRespawn = 0;
		return this;
	}
	
	protected void deactivate() {
		this.setInactive();
		numSecondsRemainingUntilRespawn = numSecondsBetweenRespawns;
	}
	
	public TriggerRespawning(Vector2D position, int radius) {
		super(position, radius);
		numSecondsRemainingUntilRespawn = 0;
	}
	
	/**
	 * If this trigger is inactive, we will decrement the amount of time left it has.  Once we decrement, we can see if 
	 * it should be activated!
	 */
	@Override
	public void update(double delta) {
		if(!isActive()){
			numSecondsRemainingUntilRespawn -= delta;
			if((numSecondsRemainingUntilRespawn <= 0) && !isActive()) {
				this.setActive();
				numSecondsRemainingUntilRespawn = 0;
			}
		}
	}
	
	public void setRespawnDelay(double seconds) {
		numSecondsBetweenRespawns = seconds;
	}
}
