package raven.game.navigation;

import java.util.List;

import raven.math.graph.GraphSearchStatus;
import raven.math.graph.GraphSearchType;

public abstract class GraphSearchTimeSliced<T>  {

	private GraphSearchType searchType;
	
	public GraphSearchTimeSliced(GraphSearchType type) { searchType = type; }
	
	/** When called, this method runs the algorithm through one search cycle.
	 * The method returns an enumerated value (target_found, target_not_found,
	 * search_incomplete) indicating the status of the search */
	public abstract GraphSearchStatus cycleOnce();
	
	/** returns the vector of edges that the algorithm has examined */
	public abstract List<T> getSPT();
	
	/** returns the total cost to the target */
	public abstract double getCostToTarget();
	
	/** returns a list of node indexes that comprise the shortest path from
	 * the source to the target */
	public abstract List<Integer> getPathToTarget();

	/** returns the path as a list of PathEdges */
	public abstract List<PathEdge> getPathAsPathEdges();

	public GraphSearchType getType(){ return searchType; }
}
