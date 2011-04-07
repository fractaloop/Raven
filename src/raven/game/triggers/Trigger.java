package raven.game.triggers;

import raven.game.BaseGameEntity;
import raven.math.Vector2D;
	
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
	
	public Trigger(int id) {
		super(id);
		
		removeFromGame = false;
		active = true;
		graphNodeIndex = -1;
		regionOfInfluence = null;
	}
	
	public abstract void tryTrigger(T entity);
	
	public abstract void update(double delta);
	
	// Accessors
	
	public int graphNodeIndex() { return graphNodeIndex; }
	public boolean isToBeRemoved() { return removeFromGame; }
	public boolean isActive() { return active; }
}
