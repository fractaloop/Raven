package raven.game.navigation;

import java.util.List;
import java.util.Vector;

import java.util.Iterator;

import com.sun.org.apache.bcel.internal.generic.NEW;

import raven.game.RavenBot;
import raven.game.RavenGame;
import raven.game.RavenMap;
import raven.game.RavenObject;
import raven.math.CellSpacePartition;
import raven.math.Vector2D;
import raven.math.graph.GraphEdge;
import raven.math.graph.GraphNode;
import raven.math.graph.GraphSearchDijkstra;
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
import raven.math.Vector2D;
import raven.script.RavenScript;
import raven.ui.GameCanvas;
import raven.utils.Regulator;

public class RavenPathPlanner<T> {

	  //indexed into my node. Contains the accumulative cost to that node
	  
	private Vector<Double> costToThisNode; 

	private  Vector<NavGraphEdge>  shortestPathTree;
	 private Vector<NavGraphEdge>  searchFrontier;

	 private int source;
	 private int target;
	
	public List<PathEdge> path;
	private RavenBot ravenBot;
	private GraphSearchDijkstra currentSearch;
	private SparseGraph<NavGraphNode<T>, NavGraphEdge> graph= new SparseGraph <NavGraphNode<T>, NavGraphEdge>();
	//this is the position the bot wishes to plan a path to reach
	private  Vector2D destinationPos=new Vector2D();

	public RavenPathPlanner(RavenBot ravenBot) {
		this.ravenBot = ravenBot;
	}
	public void GetReadyForNewSearch()
	{
		  //unregister any existing search with the path manager
		  ((RavenGame) ravenBot.getWorld()).getPathManager().UnRegister(this);
		  //clean up memory used by any existing search
		  currentSearch = null;
	}
	
	public double getCostToNode(int nodeIdx)
	{
	  //find the closest visible node to the bots position
	  int d = getClosestNodeToPosition(ravenBot.pos());
	
	  //add the cost to this node
	  double cost =ravenBot.pos().distance(graph.getNode(d).pos());
	
	  //add the cost to the target node and return
	  return cost + ravenBot.getWorld().getMap().calculateCostToTravelBetweenNodes(d, nodeIdx); 
	}

	//------------------------------ RequestPathToItem -----------------------------
	//
	// Given an item type, this method determines the closest reachable graph node
	// to the bot's position and then creates a instance of the time-sliced 
	// Dijkstra's algorithm, which it registers with the search manager
	//
	//-----------------------------------------------------------------------------
	public boolean requestPathToItem(int ItemType)
	{    
	  //clear the waypoint list and delete any active search
	  GetReadyForNewSearch();

	  //find the closest visible node to the bots position
	  int ClosestNodeToBot = getClosestNodeToPosition(ravenBot.pos());

	  //remove the destination node from the list and return false if no visible
	  //node found. This will occur if the navgraph is badly designed or if the bot
	  //has managed to get itself *inside* the geometry (surrounded by walls),
	  //or an obstacle
	  if (ClosestNodeToBot == 0)
	  { 	    return false; 
	  }

	  //create an instance of the search algorithm
	  TriggerSystem<Trigger<RavenBot> > t_con; 
	  GraphSearchDijkstra DijSearch;
	  
	  currentSearch = new GraphSearchDijkstra(graph,
	                                   ClosestNodeToBot,
	                                   ItemType);  

	  //register the search with the path manager
	  ravenBot.getWorld().getPathManager().Register(this);
	  return true;
	}



		//----------------------------- GetPath ------------------------------------
		//
		//  called by an agent after it has been notified that a search has terminated
		//  successfully. The method extracts the path from m_pCurrentSearch, adds
		//  additional edges appropriate to the search type and returns it as a list of
		//  PathEdges.
		//-----------------------------------------------------------------------------
	public List<PathEdge> getPath() 
	{	
//currentSearch.getPathAsPathEdges()
		  int closest = getClosestNodeToPosition(ravenBot.pos());

	  path.add(new PathEdge (ravenBot.pos(), this.getNodePosition(closest),0,0));

		  
		  //if the bot requested a path to a location then an edge leading to the
		  //destination must be added
		  if (currentSearch instanceof GraphSearchDijkstra)
		  {   
		    path.add(new PathEdge(path.get(path.size()).Destination(), destinationPos,0,0 ));
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

	//--------------------------- SmoothPathEdgesQuick ----------------------------
	//
	//  smooths a path by removing extraneous edges.
	//-----------------------------------------------------------------------------
	public void smoothPathEdgesQuick(List<PathEdge> Path)
	{
	  //create a couple of iterators and point them at the front of the path
		List<PathEdge> e1=Path;
		List<PathEdge> e2=Path;
	  //increment e2 so it points to the edge following e1.
	
	  //while e2 is not the last edge in the path, step through the edges checking
	  //to see if the agent can move without obstruction from the source node of
	  //e1 to the destination node of e2. If the agent can move between those 
	  //positions then the two edges are replaced with a single edge.
	  for(int i=0;i<e2.size();i++){
		  if ( e2.get(i+1).Behavior()== 0 &&    ravenBot.canWalkBetween(e1.get(i).Source(), e2.get(i+1).Destination()))
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


	//----------------------- SmoothPathEdgesPrecise ---------------------------------
	//
	//  smooths a path by removing extraneous edges.
	//-----------------------------------------------------------------------------
	public void smoothPathEdgesPrecise(List<PathEdge> Path)
	{
	  //create a couple of iterators
		List<PathEdge> e1=Path;
		List<PathEdge> e2=Path;

		
		  for(int i=0;i<e1.size();i++){
			  e2=e1;
			//while e2 is not the last edge in the path, step through the edges
			    //checking to see if the agent can move without obstruction from the 
			    //source node of e1 to the destination node of e2. If the agent can move
			    //between those positions then the any edges between e1 and e2 are
			    //replaced with a single edge.
			 for(int j=1; j<e2.size();j++){
			 
				 //check for obstruction, adjust and remove the edges accordingly
			  if ( e2.get(j).Behavior()== 0 &&    ravenBot.canWalkBetween(e1.get(i).Source(), e2.get(j).Destination()))
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
		}	}

	//------------------------------ GetNodePosition ------------------------------
	//
	//  used to retrieve the position of a graph node from its index. (takes
	//  into account the enumerations 'non_graph_source_node' and 
	//  'non_graph_target_node'
	//----------------------------------------------------------------------------- 
	public Vector2D getNodePosition(int idx)
	{
	  return graph.getNode(idx).pos();
	}

	//------------------------ GetCostToClosestItem ---------------------------
	//
	//  returns the cost to the closest instance of the giver type. This method
	//  makes use of the pre-calculated lookup table. Returns -1 if no active
	//  trigger found
	//-----------------------------------------------------------------------------
	public Double getCostToClosestItem(int giverType)
	{
	  //find the closest visible node to the bots position
	  int nd = getClosestNodeToPosition(ravenBot.pos());

	  //if no closest node found return failure
	  if (nd == -1) return -1.0;

	  double ClosestSoFar = Double.MAX_VALUE;

	  //iterate through all the triggers to find the closest *active* trigger of 
	  //type GiverType
	  List<Trigger<RavenBot>> triggers=ravenBot.getWorld().getMap().getTriggers();
	 
	  for (Trigger<RavenBot> trigger: triggers)
	  {
		  if(trigger.entityType() != null && trigger.entityType().hashCode() == giverType && trigger.isActive())
	    {
	      double cost =ravenBot.getWorld().getMap().calculateCostToTravelBetweenNodes(nd, trigger.graphNodeIndex());

	      if (cost < ClosestSoFar)
	      {
	        ClosestSoFar = cost;
	      }
	    }
	  }

	  //return a negative value if no active trigger of the type found
	  if (ClosestSoFar== Double.MAX_VALUE)
	  {
	    return -1.0;
	  }

	  return ClosestSoFar;
	}

	//--------------------------- RequestPathToPosition ------------------------------
	//
	//  Given a target, this method first determines if nodes can be reached from 
	//  the  bot's current position and the target position. If either end point
	//  is unreachable the method returns false. 
	//
	//  If nodes are reachable from both positions then an instance of the time-
	//  sliced A* search is created and registered with the search manager. the
	//  method then returns true.
//	        
	//-----------------------------------------------------------------------------

	public boolean RequestPathToPosition(Vector2D targetPos) {
		 GetReadyForNewSearch();

		  //make a note of the target position.
		  destinationPos = targetPos;

		  //if the target is walkable from the bot's position a path does not need to
		  //be calculated, the bot can go straight to the position by ARRIVING at
		  //the current waypoint
		  if (ravenBot.canWalkTo(targetPos))
		  { 
		    return true;
		  }
		  
		  //find the closest visible node to the bots position
		  int ClosestNodeToBot = getClosestNodeToPosition(ravenBot.pos());

		  //remove the destination node from the list and return false if no visible
		  //node found. This will occur if the navgraph is badly designed or if the bot
		  //has managed to get itself *inside* the geometry (surrounded by walls),
		  //or an obstacle.
		  if (ClosestNodeToBot == 0)
		  { 
		    return false; 
		  }

		  //find the closest visible node to the target position
		  int ClosestNodeToTarget = getClosestNodeToPosition(targetPos);
		  
		  //return false if there is a problem locating a visible node from the target.
		  //This sort of thing occurs much more frequently than the above. For
		  //example, if the user clicks inside an area bounded by walls or inside an
		  //object.
		  if (ClosestNodeToTarget == 0)
		  { 
		    return false; 
		  }		  //create an instance of a the distributed A* search class
		  GraphSearchDijkstra di = new GraphSearchDijkstra(graph, ClosestNodeToBot, ClosestNodeToTarget);
		  
		 //TODO 		   
		  currentSearch = new GraphSearchDijkstra(graph,
		                               ClosestNodeToBot,
		                               ClosestNodeToTarget);

		  //and register the search with the path manager
		  ravenBot.getWorld().getPathManager().Register(this);
		  return true;
		}

		
	
	//------------------------ GetClosestNodeToPosition ---------------------------
	//
	//  returns the index of the closest visible graph node to the given position
	//-----------------------------------------------------------------------------
	public int getClosestNodeToPosition(Vector2D pos)
	{
	  Double closestSoFar = Double.MAX_VALUE;
	  int   closestNode  = 0;

	  //when the cell space is queried this the the range searched for neighboring
	  //graph nodes. This value is inversely proportional to the density of a 
	  //navigation graph (less dense = bigger values)
	  double range = ravenBot.getWorld().getMap().getCellSpaceNeighborhoodRange();

	  //calculate the graph nodes that are neighboring this position
	  ravenBot.getWorld().getMap().getCellSpace().calculateNeighbors(pos, range);

	  //iterate through the neighbors and sum up all the position vectors
	  CellSpacePartition<NavGraphNode<Trigger<RavenBot>>> pN= ravenBot.getWorld().getMap().getCellSpace();
	  for (NavGraphNode<Trigger<RavenBot>> P : pN)
	  {
	    //if the path between this node and pos is unobstructed calculate the
	    //distance
	    if (ravenBot.canWalkBetween(pos, P.pos()))
	    {
	      double dist = pos.distanceSq(P.pos());

	      //keep a record of the closest so far
	      if (dist < closestSoFar)
	      {
	        closestSoFar = dist;
	        closestNode  = P.index();
	      }
	    }
	  }
	   
	  return closestNode;
	}

	//---------------------------- CycleOnce --------------------------------------
	//
	//  the path manager calls this to iterate once though the search cycle
	//  of the currently assigned search algorithm.
	//-----------------------------------------------------------------------------
	public int cycleOnce()
	{
	  //assert (m_pCurrentSearch && "<Raven_PathPlanner::CycleOnce>: No search object instantiated");

	  int result = this.cycleOnce();

	  //let the bot know of the failure to find a path
	  if (result == 0)
	  {
	     Dispatcher.dispatchMsg(Dispatcher.SEND_MSG_IMMEDIATELY,
	                             Dispatcher.SENDER_ID_IRRELEVANT,
	                             ravenBot.ID(),
	                             RavenMessage.MSG_NO_PATH_AVAILABLE,
	                             Dispatcher.NO_ADDITIONAL_INFO);

	  }

	  //let the bot know a path has been found
	  else if (result == 1)
	  {
	    //if the search was for an item type then the final node in the path will
	    //represent a giver trigger. Consequently, it's worth passing the pointer
	    //to the trigger in the extra info field of the message. (The pointer
	    //will just be NULL if no trigger)
	    
		  
	//TODO	  //Trigger<T> pTrigger = this.currentSearch.GetType().(currentSearch.GetType()..D->GetPathToTarget().back()).ExtraInfo();

	    Dispatcher.dispatchMsg(Dispatcher.SEND_MSG_IMMEDIATELY,
	                            Dispatcher.SENDER_ID_IRRELEVANT,
	                            ravenBot.ID(),
	                            RavenMessage.MSG_PATH_READY,
	                            null); //TODO interpret the message above to get this trigger.
	  }

	  return result;
	}
	
	public int getOwnerID() {
		return ravenBot.ID();
	}
}
	
