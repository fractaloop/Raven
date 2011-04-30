package raven.game.navigation;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import raven.math.graph.GraphSearchStatus;

public class PathManager {
	private List<RavenPathPlanner> searchRequests;
	private int numSearchCyclesPerUpdate;

	public PathManager(int numCyclesPerUpdate) {
		this.numSearchCyclesPerUpdate = numCyclesPerUpdate;
		searchRequests = new LinkedList<RavenPathPlanner>();
	}

	public void Register(RavenPathPlanner pathPlanner){
		//make sure the bot does not already have a current search in the queue
		if (!searchRequests.contains(pathPlanner))
			searchRequests.add(pathPlanner);
	}

	public void UnRegister(RavenPathPlanner pathPlanner){
		searchRequests.remove(pathPlanner);
	}

	//returns the amount of path requests currently active.
	public int  GetNumActiveSearches(){ return searchRequests.size(); }

	/** This method iterates through all the active path planning requests
	 * updating their searches until the user specified total number of search
	 * cycles has been satisfied.
	 * 
	 * If a path is found or the search is unsuccessful the relevant agent is
	 * notified accordingly by Telegram */
	public void updateSearches()
	{
		int NumCyclesRemaining = numSearchCyclesPerUpdate;

		//iterate through the search requests until either all requests have been
		//fulfilled or there are no search cycles remaining for this update-step.
		while (NumCyclesRemaining-- > 0) {
			if (searchRequests.isEmpty())
				break;

			// make one search cycle of this path request
			List<RavenPathPlanner> toRemove = new ArrayList<RavenPathPlanner>();
			for (RavenPathPlanner planner : searchRequests) {
				GraphSearchStatus result = planner.cycleOnce();

				// if the search has terminated remove from the list
				if (result == GraphSearchStatus.TARGET_FOUND || result == GraphSearchStatus.TARGET_NOT_FOUND) {
					toRemove.add(planner);
				}

				if (NumCyclesRemaining-- <= 0)
					break;
			}
			searchRequests.removeAll(toRemove);

		}
	}
}
