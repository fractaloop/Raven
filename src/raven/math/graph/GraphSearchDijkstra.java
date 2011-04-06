package raven.math.graph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.TreeSet;


public class GraphSearchDijkstra {
	
	private SparseGraph graph;
	
	/** this vector contains the edges that comprise the shortest path tree -
	 * a directed subtree of the graph that encapsulates the best paths from
	 * every node on the SPT to the source node */
	private List<GraphEdge> shortestPathTree;
	
	/** this is indexed into by node index and holds the total cost of the
	 * best path found so far to the given node. For example,
	 * m_CostToNode.get(5) will hold the total cost of all the edges that
	 * comprise the best path to node 5, found so far in the search (if node 5
	 * is present and has been visited) */
	private Map<GraphNode, Double> costToNode;
	
	/** this is an indexed (by node) vector of 'parent' edges leading to nodes
	 * connected to the SPT but that have not been added to the SPT yet. This
	 * is a little like the stack or queue used in BST and DST searches. */
	private List<GraphEdge> searchFrontier;
	
	GraphNode source;
	GraphNode target;
	
	private void search() {

		/* create an indexed priority queue that sorts smallest to largest
		 * (front to back).Note that the maximum number of elements the iPQ
		 * may contain is N. This is because no node can be represented on
		 * the queue more than once. */
		PriorityQueue<GraphNode> queue = new PriorityQueue<GraphNode>();
		
		// put the source node is not empty
		queue.add(source);
		
		while (!queue.isEmpty()) {
			// Get the lowest cost node from the queue
			GraphNode nextClosestNode = queue.remove();
			
			// move this edge from the frontier to the SPT
//			shortestPathTree
			
			// if the target has been found exit
			if (nextClosestNode.equals(target)) {
				return;
			}
			
			// now relax the edges
//			for (GraphNode neighbor : graph.get)
		}
	}
	
	public GraphSearchDijkstra(SparseGraph graph, GraphNode source, GraphNode target) {
		this.graph = graph;
		this.source = source;
		this.target = target;
		this.shortestPathTree = new ArrayList<GraphEdge>(graph.numNodes());
		this.searchFrontier = new ArrayList<GraphEdge>(graph.numNodes());
		this.costToNode = new HashMap<GraphNode, Double>(graph.numNodes(), 1.0f);
		
		search();
	}
	
	/** returns the vector of edges that defines the SPT. If a target was
	 * given in the constructor then this will be an SPT comprising of all the
	 * nodes examined before the target was found, else it will contain all
	 * the nodes in the graph. */
	public List<GraphEdge> getSPT() { return shortestPathTree; }
	
	/** returns a vector of nodes that comprise the shortest path from the
	 * source to the target. It calculates the path by working backwards
	 * through the SPT from the target node. */
	public List<GraphNode> getPathToTarget() {
		List<GraphNode> path = new ArrayList<GraphNode>();
		
		// just return an empty path if no target or no path found
		if (target == null) {
			return path;
		}
		
		GraphNode node = target;
		
		path.add(node);
		
		while (node != source && shortestPathTree.get(shortestPathTree.indexOf(node)) != null) {
//			GraphNode node = shortestPathTree.get(shortestPathTree.indexOf(node)).from();
			// TODO
		}
		
		return Collections.emptyList();
	}
	
	/** returns the total cost to the target */
	public double getCostToTarget() { return 0; } // TODO
	
	/** returns the total cost to the given node */
	public double getCostToNode(GraphNode node) { return 0; } // TODO
}
