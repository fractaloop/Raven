package raven.game.triggers;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import raven.game.BaseGameEntity;
import raven.game.RavenBot;

public class TriggerSystem<T extends Trigger> {
	
	private LinkedList<T> triggers;
	
	public TriggerSystem()
	{
		triggers = new LinkedList<T>();
	}
	
	private void updateTriggers(double delta) {
		HashSet<T> toRemove = new HashSet<T>();
		
		for(T trigger : triggers) {
			if (trigger.isToBeRemoved())
				toRemove.add(trigger);
			else
				trigger.update(delta);
		}
		
		triggers.removeAll(toRemove);
	}
	
	/** this method iterates through the container of entities passed as a
	 * parameter and passes each one to the Try method of each trigger
	 * *provided* the entity is alive and provided the entity is ready for a
	 * trigger update.
	 * @param entities
	 */

	private void tryTriggers(List<? extends BaseGameEntity> entities) {
		// test each entity against the triggers
		for (BaseGameEntity ent : entities) {
			// an entity must be ready for its next trigger update and it must
			// be alive before it is tested against each trigger.
			for (T trigger : triggers) {
				trigger.tryTrigger(ent);
			}
		}
	}
	
	public void clear() {
		triggers.clear();
	}
	
	public void update(double delta, List<? extends BaseGameEntity> bots) {
		updateTriggers(delta);
		tryTriggers(bots);
	}
	
	public void register(T trigger) {
		triggers.add(trigger);
	}
	
	public void render() {
		for (T trigger : triggers) {
			trigger.render();
		}
	}
	
	// Accessors
	
	public List<T> getTriggers() {
		return triggers;
	}
	
	@Override
	public boolean equals(Object o){
		if(this == o) return true;
		if(!(o instanceof TriggerSystem<?>)) return false;
		
		TriggerSystem<?> other = (TriggerSystem<?>) o;
		return this.triggers.equals(other.triggers);
	}
}
