package raven.game.triggers;

import raven.math.Vector2D;

public interface TriggerRegion {
	public boolean isTouching(Vector2D entityPos, double entityRadius);
}
