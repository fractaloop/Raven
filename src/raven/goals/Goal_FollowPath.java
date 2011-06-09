package raven.goals;

import java.util.ArrayList;
import java.util.List;

import raven.game.RavenBot;
import raven.game.navigation.NavGraphEdge;
import raven.game.navigation.PathEdge;
import raven.ui.GameCanvas;

public class Goal_FollowPath extends GoalComposite<RavenBot> {
	
	private List<PathEdge>  m_Path = new ArrayList<PathEdge>();

	public Goal_FollowPath(RavenBot m_pOwner, List<PathEdge> list) {
		super(m_pOwner, Goal.GoalType.goal_follow_path);
		this.m_Path = list;
		
		for(PathEdge edge : m_Path) {
			// Is this edge the last?
			boolean isLastEdgeInPath = m_Path.indexOf(edge) == m_Path.size()-1;
			
			switch(edge.Behavior()){
				case NavGraphEdge.NORMAL:
					AddSubgoal(new Goal_TraverseEdge(m_pOwner, edge, isLastEdgeInPath));
					break;
				case NavGraphEdge.GOES_THROUGH_DOOR:
					AddSubgoal(new Goal_NegotiateDoor(m_pOwner, edge, isLastEdgeInPath));
					break;
				case NavGraphEdge.JUMP:
					break;
				case NavGraphEdge.GRAPPLE:
					break;
				default:
					break;
			}
		}
	}

	@Override
	public void activate() { 
		// Clean subgoals of finished goals.
		List<Goal> toRemove = new ArrayList<Goal>();
		for(Goal goal : m_SubGoals) {
			if(goal.m_iStatus == CurrentStatus.completed){
				toRemove.add(goal);
			}
		}
		m_SubGoals.removeAll(toRemove);
	}

	@Override
	public CurrentStatus process(double delta) {
		//if status is inactive, call Activate()
		activateIfInactive();
		m_iStatus = ProcessSubgoals(delta);

		//if there are no subgoals present check to see if the path still has edges.
		//remaining. If it does then call activate to grab the next edge.
		if (m_iStatus == Goal.CurrentStatus.completed && !m_Path.isEmpty()) {
			activate(); 
		}
		return m_iStatus;
	}

	public void render() {

		// forward the request to the subgoals
		// in this case we imitate goalComposite . render()
		for(Goal goal : m_SubGoals) {
			goal.render();
		}
	}

	@Override
	public void terminate() {
		m_iStatus = Goal.CurrentStatus.completed;
		
	}
}
