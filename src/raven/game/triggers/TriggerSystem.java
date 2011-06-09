package raven.game.triggers;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

import raven.game.BaseGameEntity;
import raven.game.interfaces.IRavenBot;

@XStreamAlias("TriggerSystem")
public class TriggerSystem<T extends Trigger> {

	@XStreamImplicit
	private LinkedList<T> triggers;
	
	public TriggerSystem() {
		triggers = new LinkedList<T>();
	}
	
	private Object readResolve() {
		if (triggers == null) {
			triggers = new LinkedList<T>();
		}
		
		return this;
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

	private void tryTriggers(List<IRavenBot> entities) {
		// test each entity against the triggers
		for (IRavenBot ent : entities) {
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
	
	public void update(double delta, List<IRavenBot> bots) {
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
