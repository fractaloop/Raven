package raven.game.triggers;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import raven.math.Vector2D;

@XStreamAlias("TriggerRegionCircle")
public class TriggerRegionCircle implements TriggerRegion {

	private double radius;
	private Vector2D pos;

	public TriggerRegionCircle(Vector2D pos, double radius) {
		this.radius = radius;
		this.pos = pos;
	}
	
	@Override
	public boolean isTouching(Vector2D entityPos, double entityRadius) {
		return pos.distanceSq(entityPos) < (entityRadius + radius) * (entityRadius + radius);
	}
}
