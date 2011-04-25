package raven.game.navigation;

import java.util.LinkedList;
import java.util.List;

import raven.game.messaging.Dispatcher;
import raven.game.messaging.RavenMessage;


public class PathManager {
	private List<RavenPathPlanner<?>> searchRequests;
	private int numSearchCyclesPerUpdate;
	private RavenPathPlanner<?> currentSearch;
	
	public PathManager(int numCyclesPerUpdate) {
		this.numSearchCyclesPerUpdate = numCyclesPerUpdate;
		searchRequests = new LinkedList<RavenPathPlanner<?>>();
	}

	public void Register(RavenPathPlanner<?> pathPlanner){
		//make sure the bot does not already have a current search in the queue
		  if(searchRequests.contains(pathPlanner)) return;
		  else searchRequests.add(pathPlanner);
		  
		  if(searchRequests.size() == 1) {
			  currentSearch = searchRequests.get(1);
		  }
	}

	public void UnRegister(RavenPathPlanner<?> pathPlanner){
		searchRequests.remove(pathPlanner);
		if(pathPlanner == currentSearch) currentSearch = null;
	}

	  //returns the amount of path requests currently active.
	 public int  GetNumActiveSearches(){return searchRequests.size();}

	///////////////////////////////////////////////////////////////////////////////
	//------------------------- UpdateSearches ------------------------------------
	//
	//  This method iterates through all the active path planning requests 
	//  updating their searches until the user specified total number of search
	//  cycles has been satisfied.
	//
	//  If a path is found or the search is unsuccessful the relevant agent is
	//  notified accordingly by Telegram
	//-----------------------------------------------------------------------------
	public void updateSearches()
	{
	  int NumCyclesRemaining = numSearchCyclesPerUpdate;

	  //iterate through the search requests until either all requests have been
	  //fulfilled or there are no search cycles remaining for this update-step.

	  for(int i=0; NumCyclesRemaining-->0 && !searchRequests.isEmpty(); i++)
	  {	
		  RavenPathPlanner<?> curSearch;
		  curSearch = searchRequests.get(i);
		  int result = curSearch.cycleOnce();
		  if(result == 1 ||result == 0){
			  searchRequests.remove(i);}
	  

	  }//end while
	}
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
                     currentSearch.getOwnerID(),
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
                    currentSearch.getOwnerID(),
                    RavenMessage.MSG_PATH_READY,
                    null); //TODO interpret the message above to get this trigger.
	  }

	  return result;
	}



}
