package raven.math.graph;

public interface Heuristic<T extends SparseGraph<?, ?>> {
	public double Calculate(T graph, int node1, int node2);
}
