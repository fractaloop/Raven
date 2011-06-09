package raven.game.navigation;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import raven.game.RavenBot;
import raven.game.RavenObject;
import raven.game.interfaces.IRavenBot;
import raven.game.triggers.Trigger;
import raven.math.graph.EuclideanHeuristic;
import raven.math.graph.GraphNode;
import raven.math.graph.GraphSearchStatus;
import raven.math.graph.GraphSearchType;
import raven.math.graph.Heuristic;
import raven.math.graph.SparseGraph;
import raven.utils.IndexedPriorityQueue;

public class GraphSearchDijkstraTS<T extends SparseGraph<NavGraphNode<Trigger<IRavenBot>>, NavGraphEdge>> extends GraphSearchTimeSliced<NavGraphEdge> {

	private T graph;

	/** indexed into my node. Contains the accumulative cost to that node */
	private List<Double> costToThisNode;


	private List<NavGraphEdge> shortestPathTree;
	private List<NavGraphEdge> searchFrontier;

	private int source;
	private int targetNode;
	private RavenObject target;

	private IndexedPriorityQueue<Double> queue;

	private GraphSearchTermination<SparseGraph<NavGraphNode<Trigger<?>>, NavGraphEdge>> termination;

	public GraphSearchDijkstraTS(T graph, int source, RavenObject target) {
		super(GraphSearchType.Dijkstra);

		this.graph = graph;

		costToThisNode = new ArrayList<Double>(graph.numNodes());
		shortestPathTree = new ArrayList<NavGraphEdge>(graph.numNodes());
		searchFrontier = new ArrayList<NavGraphEdge>(graph.numNodes());

		// Cost arrays need to be filled
		for (int i = 0; i < graph.numNodes(); i++) {
			costToThisNode.add(0.0);
			shortestPathTree.add(null);
			searchFrontier.add(null);
		}

		this.source = source;
		this.target = target;
		this.targetNode = GraphNode.INVALID_NODE_INDEX;

		// TreeMap functions as a priority queue
		queue = new IndexedPriorityQueue<Double>(costToThisNode, graph.numNodes());

		queue.insert(source);
	}

	/** When called, this method pops the next node off the PQ and examines
	 * all its edges. The method returns an enumerated value (target_found,
	 * target_not_found, search_incomplete) indicating the status of the
	 * search */
	@Override
	public GraphSearchStatus cycleOnce() {
		//if the PQ is empty the target has not been found
		if (queue.isEmpty()) {
			return GraphSearchStatus.TARGET_NOT_FOUND;
		}

		//get lowest cost node from the queue
		int nextClosestNode = queue.pop();

		//move this node from the frontier to the spanning tree
		shortestPathTree.set(nextClosestNode, searchFrontier.get(nextClosestNode));

		//if the target has been found exit
		NavGraphNode<Trigger<IRavenBot>> node = graph.getNode(nextClosestNode);
		if (node.extraInfo() != null && node.extraInfo().isActive() && node.extraInfo().entityType() == target) {
			targetNode = nextClosestNode;
			
			return GraphSearchStatus.TARGET_FOUND;
		}

		//now to test all the edges attached to this node
		for (NavGraphEdge edge : graph.getEdges(nextClosestNode)) {
			// calculate the heuristic cost from this node to the target (H)
			double newCost = costToThisNode.get(nextClosestNode) + edge.cost();

			// if the node has not been added to the frontier, add it and
			// update the G and F costs
			if (searchFrontier.get(edge.to()) == null) {
				costToThisNode.set(edge.to(), newCost);
				
				queue.insert(edge.to());

				searchFrontier.set(edge.to(), edge);
			}
			//if this node is already on the frontier but the cost to get here
			//is cheaper than has been found previously, update the node
			//costs and frontier accordingly.
			else if (newCost < costToThisNode.get(edge.to()) && shortestPathTree.get(edge.to()) == null) {
				costToThisNode.set(edge.to(), newCost);

				queue.changePriority(edge.to());

				searchFrontier.set(edge.to(), edge);
			}
		}

		//there are still nodes to explore
		return GraphSearchStatus.SEARCH_INCOMPLETE;
	}

	/** returns the vector of edges that the algorithm has examined */
	@Override
	public List<NavGraphEdge> getSPT() {
		return shortestPathTree;
	}

	/** returns the total cost to the target */
	@Override
	public double getCostToTarget() {
		return costToThisNode.get(targetNode);
	}

	/** returns a vector of node indexes that comprise the shortest path from
	 * the source to the target */
	@Override
	public List<Integer> getPathToTarget() {
		List<Integer> path = new LinkedList<Integer>();

		if (targetNode < 0) {
			return path;
		}

		int node = targetNode;

		path.add(node);

		while(node != source && shortestPathTree.get(node) != null) {
			node = shortestPathTree.get(node).from();
			
			path.add(0, node);
		}
		
		return path;
	}

	/** returns the path as a list of PathEdges */
	@Override
	public List<PathEdge> getPathAsPathEdges() {
		List<PathEdge> path = new LinkedList<PathEdge>();
		
		if (targetNode < 0) {
			return path;
		}
		
		int node = targetNode;
		
		while (node != source && shortestPathTree.get(node) != null) {
			path.add(0, new PathEdge(
					graph.getNode(shortestPathTree.get(node).from()).pos(),
					graph.getNode(shortestPathTree.get(node).to()).pos(),
					shortestPathTree.get(node).flags(),
					shortestPathTree.get(node).IDOfIntersectingEntity()));
			
			node = shortestPathTree.get(node).from();
		}
		
		return path;
	}

}
