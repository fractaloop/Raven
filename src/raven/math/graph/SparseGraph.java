package raven.math.graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import raven.ui.GameCanvas;
import raven.utils.Log;
import raven.utils.Pair;

public class SparseGraph<NodeType extends GraphNode, EdgeType extends GraphEdge> {
	/** the nodes that comprise this graph */
	private List<NodeType> nodes;
	
	/** a list of adjacency edge lists. (each node index keys into the list of
	 * edges associated with that node) */
	private List<List<EdgeType>> edges;
	
	/** is this a directed graph? */
	private boolean isDigraph;
	
	/** the index of the next node to be added */
	private int nextNodeIndex;

	/** returns true if an edge is not already present in the graph. Used when
	 * adding edges to make sure no duplicates are created. */
	private boolean uniqueEdge(int from, int to) {
		for (EdgeType edge : edges.get(from)) {
			if (edge.to() == to) {
				return false;
			}
		}
		
		return true;
	}
	
	/** iterates through all the edges in the graph and removes any that point
	 * to an invalidated node */
	@SuppressWarnings("unused")
	private void cullInvalidEdges() {
		for (List<EdgeType> edgeList : edges) {
			Set<EdgeType> toRemove = new HashSet<EdgeType>();
			for (EdgeType edge : edgeList) {
				if (nodes.get(edge.to()).index() == GraphNode.INVALID_NODE_INDEX ||
						nodes.get(edge.from()).index() == GraphNode.INVALID_NODE_INDEX) {
					toRemove.add(edge);
				}
			}
			edgeList.removeAll(toRemove);
		}
	}
	
	public SparseGraph(boolean digraph) {
		edges = new ArrayList<List<EdgeType>>();
		nodes = new ArrayList<NodeType>();
		nextNodeIndex = 0;
		isDigraph = digraph;
	}
	
	// must fill in details for this....
	public SparseGraph(){
		nodes = new ArrayList<NodeType>();
		edges = new ArrayList<List<EdgeType>>();
		isDigraph = false;
	}
	
	/** returns the node at the given index */
	public NodeType getNode(int idx) {
		if ((idx < 0 || idx > nodes.size()) || nodes.size() == 0) {
			Log.warn("graph", "Could not find node " + idx);
			return null;
		}
		return nodes.get(idx);
	}

	/** method to get all the neighbors of a node */
	public List<EdgeType> getEdges(int index) {
		return edges.get(index);
	}

	/** const method for obtaining a reference to an edge */
	public EdgeType getEdge(int from, int to) {
		if (from < 0 || from > nodes.size() || from == GraphNode.INVALID_NODE_INDEX)
			throw new IndexOutOfBoundsException("SparseNode#getNode: from index " + from + " is invalid");
		if (to < 0 || to > nodes.size() || to == GraphNode.INVALID_NODE_INDEX)
			throw new IndexOutOfBoundsException("SparseNode#getNode: to index " + to + " is invalid");
		
		for (EdgeType edge : edges.get(from)) {
			if (edge.to() == to) {
				return edge;
			}
		}
		
		throw new IndexOutOfBoundsException("SparseNode#getNode: edge does not exist");
	}
	
	/** retrieves the next free node index */
	public int getNextFreeNodeIndex() { return nextNodeIndex; }
	
	/** adds a node to the graph and returns its index
	 * 
	 * Given a node this method first checks to see if the node has been added
	 * previously but is now inactive. If it is, it is reactivated.
	 * 
	 * If the node has not been added previously, it is checked to make sure
	 * its index matches the next node index before being added to the graph
	 */
	public int addNode(NodeType node) {
		if (node.index() < nodes.size()) {
			// make sure the client is not trying to add a node with the same
			// ID as a currently active node
			if (nodes.get(node.index()).index() == GraphNode.INVALID_NODE_INDEX) {
				throw new IndexOutOfBoundsException("SparseGraph#addNode: Attempting to add a node with a duplicate ID");
			}
				
			nodes.set(node.index(), node);
			
			return nextNodeIndex;
		} else {
			// make sure the new node has been indexed correctly
			if (node.index() != nextNodeIndex) {
				throw new IndexOutOfBoundsException("SparseGraph#addNode: invalid index");
			}
			
			nodes.add(node);
			edges.add(new LinkedList<EdgeType>());
			
			return nextNodeIndex++;
		}
	}
	
	/** removes a node by setting its index to INVALID_NODE_INDEX */
	public void removeNode(int node) {
		if (node < 0 || node >= nodes.size()) {
			throw new IndexOutOfBoundsException("SparseGraph#removeNode: invalid node index");
		}
		
		// set this node's index to INVALID_NODE_INDEX
		nodes.get(node).setIndex(GraphNode.INVALID_NODE_INDEX);
		
		// if the graph is not directed remove all edges leading to this node
		// and then clear the edges leading from the node
		if (!isDigraph) {
			// visit each neighbor and erase any edges leading to this node
			for (EdgeType edgeToNeighbor : edges.get(node)) {
				Set<EdgeType> toRemove = new HashSet<EdgeType>();
				for (EdgeType neighborEdge : edges.get(edgeToNeighbor.to())) {
					if (neighborEdge.to() == node) {
						toRemove.add(neighborEdge);
					}
				}
				edges.get(edgeToNeighbor.to()).removeAll(toRemove);
			}
		}
	}
	
	/* Use this to add an edge to the graph. The method will ensure that the
	 * edge passed as a parameter is valid before adding it to the graph. If
	 * the graph is a digraph then a similar edge connecting the nodes in the
	 * opposite direction will be automatically added. */
	@SuppressWarnings("unchecked")
	public void addEdge(EdgeType edge) {
		// first make sure the from and to nodes exist within the graph 
		if (edge.from() >= nextNodeIndex || edge.to() >= nextNodeIndex) {
			throw new IndexOutOfBoundsException("SparseGraph#addEdge: invalid node index");
		}
		
		// make sure both nodes are active before adding the edge
		if (nodes.get(edge.to()).index() != GraphNode.INVALID_NODE_INDEX &&
				nodes.get(edge.from()).index() != GraphNode.INVALID_NODE_INDEX) {
			// add the edge, first making sure it is unique
			if (uniqueEdge(edge.from(), edge.to())) {
				edges.get(edge.from()).add(edge);
			}
			
			// if the graph is undirected we must add another connection in
			// the opposite direction
			if (!isDigraph) {
				// check to make sure the edge is unique before adding
				if (uniqueEdge(edge.to(), edge.from())) {
					// TODO this probably breaks
					EdgeType newEdge = (EdgeType)edge.clone();
					newEdge.setTo(edge.from());
					newEdge.setFrom(edge.to());
					edges.get(edge.to()).add(newEdge);
				}
			}
		}
	}
	
	/** removes the edge connecting from and to from the graph (if present).
	 * If a digraph then the edge connecting the nodes in the opposite
	 * direction will also be removed. */
	public void removeEdge(int from, int to) {
		if (!isDigraph) {
			for (int i = 0; i < edges.get(to).size(); i++) {
				EdgeType edge = edges.get(to).get(i);
				if (edge.to() == from) {
					edges.get(to).remove(i);
					break;
				}
			}
		}
		
		for (int i = 0; i < edges.get(from).size(); i++) {
			EdgeType edge = edges.get(from).get(i);
			if (edge.to() == from) {
				edges.get(from).remove(i);
				break;
			}
		}
		
	}
	
	/** sets the cost of an edge */
	public void setEdgeCost(int from, int to, double cost) {
		// make sure the nodes given are valid
		if (from < 0 || to < 0 || from >= edges.size() || to >= edges.size())
			throw new IndexOutOfBoundsException("SparseGraph#setEdgeCost: invalid index");
		
		// visit each neighbor and erase any edges leading to this node
		for (EdgeType edge : edges.get(from)) {
			if (edge.to() == to) {
				edge.setCost(cost);
			}
		}
	}
	
	/** returns the number of active + inactive nodes present in the graph */
	public int numNodes() { return nodes.size(); }
	
	/** returns the number of active nodes present in the graph (this method's
	 * performance can be improved greatly by caching the value) */
	public int numActiveNodes() {
		int count = 0;
		for (NodeType node : nodes) {
			if (node.index() != GraphNode.INVALID_NODE_INDEX) {
				++count;
			}
		}
		return count;
	}

	/** returns the total number of edges present in the graph */
	public int numEdges() {
		int count = 0;
		for (List<EdgeType> edge : edges) {
			count += edge.size();
		}
		return count;
	}
	
	/** returns true if the graph is directed */
	public boolean isDigraph() { return isDigraph; }
	
	/** returns true if the graph contains no nodes */
	public boolean isEmpty() { return nodes.isEmpty(); }

	/** returns true if a node with the given index is present in the graph */
	public boolean isNodePresent(int node) {
		if (node < 0 || node >= nodes.size() || nodes.get(node).index() == GraphNode.INVALID_NODE_INDEX)
			return false;
		else
			return true;
	}
	
	/** returns true if an edge connecting the nodes 'to' and 'from' is
	 * present in the graph */
	public boolean isEdgePresent(int from, int to) {
		if (isNodePresent(from) && isNodePresent(to)) {
			for (EdgeType edge : edges.get(from)) {
				if (edge.to() == to)
					return true;
			}
		}
		
		return false;
	}
	
	public void clear() { nextNodeIndex = 0; nodes.clear(); edges.clear(); }
	
	public void removeEdges() {
		for (List<EdgeType> edge : edges) {
			edge.clear();
		}
	}

	// Utility functions. These used to be global, but they are generic
	// and need an instance anyway
	public double calculateAverageGraphEdgeLength() {
		double totalLength = 0;
		int numEdgesCounted = 0;

		for (NodeType node : nodes) {
			for (EdgeType edge : edges.get(node.index())) {
				// increment edge counter
				++numEdgesCounted;
				// add length of edge to total length
				totalLength += nodes.get(edge.from()).pos().distance(nodes.get(edge.to()).pos());
			}
		}
		
		return totalLength / numEdgesCounted;
	}

	// creates a lookup table of the cost associated from traveling from one
	public Map<Pair<Integer, Integer>, Double> createAllPairsCostsTable() {
		// Map a graph
		Map<Pair<Integer, Integer>, Double> pathCosts = new HashMap<Pair<Integer, Integer>, Double>(numNodes() * numNodes() - numNodes());
		
		for (int source = 0; source < numNodes(); source++) {
			// Do the search
			GraphSearchDijkstra search = new GraphSearchDijkstra(this, source, -1);
			
			// iterate through every node in the graph and grab the cost to
			// travel to that node
			for (int target = 0; target < numNodes(); target++) {
				pathCosts.put(new Pair<Integer, Integer>(source, target), search.getCostToNode(target));
			}
		}
		
		return pathCosts;
	}

	@Override
	public boolean equals(Object o){
		if(this == o) return true;
		if(! (o instanceof SparseGraph<?, ?>)) return false;
	
		//The only things that matter are list of edges and nodes, and isDigraph
		SparseGraph<?, ?> other = (SparseGraph<?, ?>) o;
		return (nodes.equals(other.nodes) && edges.equals(other.edges) && isDigraph == other.isDigraph);
	}

	public void render(boolean showNodeIndices) {
		if(edges == null) return;
		GameCanvas.lightGreyPen();
		for(List<EdgeType> edgesByNode : edges) {
			for(EdgeType edge : edgesByNode) {
				NodeType from = nodes.get(edge.from);
				NodeType to = nodes.get(edge.to);
				GameCanvas.line(from.pos(), to.pos());
			}
		}

		if(nodes == null) return;
		GameCanvas.bluePen();
		for(NodeType node : nodes) {
			GameCanvas.circle(node.pos(), 2);
		}
	}
}
