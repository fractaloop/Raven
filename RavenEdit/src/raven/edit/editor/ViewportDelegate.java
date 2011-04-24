package raven.edit.editor;

import raven.game.RavenMap;
import raven.math.Vector2D;

public interface ViewportDelegate {
	public void updateStatus(String status);
	public void addWalls(Vector2D[] walls);
	
	public Viewport getViewport();
	public void setViewport(Viewport viewport);
	
	public RavenMap getLevel();
}
