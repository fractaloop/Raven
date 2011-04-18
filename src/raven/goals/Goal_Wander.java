package raven.goals;

import raven.game.RavenBot;

public class Goal_Wander extends Goal<RavenBot> {

	public Goal_Wander(RavenBot m_pOwner) {
		super(m_pOwner, Goal.goalType.goal_wander);
	}

	@Override
	public void activate() {
		  m_iStatus = Goal.curStatus.active;

		  m_pOwner.getSteering().WanderOn();
		
	}

	public raven.goals.Goal.curStatus process(){
		
		  activateIfInactive();

		  return m_iStatus;
	}
	
	
	public void terminate()
	{
	  m_pOwner.getSteering().WanderOff();
	}

	@Override
	public void render() {
		// do noting
		
	}

}
