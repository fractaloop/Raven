package raven.goals;

import raven.game.RavenBot;

public class Goal_Pursuit extends GoalComposite<RavenBot> {

	private RavenBot target;
	
	public Goal_Pursuit(RavenBot m_pOwner, RavenBot target) {
		super(m_pOwner, Goal.GoalType.goal_pursuit);
		this.target = target;
	}

	@Override
	public void activate() {
		m_iStatus = Goal.CurrentStatus.active;
		m_pOwner.getSteering().pursuitOn();
	/*	m_pOwner.getSteering().seekOff();
		m_pOwner.getSteering().arriveOff();
		m_pOwner.getSteering().separationOff();
		m_pOwner.getSteering().wallAvoidanceOff();
		m_pOwner.getSteering().wanderOff();*/
		m_pOwner.getSteering().setTargetAgent1(target);
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
