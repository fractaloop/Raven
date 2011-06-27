package raven.goals;

import raven.game.RavenBot;

public class Goal_Pursuit extends GoalComposite<RavenBot> {

	public Goal_Pursuit(RavenBot m_pOwner, RavenBot target) {
		super(m_pOwner, Goal.GoalType.goal_pursuit);
		
	}

	@Override
	public void activate() {
		m_iStatus = Goal.CurrentStatus.active;
		m_pOwner.getSteering().pursuitOn();

	}

	@Override
	public raven.goals.Goal.CurrentStatus process(double delta) {
		activateIfInactive();
		return m_iStatus;
	}


	@Override
	public void terminate() {
		m_pOwner.getSteering().pursuitOff();
		m_iStatus = Goal.CurrentStatus.completed;
	}

	@Override
	public void render() {
		// do nothing
		m_SubGoals.get(0).render();
	}

}
