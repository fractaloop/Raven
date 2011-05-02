package raven.goals;

import raven.game.RavenBot;
import raven.math.Vector2D;
import raven.ui.GameCanvas;

public class AttackTargetGoal_Evaluator extends Goal_Evaluator {


	public AttackTargetGoal_Evaluator(Double bias) {
		super(bias);
	}

	public double calculateDesirability(RavenBot pBot){
		double Desirability = 0.0;

		//only do the calculation if there is a target present
		if (pBot.getTargetSys().isTargetPresent()) 
		{
			double Tweaker = 1.0;

			Desirability = Tweaker * RavenFeature.Health(pBot) * RavenFeature.TotalWeaponStrength(pBot);

			//bias the value according to the personality of the bot
			Desirability *= getBias();
		}

		return Desirability;

	}

	public void setGoal(RavenBot pEnt){
		pEnt.getBrain().addGoal_attackTarget(); 
	}

	public void renderInfo(Vector2D Position, RavenBot pBot){
		GameCanvas.textAtPos(Position, "AT: " + String.valueOf(calculateDesirability(pBot)));

		String s = String.valueOf(RavenFeature.Health(pBot)) + ", " + String.valueOf(RavenFeature.TotalWeaponStrength(pBot));

		GameCanvas.textAtPos(Position, s);
	}
}

