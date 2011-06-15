package raven.edit.editor;

import raven.game.RavenBot;
import raven.game.RavenMap;
import raven.game.interfaces.IRavenBot;
import raven.game.triggers.Trigger;
import raven.math.Vector2D;

public interface ViewportDelegate {
	public void updateStatus(String status);
	public void addWalls(Vector2D[] walls);
	public void addTrigger(Trigger<IRavenBot> trigger);
	
	public Viewport getViewport();
	public void setViewport(Viewport viewport);
	
	public RavenMap getLevel();
}
