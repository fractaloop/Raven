package raven.goals;

import raven.game.RavenBot;
import raven.math.Vector2D;
import raven.ui.GameCanvas;

public class Goal_HuntTarget extends GoalComposite<RavenBot> {
	//this value is set to true if the last visible position of the target
	//bot has been searched without success
	boolean  m_bLVPTried;
	public Goal_HuntTarget(RavenBot m_pOwner) {
		super(m_pOwner, Goal.GoalType.goal_hunt_target);
		this.m_bLVPTried = false;
	}

	@Override
	public void activate() {
		m_iStatus = Goal.CurrentStatus.active;

		//if this goal is reactivated then there may be some existing subgoals that
		//must be removed
		removeAllSubgoals();

		//it is possible for the target to die whilst this goal is active so we
		//must test to make sure the bot always has an active target

		if (m_pOwner.getTargetSys().isTargetPresent())
		{
			//grab a local copy of the last recorded position (LRP) of the target
			Vector2D lrp = m_pOwner.getTargetSys().getLastRecordedPosition();

			//if the bot has reached the LRP and it still hasn't found the target
			//it starts to search by using the explore goal to move to random
			//map locations
			if (lrp == null || m_pOwner.isAtPosition(lrp))
			{
				AddSubgoal(new Goal_Explore(m_pOwner));
			}

			//else move to the LRP
			else
			{
				AddSubgoal(new Goal_MoveToPosition(m_pOwner, lrp));
			}
		}

		//if their is no active target then this goal can be removed from the queue
		else {
			m_iStatus = Goal.CurrentStatus.completed;
		}
	}

	@Override
	public CurrentStatus  process(double delta) {
		//if status is inactive, call Activate()
		activateIfInactive();

		m_iStatus = ProcessSubgoals(delta);

		//if target is in view this goal is satisfied
		if (m_pOwner.getTargetSys().isTargetWithinFOV()) {
			m_iStatus = Goal.CurrentStatus.completed;
		}
		return m_iStatus;
	}

	@Override
	public void terminate(){ }

	@Override
	public void render() {
		//#define SHOW_LAST_RECORDED_POSITION
		//render last recorded position as a green circle
		if (m_pOwner.getTargetSys().isTargetPresent()) {
			GameCanvas.greenBrush();
			GameCanvas.redBrush();
			GameCanvas.circle(m_pOwner.getTargetSys().getLastRecordedPosition(), 3);
		}

		if (!m_SubGoals.isEmpty()) {
			m_SubGoals.get(0).render();
		}
	}
}
