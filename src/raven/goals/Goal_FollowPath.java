package raven.goals;

import java.util.ArrayList;
import java.util.Iterator;

import raven.game.RavenBot;
import raven.game.navigation.NavGraphEdge;
import raven.math.Vector2D;
import raven.math.graph.GraphEdge;
import raven.ui.GameCanvas;

public class Goal_FollowPath extends GoalComposite<RavenBot> {

	private ArrayList<NavGraphEdge>  m_Path = new ArrayList<NavGraphEdge>();




	public Goal_FollowPath(RavenBot m_pOwner, ArrayList<NavGraphEdge> list) {
        super(m_pOwner, Goal.goalType.goal_follow_path);
		this.m_Path = list;
	}


	public void activate() {
		m_iStatus = Goal.curStatus.active;

		//get a reference to the next edge
		NavGraphEdge edge = m_Path.get(0);

		//remove the edge from the path
		m_Path.remove(0); 

		//some edges specify that the bot should use a specific behavior when
		//following them. This switch statement queries the edge behavior flag and
		//adds the appropriate goals/s to the subgoal list.
		switch(edge.flags()){
		case NavGraphEdge.NORMAL:
		{
			AddSubgoal(new Goal_TraverseEdge(getM_pOwner(), edge, m_Path.isEmpty()));
		}

		break;

		case NavGraphEdge.GOES_THROUGH_DOOR:
		{

			//also add a goal that is able to handle opening the door
			AddSubgoal(new Goal_NegotiateDoor(getM_pOwner(), edge, m_Path.isEmpty()));
		}

		break;

		case NavGraphEdge.JUMP:
		{
			//add subgoal to jump along the edge
			// not defined in c++ code...
		}

		break;

		case NavGraphEdge.GRAPPLE:
		{
			//add subgoal to grapple along the edge
			// not defined in c++ code
		}

		break;

		default:

			try {
				throw new Exception("<Goal_FollowPath::Activate>: Unrecognized edge type");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}


	}
	public raven.goals.Goal.curStatus process() {
		 //if status is inactive, call Activate()
		  activateIfInactive();

		  m_iStatus = ProcessSubgoals();

		  //if there are no subgoals present check to see if the path still has edges.
		  //remaining. If it does then call activate to grab the next edge.
		  if (m_iStatus == Goal.curStatus.completed && !m_Path.isEmpty())
		  {
		    activate(); 
		  }

		  return m_iStatus;
	}
	public void render() {
		

//        render all the path waypoints remaining on the path list
		  Iterator<NavGraphEdge> it = m_Path.iterator();
          NavGraphEdge temp = new NavGraphEdge();
		  while(it.hasNext())
		  {  
			  temp = it.next();
		    GameCanvas.blackPen();
		    GameCanvas.lineWithArrow(temp.from()), temp.to(), 5);
		    
		    GameCanvas.redBrush();
		    GameCanvas.blackPen();
		    GameCanvas.circle(temp.to(), 3);
		  }

		  //forward the request to the subgoals
		  // in this case we imitate goalComposite . render()
		  if (!m_SubGoals.isEmpty())
		  {
		    m_SubGoals.get(0).render();
		  }		
		
	}




}
