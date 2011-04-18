package raven.goals;

import raven.game.RavenBot;
import raven.math.Vector2D;
import raven.math.graph.GraphEdge;

public class Goal_NegotiateDoor extends GoalComposite<RavenBot> {
GraphEdge mine = new GraphEdge();
boolean   m_bLastEdgeInPath;

	public Goal_NegotiateDoor(RavenBot ravenBot, GraphEdge edge, boolean flag) {
		super(ravenBot, Goal.goalType.goal_negotiate_door);
		this.mine = edge;
		this.m_bLastEdgeInPath = flag;
	}

	@Override
	public void activate() {
		  m_iStatus = Goal.curStatus.active;
		  
		  //if this goal is reactivated then there may be some existing subgoals that
		  //must be removed
		  removeAllSubgoals();
		  
		  //get the position of the closest navigable switch
		  Vector2D posSw = getM_pOwner().getWorld().getPosOfClosestSwitch(getM_pOwner().pos(),
		                                                          mine.doorID());

		  //because goals are *pushed* onto the front of the subgoal list they must
		  //be added in reverse order.
		  
		  //first the goal to traverse the edge that passes through the door
		  AddSubgoal(new Goal_TraverseEdge(getM_pOwner(), mine, m_bLastEdgeInPath));

		  //next, the goal that will move the bot to the beginning of the edge that
		  //passes through the door
		  AddSubgoal(new Goal_MoveToPosition(getM_pOwner(), mine.source()));
		  
		  //finally, the Goal that will direct the bot to the location of the switch
		  AddSubgoal(new Goal_MoveToPosition(getM_pOwner(), posSw));
		
	}

	
	public raven.goals.Goal.curStatus process() {
		  //if status is inactive, call Activate()
		  activateIfInactive();

		  //process the subgoals
		  m_iStatus = ProcessSubgoals();

		  return m_iStatus;
	}

}
