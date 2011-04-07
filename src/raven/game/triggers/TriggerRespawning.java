package raven.game.triggers;

import raven.game.BaseGameEntity;

public abstract class TriggerRespawning<T extends BaseGameEntity> extends Trigger<T> {
	protected double numSecondsBetweenRespawns;
	protected double numSecondsRemainingUntilRespawn;
	
	protected void deactivate() {
		this.setInactive();
		
		numSecondsRemainingUntilRespawn = numSecondsBetweenRespawns;
	}
	
	public TriggerRespawning(int id) {
		super(id);
		
		numSecondsBetweenRespawns = 0;
		numSecondsRemainingUntilRespawn = 0;
	}
	
	@Override
	public void update(double delta) {
		numSecondsRemainingUntilRespawn -= delta;
		if((numSecondsRemainingUntilRespawn <= 0) && !isActive()) {
			this.setActive();
		}
	}
	
	public void setRespawnDelay(double seconds) {
		numSecondsBetweenRespawns = seconds;
	}
}
