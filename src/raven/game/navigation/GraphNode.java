package raven.game.navigation;

import java.io.Reader;

import raven.utils.StreamUtils;

public class GraphNode {
	public static final int INVALID_NODE_INDEX = -1;
	
	protected int index;
	
	public GraphNode() { index = INVALID_NODE_INDEX; }
	public GraphNode(int index) { this.index = index; }
	public GraphNode(Reader reader) {
		index = (Integer)StreamUtils.getValueFromStream(reader);
	}
	
	public int index() { return index; }
	public void setIndex(int index) { this.index = index; }
	
	public String toString() { return "Index: " + index; }
}
