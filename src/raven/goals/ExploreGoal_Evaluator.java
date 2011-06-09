package raven.goals;

import raven.game.RavenBot;
import raven.goals.Goal.GoalType;
import raven.math.Vector2D;
import raven.ui.GameCanvas;

public class ExploreGoal_Evaluator extends Goal_Evaluator {
	public ExploreGoal_Evaluator(Double inp) {
		super(inp, GoalType.goal_explore);
	}




	//---------------- CalculateDesirability -------------------------------------
	//-----------------------------------------------------------------------------
	public double calculateDesirability(RavenBot pBot)
	{
		double Desirability = 0.05;

		Desirability *= getBias();

		return Desirability;
	}

	//----------------------------- SetGoal ---------------------------------------
	//-----------------------------------------------------------------------------
	public void setGoal(RavenBot pBot)
	{
		pBot.getBrain().addGoal_explore();
	}

	//-------------------------- RenderInfo ---------------------------------------
	//-----------------------------------------------------------------------------
	public void renderInfo(Vector2D Position, RavenBot pBot)
	{
		GameCanvas.textAtPos(Position, "EX: " + "Desire: "+ calculateDesirability(pBot));
	}

}
