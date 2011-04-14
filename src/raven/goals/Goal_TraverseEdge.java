package raven.goals;

import raven.game.RavenBot;
import raven.math.Vector2D;
import raven.math.graph.GraphEdge;
import raven.ui.GameCanvas;

public class Goal_TraverseEdge extends GoalComposite<RavenBot> {
	 //the edge the bot will follow
	  GraphEdge  m_Edge;

	  //true if m_Edge is the last in the path.
	  boolean      m_bLastEdgeInPath;

	  //the estimated time the bot should take to traverse the edge
	  double     m_dTimeExpected;
	  
	  //this records the time this goal was activated
	  double     m_dStartTime;

	  //returns true if the bot gets stuck
	  boolean isStuck(){
		  double TimeTaken = Clock.GetCurrentTime() - m_dStartTime;
		  if (TimeTaken > m_dTimeExpected){
		   System.out.println("BOT "  + getM_pOwner().ID() + " IS STUCK!!");


		    return true;
		  }

		  return false;
		  
	  }
	  
	  
	  
	public Goal_TraverseEdge(RavenBot ravenBot, GraphEdge edge, boolean lastedgeinpath) {

       // Goal<Raven_Bot>(pBot, goal_traverse_edge),
		setM_pOwner(ravenBot);
        m_Edge = edge;
        m_dTimeExpected = 0.0;
        m_bLastEdgeInPath = lastedgeinpath;
	}



	@Override
	public void activate() {
		m_iStatus = Goal.curStatus.active;
		  
		  //the edge behavior flag may specify a type of movement that necessitates a 
		  //change in the bot's max possible speed as it follows this edge
		  switch(m_Edge.behavior())
		  {
		    case swim:
		    {
		      getM_pOwner().SetMaxSpeed(script.GetDouble("Bot_MaxSwimmingSpeed"));
		    }
		   
		    break;
		   
		    case crawl:
		    {
		       getM_pOwner().SetMaxSpeed(script.GetDouble("Bot_MaxCrawlingSpeed"));
		    }
		   
		    break;
		  }
		  

		  //record the time the bot starts this goal
		  m_dStartTime = Clock.GetCurrentTime();   
		  
		  //calculate the expected time required to reach the this waypoint. This value
		  //is used to determine if the bot becomes stuck 
		  m_dTimeExpected = getM_pOwner().calculateTimeToReachPosition(m_Edge.destination());
		  
		  //factor in a margin of error for any reactive behavior
		  double MarginOfError = 2.0;

		  m_dTimeExpected += MarginOfError;


		  //set the steering target
		  getM_pOwner().getSteering().SetTarget(m_Edge.destination());

		  //Set the appropriate steering behavior. If this is the last edge in the path
		  //the bot should arrive at the position it points to, else it should seek
		  if (m_bLastEdgeInPath)
		  {
		     getM_pOwner().getSteering().ArriveOn();
		  }

		  else
		  {
		    getM_pOwner().getSteering().SeekOn();
		  }
		
	}
	
	public raven.goals.Goal.curStatus process(){
		  //if status is inactive, call Activate()
		  activateIfInactive();
		  
		  //if the bot has become stuck return failure
		  if (isStuck())
		  {
		    m_iStatus = Goal.curStatus.failed;
		  }
		  
		  //if the bot has reached the end of the edge return completed
		  else
		  { 
		    if (getM_pOwner().isAtPosition(m_Edge.destination()))
		    {
		      m_iStatus = Goal.curStatus.completed;
		    }
		  }

		  return m_iStatus;
	}
	
	
	public void terminate(){
		  //turn off steering behaviors.
		  getM_pOwner().getSteering().SeekOff();
		  getM_pOwner().getSteering().ArriveOff();

		  //return max speed back to normal
		  getM_pOwner().SetMaxSpeed(script.GetDouble("Bot_MaxSpeed"));
		
	}

	
	public void render(){
		  if (m_iStatus == Goal.curStatus.active)
		  {
		    GameCanvas.bluePen();
		    GameCanvas.line(getM_pOwner().pos(), m_Edge.destination());
		    GameCanvas.greenBrush();
		    GameCanvas.blackPen();
		    GameCanvas.circle(m_Edge.destination(), 3);
		  }
	}
	
	
	

}
