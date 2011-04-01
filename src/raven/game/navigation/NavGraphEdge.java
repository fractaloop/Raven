package raven.game.navigation;

import java.io.Reader;

import raven.utils.StreamUtils;

public class NavGraphEdge extends GraphEdge implements GraphEdgeFactory<NavGraphEdge> {
	public static final int NORMAL	= 0;
	public static final int SWIM	= 1 << 0;
	public static final int CRAWL	= 1 << 1;
	public static final int CREEP	= 1 << 2;
	public static final int JUMP	= 1 << 3;
	public static final int FLY		= 1 << 4;
	public static final int GRAPPLE	= 1 << 5;
	public static final int GOES_THROUGH_DOOR = 1 << 6;
	
	protected int flags;
	
	/** if this edge intersects with an object (such as a door or lift), then
	 * this is that object's ID. */
	protected int IDOfIntersectingEntity;
	
	public NavGraphEdge(int from, int to, double cost, int flags, int id) {
		super(from, to, cost);
		this.flags = flags;
		this.IDOfIntersectingEntity = id;
	}
	public NavGraphEdge(int from, int to, double cost, int flags) {
		this(from, to, cost, flags, GraphNode.INVALID_NODE_INDEX);
	}
	public NavGraphEdge(int from, int to, double cost) {
		this(from, to, cost, 0, GraphNode.INVALID_NODE_INDEX);
	}
	public NavGraphEdge() {
		this(GraphNode.INVALID_NODE_INDEX, GraphNode.INVALID_NODE_INDEX, 0);
	}

	public NavGraphEdge(Reader reader) {
		Object dummy;
		
		dummy = StreamUtils.getValueFromStream(reader);
		from = (Integer)StreamUtils.getValueFromStream(reader);
		dummy = StreamUtils.getValueFromStream(reader);
		to = (Integer)StreamUtils.getValueFromStream(reader);
		dummy = StreamUtils.getValueFromStream(reader);
		cost = (Double)StreamUtils.getValueFromStream(reader);
		dummy = StreamUtils.getValueFromStream(reader);
		flags = (Integer)StreamUtils.getValueFromStream(reader);
		dummy = StreamUtils.getValueFromStream(reader);
		IDOfIntersectingEntity = (Integer)StreamUtils.getValueFromStream(reader);
	}

	public int flags() { return flags; }
	public void setFlags(int flags) { this.flags = flags; }
	public int IDOfIntersectingEntity() { return IDOfIntersectingEntity; }
	public void setIDOfIntersectingEntity(int iDOfIntersectingEntity) { IDOfIntersectingEntity = iDOfIntersectingEntity; }
	
	public String toString() {
		return "from: " + from + " to: " + to + " cost: " + cost + " flags: " + flags + " ID: " + IDOfIntersectingEntity + "\n";
	}
	
	@Override
	public NavGraphEdge createInstance(Reader reader) {
		return new NavGraphEdge(reader);
	}
}
