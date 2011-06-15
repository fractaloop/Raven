package raven.edit.graph;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

import raven.game.RavenBot;
import raven.game.RavenMap;
import raven.game.interfaces.IRavenBot;
import raven.game.navigation.NavGraphEdge;
import raven.game.navigation.NavGraphNode;
import raven.game.triggers.Trigger;
import raven.math.Vector2D;
import raven.math.Wall2D;
import raven.math.WallIntersectionTest;
import raven.math.graph.GraphNode;
import raven.math.graph.SparseGraph;
import raven.script.RavenScript;

public class GraphBuilder {

	private RavenMap level;
	
	protected double nodeSpacing = RavenScript.getDouble("DefaultNodeSpacing");
	protected double maxEdgeLength = RavenScript.getDouble("DefaultMaxEdgeLength");
	protected double nodeMargin = RavenScript.getDouble("NodeMargin");

	public GraphBuilder(RavenMap level) {
		this.setLevel(level);
	}
	
	public void rebuild() {
		SparseGraph<NavGraphNode<Trigger<IRavenBot>>, NavGraphEdge> graph = level.getNavGraph();
		
		graph.removeEdges();
		
		for (int i = 0; i < graph.numNodes(); i++) {
			// j = 0 if this is a digraph!
			for (int j = i+1; j < graph.numNodes(); j++) {
				Vector2D from = graph.getNode(i).pos();
				Vector2D to = graph.getNode(j).pos();
				double distanceSq = from.distanceSq(to);
				// It must be within range and it must not be obstructed by a wall
				if (distanceSq < maxEdgeLength * maxEdgeLength && !WallIntersectionTest.doWallsObstructLineSegment(from, to, level.getWalls())) {
					graph.addEdge(new NavGraphEdge(i, j, Math.sqrt(distanceSq)));
				}
			}
		}
	}

	public double getMaxEdgeLength() { return maxEdgeLength; }
	public void setMaxEdgeLength(double maxEdgeLength) { this.maxEdgeLength = maxEdgeLength; }

	protected boolean isValidNodeLocation(Vector2D position, List<Wall2D> walls) {
		return (position.x > 0 && position.x < level.getSizeX() && position.y > 0 && position.y < level.getSizeY() &&
				!WallIntersectionTest.doWallsIntersectCircle(walls, position, nodeMargin));
	}
	
	public void growFromSeed() {
		SparseGraph<NavGraphNode<Trigger<IRavenBot>>, NavGraphEdge> graph = level.getNavGraph();
		
		// Only 1 seed allowed!
		if (graph.numNodes() != 1)
			return;
		
		// Seed node
		NavGraphNode<Trigger<IRavenBot>> seed_node = graph.getNode(0);
		
		// Start the graph over!
		graph.clear();
		
		// Queue through every position, adding NSEW nodes at nodeSpacing distance
		LinkedBlockingQueue<Vector2D> queue = new LinkedBlockingQueue<Vector2D>();
		ArrayList<Vector2D> processed = new ArrayList<Vector2D>();
		
		queue.add(seed_node.pos());
		
		while (!queue.isEmpty()) {
			// Mark this location as completed
			Vector2D location = queue.remove();
			processed.add(location);
			
			// Scanline method
			boolean spanLeft = false;
			boolean spanRight = false;
			
			// Rewind up to the highest point
			Vector2D cursor = location.add(new Vector2D(0, -nodeSpacing));
			while (isValidNodeLocation(cursor, level.getWalls()) && !WallIntersectionTest.doWallsObstructLineSegment(location, cursor, level.getWalls()))
				cursor = cursor.add(new Vector2D(0, -nodeSpacing));
			
			// Go forward and start building
			cursor = cursor.add(new Vector2D(0, nodeSpacing));
			
			// Add nodes going right until we hit a boundary
			while (isValidNodeLocation(cursor, level.getWalls()) && !WallIntersectionTest.doWallsObstructLineSegment(location, cursor, level.getWalls())) {
				Vector2D down = cursor.add(new Vector2D(0, nodeSpacing));
				Vector2D left = cursor.add(new Vector2D(-nodeSpacing, 0));
				Vector2D right = cursor.add(new Vector2D(nodeSpacing, 0));

				graph.addNode(new NavGraphNode<Trigger<IRavenBot>>(graph.getNextFreeNodeIndex(), cursor));
				processed.add(cursor);
				
				if (!spanLeft && isValidNodeLocation(left, level.getWalls()) && !WallIntersectionTest.doWallsObstructLineSegment(location, left, level.getWalls()) && !processed.contains(left)) {
					// Span left if we aren't already spanning, the location is valid, and there are no obstructions
					spanLeft = true;
					queue.add(left);
				} else if (spanLeft && (!isValidNodeLocation(left, level.getWalls()) || WallIntersectionTest.doWallsObstructLineSegment(location, left, level.getWalls()))) {
					// Stop spanning left if we hit an invalid location or a wall
					spanLeft = false;
				}

				if (!spanRight && isValidNodeLocation(right, level.getWalls()) && !WallIntersectionTest.doWallsObstructLineSegment(location, right, level.getWalls()) && !processed.contains(right)) {
					// Span right if we aren't already spanning, the location is valid, and there are no obstructions
					spanRight = true;
					queue.add(right);
				} else if (spanRight && (!isValidNodeLocation(right, level.getWalls()) || WallIntersectionTest.doWallsObstructLineSegment(location, right, level.getWalls()))) {
					// Stop spanning right if we hit an invalid location or a wall
					spanRight = false;
				}
				cursor = down;
			}

		}
		
		// Oh, and finally we need a node under every trigger
		for (Trigger<IRavenBot> trigger : level.getTriggers()) {
			NavGraphNode<Trigger<IRavenBot>> node = new NavGraphNode<Trigger<IRavenBot>>(graph.getNextFreeNodeIndex(), trigger.pos());
			trigger.setGraphNodeIndex(node.index());
			node.setExtraInfo(trigger);
			graph.addNode(node);
		}
		
		rebuild();
	}

	public void setLevel(RavenMap level) { this.level = level; }
	public RavenMap getLevel() { return level; }
}
