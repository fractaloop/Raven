package raven.game.navigation;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import raven.game.RavenBot;
import raven.game.interfaces.IRavenBot;
import raven.game.triggers.Trigger;
import raven.math.graph.EuclideanHeuristic;
import raven.math.graph.GraphSearchStatus;
import raven.math.graph.GraphSearchType;
import raven.math.graph.Heuristic;
import raven.math.graph.SparseGraph;
import raven.utils.IndexedPriorityQueue;

public class GraphSearchAStarTS<T extends SparseGraph<NavGraphNode<Trigger<IRavenBot>>, NavGraphEdge>> extends GraphSearchTimeSliced<NavGraphEdge> {

	private T graph;

	private Heuristic<T> heuristic;

	/** indexed into my node. Contains the 'real' accumulative cost to that node */
	private List<Double> gCosts;

	/** indexed into by node. Contains the cost from adding gCosts[n] to the
	 * heuristic cost from n to the target node. */
	private List<Double> fCosts;

	private List<NavGraphEdge> shortestPathTree;
	private List<NavGraphEdge> searchFrontier;

	private int source;
	private int target;

	private IndexedPriorityQueue<Double> queue;

	public GraphSearchAStarTS(T graph, int source, int target) {
		super(GraphSearchType.AStar);

		this.graph = graph;
		this.heuristic = new EuclideanHeuristic<T>();

		shortestPathTree = new ArrayList<NavGraphEdge>(graph.numNodes());
		searchFrontier = new ArrayList<NavGraphEdge>(graph.numNodes());

		gCosts = new ArrayList<Double>(graph.numNodes());
		fCosts = new ArrayList<Double>(graph.numNodes());
		// Cost arrays need to be filled
		for (int i = 0; i < graph.numNodes(); i++) {
			gCosts.add(0.0);
			fCosts.add(0.0);
			shortestPathTree.add(null);
			searchFrontier.add(null);
		}

		this.source = source;
		this.target = target;

		// TreeMap functions as a priority queue
		queue = new IndexedPriorityQueue<Double>(fCosts, graph.numNodes());

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

		//put the node on the SPT
		shortestPathTree.set(nextClosestNode, searchFrontier.get(nextClosestNode));

		//if the target has been found exit
		if (nextClosestNode == target) {
			return GraphSearchStatus.TARGET_FOUND;
		}

		//now to test all the edges attached to this node
		for (NavGraphEdge edge : graph.getEdges(nextClosestNode)) {
			// calculate the heuristic cost from this node to the target (H)
			double hCost = heuristic.Calculate(graph, target, edge.to());

			// calculate the 'real' cost to this node from the source (G)
			double gCost = gCosts.get(nextClosestNode) + edge.cost();

			// if the node has not been added to the frontier, add it and
			// update the G and F costs
			if (searchFrontier.get(edge.to()) == null) {
				fCosts.set(edge.to(), gCost + hCost);
				gCosts.set(edge.to(), gCost);

				queue.insert(edge.to());

				searchFrontier.set(edge.to(), edge);
			}
			//if this node is already on the frontier but the cost to get here
			//is cheaper than has been found previously, update the node
			//costs and frontier accordingly.
			else if (gCost < gCosts.get(edge.to()) && shortestPathTree.get(edge.to()) == null) {
				fCosts.set(edge.to(), gCost + hCost);
				gCosts.set(edge.to(), gCost);

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
		return gCosts.get(target);
	}

	/** returns a vector of node indexes that comprise the shortest path from
	 * the source to the target */
	@Override
	public List<Integer> getPathToTarget() {
		List<Integer> path = new LinkedList<Integer>();

		if (target < 0) {
			return path;
		}

		int node = target;

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
		
		if (target < 0) {
			return path;
		}
		
		int node = target;
		
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
