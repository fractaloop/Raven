package raven.goals;

import raven.game.RavenBot;
import raven.game.messaging.Telegram;
import raven.math.Vector2D;
import raven.ui.GameCanvas;

public class Goal_MoveToPosition extends GoalComposite<RavenBot>{
	//the position the bot wants to reach
	Vector2D m_vDestination;
	//TODO
	public Goal_MoveToPosition(RavenBot owner, Vector2D pos){
		super(owner, Goal.goalType.goal_move);
		m_vDestination = pos;
	}



	public void activate() {
		m_iStatus = Goal.curStatus.active;

		//make sure the subgoal list is clear.
		removeAllSubgoals();

		//requests a path to the target position from the path planner. Because, for
		//demonstration purposes, the Raven path planner uses time-slicing when 
		//processing the path requests the bot may have to wait a few update cycles
		//before a path is calculated. Consequently, for appearances sake, it just
		//seeks directly to the target position whilst it's awaiting notification
		//that the path planning request has succeeded/failed
		if (getM_pOwner().getPathPlanner().RequestPathToPosition(m_vDestination))
		{
			AddSubgoal(new Goal_SeekToPosition(getM_pOwner(), m_vDestination));
		}



	}
	public raven.goals.Goal.curStatus  process() {
		//if status is inactive, call Activate()
		activateIfInactive();

		//process the subgoals
		m_iStatus = ProcessSubgoals();

		//if any of the subgoals have failed then this goal re-plans
		reactivateIfFailed();

		return m_iStatus;
	}




	public void terminate(){}

	//this goal is able to accept messages
	public boolean HandleMessage(Telegram msg) {
		//first, pass the message down the goal hierarchy
		boolean bHandled = ForwardMessageToFrontMostSubgoal(msg);

		//if the msg was not handled, test to see if this goal can handle it
		if (bHandled == false)
		{
			switch(msg.msg)
			{
			case MSG_PATH_READY:

				//clear any existing goals
				removeAllSubgoals();

				AddSubgoal(new Goal_FollowPath(getM_pOwner(),
						getM_pOwner().getPathPlanner().getPath()));

				return true; //msg handled


			case MSG_NO_PATH_AVAILABLE:

				m_iStatus = Goal.curStatus.failed;

				return true; //msg handled

			default: return false;
			}
		}

		//handled by subgoals
		return true;
	}



	@Override
	public void render() {
		//forward the request to the subgoal
		// fake it.
		if (!m_SubGoals.isEmpty())
		{
			m_SubGoals.get(0).render();
		}

		//draw a bullseye
		GameCanvas.blackPen();
		GameCanvas.blueBrush();
		GameCanvas.circle(m_vDestination, 6);
		GameCanvas.redBrush();
		GameCanvas.redPen();
		GameCanvas.circle(m_vDestination, 4);
		GameCanvas.yellowBrush();
		GameCanvas.yellowPen();
		GameCanvas.circle(m_vDestination, 2);

	}
}
