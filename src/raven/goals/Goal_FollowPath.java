package raven.goals;

import java.util.ArrayList;

import raven.game.RavenBot;
import raven.game.navigation.NavGraphEdge;
import raven.game.navigation.PathEdge;
import raven.math.Vector2D;
import raven.math.graph.GraphEdge;

public class Goal_FollowPath extends GoalComposite<RavenBot> {

	private ArrayList<PathEdge>  m_Path = new ArrayList<PathEdge>();




	public Goal_FollowPath(RavenBot m_pOwner, ArrayList<PathEdge> list) {
		super(m_pOwner, Goal.goalType.goal_follow_path);
		this.m_Path = list;
	}


	public void activate() {
		m_iStatus = Goal.curStatus.active;

		//get a reference to the next edge
		PathEdge edge = m_Path.get(0);

		//remove the edge from the path
		m_Path.remove(0); 

		//some edges specify that the bot should use a specific behavior when
		//following them. This switch statement queries the edge behavior flag and
		//adds the appropriate goals/s to the subgoal list.
		switch(edge.Behavior()){
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
		Iterator<PathEdge> it = m_Path.iterator();
		PathEdge temp;
		while(it.hasNext())
		{  
			temp = it.next();
			GameCanvas.blackPen();
			GameCanvas.lineWithArrow(temp.Source(), temp.Destination(), 5);

			GameCanvas.redBrush();
			GameCanvas.blackPen();
			GameCanvas.circle(temp.Destination(), 3);
		}

		//forward the request to the subgoals
		// in this case we imitate goalComposite . render()
		if (!m_SubGoals.isEmpty())
		{
			m_SubGoals.get(0).render();
		}		

	}




}
