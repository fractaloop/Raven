package raven.edit.editor;

import raven.math.Vector2D;

public interface ViewportDelegate {
	public void updateStatus(String status);
	public void addWalls(Vector2D[] walls);
}
