package raven.game.navigation;

import java.io.Reader;

public class GraphNode {
	protected int index;
	
	public static int INVALID_NODE_INDEX = -1;
	
	public GraphNode() {
		index = INVALID_NODE_INDEX;
	}
	
	public GraphNode(int index) {
		this.index = index;
	}
	
	public GraphNode(Reader reader) {
		// TODO
		//char buffer[50]; stream >> buffer >> m_iIndex;
	}
	
	public int index() { return index; }
	public void setIndex(int newIndex) { index = newIndex; }
	
	public String toString() {
		return "Index: " + index + "\n";
	}
}
