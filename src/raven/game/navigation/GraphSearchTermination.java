package raven.game.navigation;

import raven.game.RavenObject;
import raven.game.triggers.Trigger;
import raven.math.graph.SparseGraph;

public interface GraphSearchTermination<T extends SparseGraph<NavGraphNode<Trigger<?>>, NavGraphEdge>> {
	public boolean isSatisfied(T graph, int target, int currentNodeIndex);

	boolean isSatisfied(T graph, RavenObject target, int currentNodeIndex);
}
