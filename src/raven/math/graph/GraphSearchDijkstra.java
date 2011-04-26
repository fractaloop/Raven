package raven.math.graph;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import java.util.Map.Entry;


public class GraphSearchDijkstra {
	
	private SparseGraph<? extends GraphNode, ? extends GraphEdge> graph;
	
	/** this vector contains the edges that comprise the shortest path tree -
	 * a directed subtree of the graph that encapsulates the best paths from
	 * every node on the SPT to the source node */
	private List<GraphEdge> shortestPathTree;
	
	/** this is indexed into by node index and holds the total cost of the
	 * best path found so far to the given node. For example,
	 * m_CostToNode.get(5) will hold the total cost of all the edges that
	 * comprise the best path to node 5, found so far in the search (if node 5
	 * is present and has been visited) */
	private List<Double> costToNode;
	
	/** this is an indexed (by node) vector of 'parent' edges leading to nodes
	 * connected to the SPT but that have not been added to the SPT yet. This
	 * is a little like the stack or queue used in BST and DST searches. */
	private List<GraphEdge> searchFrontier;
	
	int source;
	int target;
	
	private void search() {

		/* create an indexed priority queue that sorts smallest to largest
		 * (front to back).Note that the maximum number of elements the iPQ
		 * may contain is N. This is because no node can be represented on
		 * the queue more than once. */
		TreeMap<Double,Integer> queue = new TreeMap<Double,Integer>();
		
		// put the source node is not empty
		queue.put(0.0, source);
		
		while (!queue.isEmpty()) {
			// get lowest cost node from the queue. Don't forget, the return
			// value is a *node index*, not the node itself. This node is the
			// node not already on the SPT that is the closest to the source
			// node
			Entry<Double,Integer> queueEntry = queue.firstEntry();
			queue.remove(queueEntry.getKey());
			int nextClosestNode = queueEntry.getValue();
			
			// move this edge from the frontier to the shortest path tree
			shortestPathTree.set(nextClosestNode, searchFrontier.get(nextClosestNode));

			// if the target has been found exit
			if (nextClosestNode == target) {
				return;
			}
			
			// for each edge connected to the next closest node
			for (int i = 0; i < graph.getEdges(nextClosestNode).size(); i++) {
				GraphEdge edge = graph.getEdges(nextClosestNode).get(i);
				double newCost = costToNode.get(nextClosestNode) + edge.cost();
				
				// if this edge has never been on the frontier make a note of
				// the cost to get to the node it points to, then add the edge
				// to the frontier and the destination node to the PQ.
				if (searchFrontier.get(edge.to()) == null) {
					costToNode.set(edge.to(), newCost);
					searchFrontier.set(edge.to(), edge);
					queue.put(newCost, edge.to());
				}
				// else test to see if the cost to reach the destination node
				// via the current node is cheaper than the cheapest cost
				// found so far. If this path is cheaper, we assign the new
				// cost to the destination node, update its entry in the PQ to
				// reflect the change and add the edge to the frontier
				else if ( (newCost < costToNode.get(edge.to())) && shortestPathTree.get(edge.to()) == null) {
					costToNode.set(edge.to(), newCost);
					
					queue.remove(edge.cost());
					queue.put(newCost, edge.to());
					
					searchFrontier.set(edge.to(), edge);
				}

			}
		}
	}
	
	/**
	 * Create a new Dijkstra search tree of the given graph.
	 * @param graph the graph to compute on
	 * @param source the index in the graph of the node to start searching from
	 * @param target the index in the graph of the node to search to, or -1 if no target
	 */
	public GraphSearchDijkstra(SparseGraph<? extends GraphNode, ? extends GraphEdge> graph, int source, int target) {
		this.graph = graph;
		this.source = source;
		this.target = target;
		this.shortestPathTree = new ArrayList<GraphEdge>(graph.numNodes());
		this.searchFrontier = new ArrayList<GraphEdge>(graph.numNodes());
		this.costToNode = new ArrayList<Double>(graph.numNodes());
		
		// The algorithm requires the array's be filled with 0s
		for (int i = 0; i < graph.numNodes(); i++) {
			shortestPathTree.add(null);
			searchFrontier.add(null);
			costToNode.add(0.0);
		}
		
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
	public List<Integer> getPathToTarget() {
		List<Integer> path = new ArrayList<Integer>();
		
		// just return an empty path if no target or no path found
		if (target < 0) {
			return path;
		}
		
		int node = target;
		
		path.add(node);
		
		while (node != source && shortestPathTree.get(node) != null) {
			node = shortestPathTree.get(node).from();
			path.add(node);
		}
		
		return path;
	}
	
	/** returns the total cost to the target */
	public double getCostToTarget() { return costToNode.get(target); }
	
	/** returns the total cost to the given node */
	public double getCostToNode(int node) { return costToNode.get(node); }
}
