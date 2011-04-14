package raven.math.graph;

import java.io.Reader;

import raven.math.Vector2D;
import raven.utils.StreamUtils;

public class GraphEdge {
	// An edge connects two nodes. Valid node indices are always positive.
	protected int from;
	protected int to;

	// the cost of traversing the edge
	protected double cost;
	
	public GraphEdge(int from, int to, double cost) {
		this.cost = cost;
		this.from = from;
		this.to = to;
	}
	
	public GraphEdge(int from, int to) {
		this(from, to, 1.0);
	}
	
	public GraphEdge() {
		this(GraphNode.INVALID_NODE_INDEX, GraphNode.INVALID_NODE_INDEX, 1.0);
	}
	
	public GraphEdge(Reader reader) {
		@SuppressWarnings("unused")
		Object dummy;
		dummy = StreamUtils.getValueFromStream(reader);
		from = (Integer)StreamUtils.getValueFromStream(reader);
		dummy = StreamUtils.getValueFromStream(reader);
		to = (Integer)StreamUtils.getValueFromStream(reader);
		dummy = StreamUtils.getValueFromStream(reader);
		cost = (Double)StreamUtils.getValueFromStream(reader);
	}
	
	public Object clone() { return this.clone(); } 
	
	public int from() { return from; }
	public void setFrom(int newIndex) { from = newIndex; }
	
	public int to() { return to; }
	public void setTo(int newIndex) { to = newIndex; }
	
	public double cost() { return cost; }
	public void setCost(double newCost) { cost = newCost; }
	
	public boolean equals(Object other) {
		if (other instanceof GraphEdge) {
			GraphEdge edge = (GraphEdge)other;
			return from == edge.from && to == edge.to && cost == edge.cost;
		} else {
			return false;
		}
	}
	
	public String toString() {
		return "from: " + from + " to: " + to + " cost: " + cost + "\n"; 
	}

	public int doorID() {
		// TODO Auto-generated method stub
		return 0;
	}

	public Vector2D source() {
		// TODO Auto-generated method stub
		return null;
	}



	
}
