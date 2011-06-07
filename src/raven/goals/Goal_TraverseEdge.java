package raven.goals;

import raven.game.RavenBot;
import raven.game.navigation.NavGraphEdge;
import raven.game.navigation.PathEdge;
import raven.script.RavenScript;
import raven.ui.GameCanvas;

public class Goal_TraverseEdge extends GoalComposite<RavenBot> {
	//the edge the bot will follow
	PathEdge  m_Edge;

	//true if m_Edge is the last in the path.
	boolean      m_bLastEdgeInPath;

	//the estimated time the bot should take to traverse the edge
	double     m_dTimeExpected;

	//this records the time this goal was activated
	double     elapsedTime;

	//returns true if the bot gets stuck
	boolean isStuck(){
		if (elapsedTime > m_dTimeExpected){
			System.out.println("BOT "  + m_pOwner.ID() + " IS STUCK!!");
			return true;
		}

		return false;

	}



	public Goal_TraverseEdge(RavenBot ravenBot, PathEdge edge, boolean lastedgeinpath) {

		// Goal<Raven_Bot>(pBot, goal_traverse_edge),
		super(ravenBot, Goal.GoalType.goal_traverse_edge);
		m_Edge = edge;
		m_dTimeExpected = 0.0;
		m_bLastEdgeInPath = lastedgeinpath;
	}



	@Override
	public void activate() {
		m_iStatus = Goal.CurrentStatus.active;

		//the edge behavior flag may specify a type of movement that necessitates a 
		//change in the bot's max possible speed as it follows this edge
		switch(m_Edge.Behavior()) {
			case NavGraphEdge.SWIM:
				m_pOwner.setMaxSpeed(RavenScript.getDouble("Bot_MaxSwimmingSpeed"));
				break;
			case NavGraphEdge.CRAWL:
				m_pOwner.setMaxSpeed(RavenScript.getDouble("Bot_MaxCrawlingSpeed"));
				break;
		}


		//record the time the bot starts this goal
		elapsedTime = 0;   

		//calculate the expected time required to reach the this waypoint. This value
		//is used to determine if the bot becomes stuck 
		m_dTimeExpected = m_pOwner.calculateTimeToReachPosition(m_Edge.Destination());

		//factor in a margin of error for any reactive behavior
		double MarginOfError = 2.0;

		m_dTimeExpected += MarginOfError;


		//set the steering target
		m_pOwner.getSteering().setTarget(m_Edge.Destination());

		//Set the appropriate steering behavior. If this is the last edge in the path
		//the bot should arrive at the position it points to, else it should seek
		if (m_bLastEdgeInPath) {
			m_pOwner.getSteering().arriveOn();
		} else {
			m_pOwner.getSteering().seekOn();
		}

	}

	@Override
	public raven.goals.Goal.CurrentStatus process(double delta){
		//if status is inactive, call Activate()
		activateIfInactive();

		//if the bot has become stuck return failure
		elapsedTime += delta;
		if (isStuck())
		{
			m_iStatus = Goal.CurrentStatus.failed;
		}

		//if the bot has reached the end of the edge return completed
		else { 
			if (m_pOwner.isAtPosition(m_Edge.Destination())) {
				m_iStatus = Goal.CurrentStatus.completed;
			}
		}
		return m_iStatus;
	}
	
	@Override
	public void terminate(){
		//turn off steering behaviors.
		m_pOwner.getSteering().seekOff();
		m_pOwner.getSteering().arriveOff();

		//return max speed back to normal
		m_pOwner.setMaxSpeed(RavenScript.getDouble("Bot_MaxSpeed"));
		
		// set goal status to completed.
		m_iStatus = Goal.CurrentStatus.completed;

	}


	@Override
	public void render(){
		if (m_iStatus == Goal.CurrentStatus.active)
		{
			GameCanvas.bluePen();
			GameCanvas.line(m_pOwner.pos(), m_Edge.Destination());
			GameCanvas.greenBrush();
			GameCanvas.blackPen();
			GameCanvas.circle(m_Edge.Destination(), 3);
		}
	}




}
