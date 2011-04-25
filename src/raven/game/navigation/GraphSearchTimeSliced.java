package raven.game.navigation;

import java.util.List;
import java.util.Vector;

import raven.math.graph.GraphEdge;
import raven.math.graph.GraphEdgeFactory;
import raven.math.graph.GraphNode;
import raven.math.graph.SparseGraph;
import raven.utils.StreamUtils;
import raven.math.graph.GraphSearchDijkstra;

public class GraphSearchTimeSliced<NodeType extends NavGraphNode<T>,EdgeType extends NavGraphEdge, T>  {

	public enum SearchType{AStar, Dijkstra};
	  
	private Vector<Double> costToThisNode; 

	private  Vector<EdgeType>  shortestPathTree;
	 private Vector<EdgeType>  searchFrontier;

	 private int source;
	 private int target;
	 private SparseGraph<NodeType, EdgeType> graph=new SparseGraph<NodeType, EdgeType>();
	public List<PathEdge> path;
	private SearchType searchType;
	public GraphSearchTimeSliced(){
		
	}
	public SearchType GetType(){
		return searchType;
	}
	//-------------------------- GetPathAsPathEdges -------------------------------
	//
	//  returns the path as a list of PathEdges
	//-----------------------------------------------------------------------------

	public List<PathEdge> getPathAsPathEdges(){
		//List<PathEdge> path= new List<PathEdge>();

		  //just return an empty path if no target or no path found
		  if (target < 0)  return path;    

		  int nd = target;
		    
		  while ((nd != source) && (shortestPathTree.get(nd) != null))
		  {
		    path.add(new PathEdge(graph.getNode(shortestPathTree.get(nd).from()).pos(),
		    		graph.getNode(shortestPathTree.get(nd).to()).pos(),
		                             shortestPathTree.get(nd).flags(),shortestPathTree.get(nd).IDOfIntersectingEntity()));

		    nd = shortestPathTree.get(nd).from();
		  }

		  return path;
		}
	
}