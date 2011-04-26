package raven.game.triggers;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import raven.math.InvertedAABox2D;
import raven.math.Vector2D;

@XStreamAlias("TriggerRegionRectangle")
public class TriggerRegionRectangle implements TriggerRegion {

	private InvertedAABox2D trigger;

	public TriggerRegionRectangle(Vector2D topLeft, Vector2D bottomRight) {
		trigger = new InvertedAABox2D(topLeft, bottomRight);
	}
	
	@Override
	public boolean isTouching(Vector2D pos, double entityRadius) {
		InvertedAABox2D box = new InvertedAABox2D(new Vector2D(pos.x - entityRadius, pos.y - entityRadius),
							new Vector2D(pos.x + entityRadius, pos.y + entityRadius));
		
		return box.isOverlappedWith(trigger);
	}
}
