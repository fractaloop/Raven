package raven.goals;

import raven.game.RavenBot;
import raven.math.Vector2D;
import raven.utils.Log;

public class Goal_AttackTarget extends GoalComposite<RavenBot> {


	public Goal_AttackTarget(RavenBot m_pOwner) {
		super(m_pOwner, Goal.GoalType.goal_attack_target);
	}

	@Override
	public void activate(){
		m_iStatus = Goal.CurrentStatus.active;

		//if this goal is reactivated then there may be some existing subgoals that
		//must be removed
		removeAllSubgoals();

		//it is possible for a bot's target to die whilst this goal is active so we
		//must test to make sure the bot always has an active target
		if (!m_pOwner.getTargetSys().isTargetPresent())
		{
			m_iStatus = Goal.CurrentStatus.completed;
			Log.debug("attack", "target no longer present");
			return;
		}

		//if the bot is able to shoot the target (there is LOS between bot and
		//target), then select a tactic to follow while shooting
		if (m_pOwner.getTargetSys().isTargetShootable())
		{
			//if the bot has space to strafe then do so
			if (m_pOwner.canStepLeft() != null || m_pOwner.canStepRight() != null)
			{
				AddSubgoal(new Goal_DodgeSideToSide(m_pOwner));
			}

			//if not able to strafe, head directly at the target's position 
			else
			{
				AddSubgoal(new Goal_SeekToPosition(m_pOwner, m_pOwner.getTargetBot().pos()));
			}
		}

		//if the target is not visible, go hunt it.
		else
		{
			AddSubgoal(new Goal_HuntTarget(m_pOwner));
		}
	}
	
	@Override
	public raven.goals.Goal.CurrentStatus process(double delta){
		//if status is inactive, call Activate()
		activateIfInactive();

		//process the subgoals
		m_iStatus = ProcessSubgoals(delta);

		reactivateIfFailed();

		return m_iStatus;
	}

	@Override
	public void terminate(){
		m_iStatus = Goal.CurrentStatus.completed;
	}




}
