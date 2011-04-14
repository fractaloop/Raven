package raven.goals;

import raven.game.RavenBot;

public abstract class Goal_Evaluator {

	private Double m_iBias;
	
	
	public abstract double calculateDesirability(RavenBot m_pOwner);


	public abstract void setGoal(RavenBot m_pOwner);


	public void setM_iBias(Double m_iBias) {
		this.m_iBias = m_iBias;
	}


	public Double getM_iBias() {
		return m_iBias;
	}


}
