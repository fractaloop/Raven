package raven.game.triggers;

import java.awt.Image;

import raven.game.BaseGameEntity;
import raven.game.interfaces.IRavenBot;
import raven.math.Vector2D;

public abstract class TriggerLimitedLifetime<T extends IRavenBot> extends Trigger<T>{
	protected double lifetime;
	
	public TriggerLimitedLifetime(Vector2D position, int radius, double lifetime, Image img) {
		super(position, radius);
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
