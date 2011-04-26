package raven.edit.graph;

import java.util.List;

import raven.game.RavenBot;
import raven.game.RavenMap;
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

	public GraphBuilder(RavenMap level) {
		this.setLevel(level);
	}
	
	public void rebuild() {
		SparseGraph<NavGraphNode<Trigger<RavenBot>>, NavGraphEdge> graph = level.getNavGraph();
		
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

	public void growFromSeed(NavGraphNode<Trigger<RavenBot>> node, List<Wall2D> walls) {
		System.out.println("growFromSeed");
	}

	public void setLevel(RavenMap level) { this.level = level; }
	public RavenMap getLevel() { return level; }
}
