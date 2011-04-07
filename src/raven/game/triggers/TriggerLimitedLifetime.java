package raven.game.triggers;

import raven.game.BaseGameEntity;

public abstract class TriggerLimitedLifetime<T extends BaseGameEntity> extends Trigger<T>{
	protected double lifetime;
	
	public TriggerLimitedLifetime(double lifetime) {
		super(getNextValidID());
		
		this.lifetime = lifetime;
	}
	
	@Override
	public void update(double delta) {
		lifetime -= delta;
		if (lifetime <= 0) {
			this.setToBeRemovedFromGame();
		}
	}
}
