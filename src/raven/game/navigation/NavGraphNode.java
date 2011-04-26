package raven.game.navigation;

import java.io.Reader;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import raven.math.Vector2D;
import raven.math.graph.GraphEdge;
import raven.math.graph.GraphNode;

@XStreamAlias("NavGraphNode")
public class NavGraphNode<T> extends GraphNode {
	protected T extraInfo;
	
	public NavGraphNode() {
		extraInfo = null;
	}

	public NavGraphNode(int index, Vector2D pos) {
		super(index);
		position = pos;
	}
	
	public NavGraphNode(NavGraphNode<T> copy) {
		position = copy.position;
		index = copy.index;
		extraInfo = copy.extraInfo;
	}

	public NavGraphNode<T> clone() { return new NavGraphNode<T>(this); }
	
	// Accessors
	
	public T extraInfo() { return extraInfo; }
	public void setExtraInfo(T info) { extraInfo = info; }
	
	public String toString() {
		return "Index: " + this.index + " PosX: " + position.x + " PosY: " + position.y + "\n";
	}
}
