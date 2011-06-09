package raven.game.navigation;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.Iterator;

import raven.game.RavenBot;
import raven.game.interfaces.IRavenBot;
import raven.game.RavenGame;
import raven.game.RavenMap;
import raven.game.RavenObject;
import raven.math.CellSpacePartition;
import raven.math.Vector2D;
import raven.math.graph.GraphEdge;
import raven.math.graph.GraphNode;
import raven.math.graph.GraphSearchDijkstra;
import raven.math.graph.GraphSearchStatus;
import raven.math.graph.GraphSearchType;
import raven.math.graph.SparseGraph;
import raven.game.*;
import raven.game.triggers.*;
import raven.game.navigation.PathEdge;
import raven.game.messaging.Dispatcher;
import raven.game.messaging.RavenMessage;
import raven.game.messaging.Telegram;
import raven.game.navigation.RavenPathPlanner;
import raven.goals.GoalThink;
import raven.math.C2DMatrix;
import raven.math.Transformations;
import raven.script.RavenScript;
import raven.ui.GameCanvas;
import raven.utils.Log;
import raven.utils.Regulator;

public class RavenPathPlanner {

	// Onwer of this instance
	private IRavenBot owner;

	// The navgraph
	private SparseGraph<NavGraphNode<Trigger<IRavenBot>>, NavGraphEdge> navGraph;

	private GraphSearchTimeSliced<NavGraphEdge> currentSearch;
	private GraphSearchType searchType;

	// The destination position
	private Vector2D destinationPos;

	public RavenPathPlanner() {
		getReadyForNewSearch();
	}

	public RavenPathPlanner(RavenBot owner) {
		this.owner = owner;
		navGraph = owner.getWorld().getMap().getNavGraph();
		currentSearch = null;
	}

	/** returns the index of the closest visible graph node to the given position */
	private int getClosestNodeToPosition(Vector2D pos)
	{
		double closestSoFar = Double.MAX_VALUE;
		int closestNode = GraphNode.INVALID_NODE_INDEX;

		//when the cell space is queried this the the range searched for neighboring
		//graph nodes. This value is inversely proportional to the density of a 
		//navigation graph (less dense = bigger values)
		double range = owner.getWorld().getMap().getCellSpaceNeighborhoodRange();

		//calculate the graph nodes that are neighboring this position
		owner.getWorld().getMap().getCellSpace().calculateNeighbors(pos, range);

		//iterate through the neighbors and sum up all the position vectors
		CellSpacePartition<NavGraphNode<Trigger<IRavenBot>>> nodes= owner.getWorld().getMap().getCellSpace();
		for (NavGraphNode<Trigger<IRavenBot>> node : nodes)
		{
			//if the path between this node and pos is unobstructed calculate the
			//distance
			if (owner.canWalkBetween(pos, node.pos()))
			{
				double dist = pos.distanceSq(node.pos());

				//keep a record of the closest so far
				if (dist < closestSoFar)
				{
					closestSoFar = dist;
					closestNode  = node.index();
				}
			}
		}

		return closestNode;
	}

	/** smooths a path by removing extraneous edges. (may not remove all
	 * extraneous edges) */
	private void smoothPathEdgesQuick(List<PathEdge> Path)
	{
		// TODO
		//create a couple of iterators and point them at the front of the path
		List<PathEdge> e1=Path;
		List<PathEdge> e2=Path;
		//increment e2 so it points to the edge following e1.

		//while e2 is not the last edge in the path, step through the edges checking
		//to see if the agent can move without obstruction from the source node of
		//e1 to the destination node of e2. If the agent can move between those 
		//positions then the two edges are replaced with a single edge.
		for(int i=0;i<e2.size();i++){
			if ( e2.get(i+1).Behavior()== 0 &&    owner.canWalkBetween(e1.get(i).Source(), e2.get(i+1).Destination()))
			{
				e1.get(i).SetDestination(e2.get(i+1).Destination());
				e2.remove(i+1);
				//Path.remove(e2.get(i+1));
			}
			else
			{
				e1 = e2;
			}
		}
		Path=e2;
	}


	/** smooths a path by removing extraneous edges. (removes *all* extraneous
	 * edges) */
	private void smoothPathEdgesPrecise(List<PathEdge> Path)
	{
		// TODO

		//create a couple of iterators
		List<PathEdge> e1=Path;
		List<PathEdge> e2=Path;


		for(int i=0;i<e1.size();i++) {
			e2=e1;
			//while e2 is not the last edge in the path, step through the edges
			//checking to see if the agent can move without obstruction from the 
			//source node of e1 to the destination node of e2. If the agent can move
			//between those positions then the any edges between e1 and e2 are
			//replaced with a single edge.
			for(int j=1; j<e2.size();j++){

				//check for obstruction, adjust and remove the edges accordingly
				if ( e2.get(j).Behavior()== 0 &&    owner.canWalkBetween(e1.get(i).Source(), e2.get(j).Destination()))
				{
					e1.get(i).SetDestination(e2.get(j).Destination());
					e2.remove(j);
					e1=e2;
					--i;	}
				else
				{
					++j;
				}
			}
			Path=e2;
		}
	}

	private void getReadyForNewSearch()
	{
		//unregister any existing search with the path manager
		owner.getWorld().getPathManager().UnRegister(this);

		//clean up memory used by any existing search
		currentSearch = null;
	}

	/** Given an item type, this method determines the closest reachable graph
	 * node to the bot's position and then creates a instance of the time-
	 * sliced Dijkstra's algorithm, which it registers with the search manager */
	public boolean requestPathToItem(RavenObject type)
	{    
		//clear the waypoint list and delete any active search
		getReadyForNewSearch();

		//find the closest visible node to the bots position
		int ClosestNodeToBot = getClosestNodeToPosition(owner.pos());

		//remove the destination node from the list and return false if no visible
		//node found. This will occur if the navgraph is badly designed or if the bot
		//has managed to get itself *inside* the geometry (surrounded by walls),
		//or an obstacle
		if (ClosestNodeToBot == 0) {
			Log.debug("PathPlanner", "No closest node to bot found!");
			return false; 
		}

		currentSearch = new GraphSearchDijkstraTS<SparseGraph<NavGraphNode<Trigger<IRavenBot>>,NavGraphEdge>>(navGraph, ClosestNodeToBot, type);
		searchType = GraphSearchType.Dijkstra;
		
		//register the search with the path manager
		owner.getWorld().getPathManager().Register(this);
		
		return true;
	}
	
	/** Given a target, this method first determines if nodes can be reached from 
	 * the  bot's current position and the target position. If either end point
	 * is unreachable the method returns false.
	 *  
	 *  If nodes are reachable from both positions then an instance of the time-
	 *  sliced A* search is created and registered with the search manager. the
	 *  method then returns true. */
	public boolean RequestPathToPosition(Vector2D targetPos) {
		getReadyForNewSearch();

		//make a note of the target position.
		destinationPos = targetPos;

		//if the target is walkable from the bot's position a path does not need to
		//be calculated, the bot can go straight to the position by ARRIVING at
		//the current waypoint
		if (owner.canWalkTo(targetPos))
		{ 
			return true;
		}

		//find the closest visible node to the bots position
		int ClosestNodeToBot = getClosestNodeToPosition(owner.pos());

		//remove the destination node from the list and return false if no visible
		//node found. This will occur if the navgraph is badly designed or if the bot
		//has managed to get itself *inside* the geometry (surrounded by walls),
		//or an obstacle.
		if (ClosestNodeToBot == GraphNode.INVALID_NODE_INDEX)
		{ 
			Log.trace("PathPlanner", "No closest node to bot found!");
			return false; 
		} else {
			Log.trace("PathPlanner", "Closest node to bot is " + ClosestNodeToBot);
		}

		//find the closest visible node to the target position
		int ClosestNodeToTarget = getClosestNodeToPosition(targetPos);

		//return false if there is a problem locating a visible node from the target.
		//This sort of thing occurs much more frequently than the above. For
		//example, if the user clicks inside an area bounded by walls or inside an
		//object.
		if (ClosestNodeToTarget == GraphNode.INVALID_NODE_INDEX)
		{ 
			Log.trace("PathPlanner", "No closest node to target ( " + targetPos + ") found!");
			return false; 
		} else {
			Log.trace("PathPlanner", "Closest node to target is " + ClosestNodeToTarget);
		}
		
		//create an instance of a the distributed A* search class
		currentSearch = new GraphSearchAStarTS<SparseGraph<NavGraphNode<Trigger<IRavenBot>>,NavGraphEdge>>(navGraph, ClosestNodeToBot, ClosestNodeToTarget);
		searchType = GraphSearchType.AStar;
		
		//and register the search with the path manager
		owner.getWorld().getPathManager().Register(this);
		return true;
	}
	

	/** called by an agent after it has been notified that a search has
	 * terminated successfully. The method extracts the path from
	 * currentSearch, adds additional edges appropriate to the search type and
	 * returns it as a list of PathEdges. */
	public List<PathEdge> getPath() 
	{
		if (currentSearch == null)
			throw new RuntimeException("RavenPathPlanner#getPath called without a search!");

		List<PathEdge> path = currentSearch.getPathAsPathEdges();
		int closest = getClosestNodeToPosition(owner.pos());

		path.add(0, new PathEdge(owner.pos(), getNodePosition(closest),NavGraphEdge.NORMAL, 0));


		//if the bot requested a path to a location then an edge leading to the
		//destination must be added
		if (searchType == GraphSearchType.AStar)
		{   
			path.add(new PathEdge(path.get(path.size()-1).Destination(), destinationPos, NavGraphEdge.NORMAL, 0));
		}

		//smooth paths if required
		if (RavenUserOptions.smoothPathsQuick)
		{
			smoothPathEdgesQuick(path);
		}

		if (RavenUserOptions.smoothPathsPrecise)
		{
			smoothPathEdgesPrecise(path);
		}

		return path;
	}

	/** returns the cost to travel from the bot's current position to a
	 * specific graph node. This method makes use of the pre-calculated lookup
	 * table created by RavenGame */
	public double getCostToNode(int nodeIdx)
	{
		//find the closest visible node to the bots position
		int node = getClosestNodeToPosition(owner.pos());

		//add the cost to this node
		double cost = owner.pos().distance(navGraph.getNode(node).pos());

		//add the cost to the target node and return
		return cost + owner.getWorld().getMap().calculateCostToTravelBetweenNodes(node, nodeIdx); 
	}

	/** returns the cost to the closest instance of the giver type. This
	 * method makes use of the pre-calculated lookup table. Returns -1 if no
	 * active trigger found */
	public Double getCostToClosestItem(RavenObject giverType) {
		//find the closest visible node to the bots position
		int node = getClosestNodeToPosition(owner.pos());

		//if no closest node found return failure
		if (node == GraphNode.INVALID_NODE_INDEX)
			return -1.0;

		double ClosestSoFar = Double.MAX_VALUE;

		//iterate through all the triggers to find the closest *active* trigger of 
		//type GiverType
		List<Trigger<IRavenBot>> triggers = owner.getWorld().getMap().getTriggers();

		for (Trigger<IRavenBot> trigger : triggers) {
			if(trigger.entityType() == giverType && trigger.isActive()) {
				double cost = owner.getWorld().getMap().calculateCostToTravelBetweenNodes(node, trigger.graphNodeIndex());

				if (cost < ClosestSoFar) {
					ClosestSoFar = cost;
				}
			}
		}

		//return a negative value if no active trigger of the type found
		if (ClosestSoFar == Double.MAX_VALUE) {
			return -1.0;
		}

		return ClosestSoFar;
	}

	/** the path manager calls this to iterate once though the search cycle of
	 * the currently assigned search algorithm. When a search is terminated
	 * the method messages the owner with either the msg_NoPathAvailable or
	 * msg_PathReady messages */
	public GraphSearchStatus cycleOnce()
	{
		if (currentSearch == null)
			throw new RuntimeException("<Raven_PathPlanner::CycleOnce>: No search object instantiated");

		GraphSearchStatus result = currentSearch.cycleOnce();

		//let the bot know of the failure to find a path
		if (result == GraphSearchStatus.TARGET_NOT_FOUND) {
			Dispatcher.dispatchMsg(Dispatcher.SEND_MSG_IMMEDIATELY,
					Dispatcher.SENDER_ID_IRRELEVANT,
					owner.ID(),
					RavenMessage.MSG_NO_PATH_AVAILABLE,
					Dispatcher.NO_ADDITIONAL_INFO);

		}

		//let the bot know a path has been found
		else if (result == GraphSearchStatus.TARGET_FOUND)
		{
			//if the search was for an item type then the final node in the path will
			//represent a giver trigger. Consequently, it's worth passing the pointer
			//to the trigger in the extra info field of the message. (The pointer
			//will just be NULL if no trigger)


			Trigger<IRavenBot> trigger = navGraph.getNode(currentSearch.getPathToTarget().get(currentSearch.getPathToTarget().size() - 1)).extraInfo();

			Dispatcher.dispatchMsg(Dispatcher.SEND_MSG_IMMEDIATELY,
					Dispatcher.SENDER_ID_IRRELEVANT,
					owner.ID(),
					RavenMessage.MSG_PATH_READY,
					trigger);
		}

		return result;
	}

	/** used to retrieve the position of a graph node from its index. (takes
	 * into account the enumerations 'non_graph_source_node' and
	 * 'non_graph_target_node' */
	public Vector2D getNodePosition(int idx)
	{
		return navGraph.getNode(idx).pos();
	}
}

