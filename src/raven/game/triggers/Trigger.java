package raven.game.triggers;

import java.awt.Image;

import com.sun.xml.internal.stream.Entity;
import com.thoughtworks.xstream.annotations.XStreamAlias;

import raven.game.BaseGameEntity;
import raven.game.EntityManager;
import raven.game.RavenObject;
import raven.math.Vector2D;

@XStreamAlias("Trigger")
public abstract class Trigger<T extends BaseGameEntity> extends BaseGameEntity {
	private TriggerRegion regionOfInfluence;
	
	private boolean removeFromGame;
	
	private boolean active;
	
	private int graphNodeIndex;
	
	protected void setGraphNodeIndex(int index) { graphNodeIndex = index; }
	protected void setToBeRemovedFromGame() { removeFromGame = true; }
	protected void setInactive() { active = false; }
	protected void setActive() { active = true; }
	
	protected boolean isTouchingTrigger(Vector2D entityPos, double entityRadius) {
		if (regionOfInfluence != null) {
			return regionOfInfluence.isTouching(entityPos, entityRadius);
		}
		
		return false;
	}
	
	// Child classes use one of these methods to initialize the trigger region
	
	protected void addCircularTriggerRegion(Vector2D center, double radius) {
		regionOfInfluence = new TriggerRegionCircle(center, radius);
	}
	
	protected void addRectangularTriggerRegion(Vector2D topLeft, Vector2D bottomRight) {
		regionOfInfluence = new TriggerRegionRectangle(topLeft, bottomRight);
	}
	
	
	/**
	 * Create a new trigger.  These are weapon spawns, health spawns, etc, but NOT Spawn Points!
	 * @param id Pass in a unique id.
	 * @param img The image to be drawn.
	 * @param centerPoint Center point of the trigger.
	 * @param radius The radius of the trigger circle.
	 */
	public Trigger(Vector2D centerPoint, int radius) {
		super(EntityManager.getAvailableID(), RavenObject.TRIGGER);

		if(centerPoint != null) {
			regionOfInfluence = new TriggerRegionCircle(centerPoint, radius);
		}
		position = new Vector2D(centerPoint);
		removeFromGame = false;
		active = true;
	}
	
	
	public abstract void tryTrigger(T entity);
	
	public abstract void update(double delta);
	
	// Accessors
	
	public int graphNodeIndex() { return graphNodeIndex; }
	public boolean isToBeRemoved() { return removeFromGame; }
	public boolean isActive() { return active; }
}
