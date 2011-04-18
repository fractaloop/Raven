package raven.goals;

import raven.game.RavenBot;

public abstract class Goal_Evaluator {

	private Double m_iBias;
	
	
	public Goal_Evaluator(Double bias) {
		this.m_iBias = bias;
	}


	public abstract double calculateDesirability(RavenBot m_pOwner);


	public abstract void setGoal(RavenBot m_pOwner);


	public Double getM_iBias() {
		return m_iBias;
	}


}
