package raven.math.graph;

/**
 * The euclidian heuristic (straight-line distance)

 * @param <T> the graph type
 */
public class EuclideanHeuristic<T extends SparseGraph<?,?>> implements Heuristic<T> {

	@Override
	public double Calculate(T graph, int node1, int node2) {
		return graph.getNode(node1).pos().distance(graph.getNode(node2).pos());
	}

}
