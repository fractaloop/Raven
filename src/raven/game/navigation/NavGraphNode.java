package raven.game.navigation;

import java.io.Reader;

import raven.math.Vector2D;

public class NavGraphNode<T> extends GraphNode implements GraphNodeFactory<NavGraphNode<T>> {
	protected Vector2D position;
	
	protected T extraInfo;
	
	public NavGraphNode() {
		extraInfo = null;
	}

	public NavGraphNode(int index, Vector2D pos) {
		super(index);
		position = pos;
	}

	public NavGraphNode(Reader reader) {
		super(reader);
		// TODO Auto-generated constructor stub
	}
	
	// Accessors
	
	public Vector2D pos() { return position; }
	public void setPos(Vector2D newPosition) { position = newPosition; }
	
	public T extraInfo() { return extraInfo; }
	public void setExtraInfo(T info) { extraInfo = info; }
	
	public String toString() {
		return "Index: " + this.index + " PosX: " + position.x + " PosY: " + position.y + "\n";
	}

	@Override
	public NavGraphNode<T> createInstance(Reader reader) {
		return new NavGraphNode<T>(reader);
	}

}
