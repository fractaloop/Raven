package raven.goals;

import raven.game.RavenBot;

public abstract class Goal_Evaluator {

	private Double bias;


	public Goal_Evaluator(Double bias) {
		this.bias = bias;
	}


	public abstract double calculateDesirability(RavenBot m_pOwner);


	public abstract void setGoal(RavenBot m_pOwner);


	public Double getBias() {
		return bias;
	}


}
