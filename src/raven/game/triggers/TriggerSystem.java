package raven.game.triggers;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

public class TriggerSystem<T extends Trigger<?>> {
	
	private LinkedList<T> triggers;
	
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
	
	private void tryTriggers(List<T> entities) {
		// TODO Auto-generated method stub
	}
	
	public void clear() {
		triggers.clear();
	}
	
	public void update(double delta, List<T> entities) {
		updateTriggers(delta);
		tryTriggers(entities);
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
}
