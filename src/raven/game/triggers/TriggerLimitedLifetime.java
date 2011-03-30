package raven.game.triggers;

public abstract class TriggerLimitedLifetime<T> extends Trigger<T>{
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
