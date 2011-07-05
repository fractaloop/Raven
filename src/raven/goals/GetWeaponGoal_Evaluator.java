package raven.goals;

import raven.game.RavenBot;
import raven.game.RavenObject;
import raven.goals.Goal.GoalType;
import raven.math.Vector2D;
import raven.ui.GameCanvas;

public class GetWeaponGoal_Evaluator extends Goal_Evaluator {
	private RavenObject weaponType;

	public GetWeaponGoal_Evaluator(Double bias, RavenObject weaponType) {
		super(bias, GoalType.unknown_type);
		switch(weaponType) {
			case ROCKET_LAUNCHER:
				setGoalType(GoalType.goal_get_rocket_launcher);
				break;
			case RAIL_GUN:
				setGoalType(GoalType.goal_get_railgun);
				break;
			case SHOTGUN:
				setGoalType(GoalType.goal_get_shotgun);
				break;
		}
		this.weaponType = weaponType;
	}

	public double calculateDesirability(RavenBot pBot)
	{
		//grab the distance to the closest instance of the weapon type
		double Distance = RavenFeature.DistanceToItem(pBot, weaponType);

		//if the distance feature is rated with a value of 1 it means that the
		//item is either not present on the map or too far away to be worth 
		//considering, therefore the desirability is zero
		if (Distance < 0) {
			return 0;
		} else {
			//value used to tweak the desirability
			// all the tweakers are 1.0 in the params.js file
			double Tweaker = 1.0;

			double Health, WeaponStrength;

			Health = RavenFeature.Health(pBot);
			WeaponStrength = RavenFeature.IndividualWeaponStrength(pBot, weaponType);

			// we want this to be high for weapons that are not the blaster.
			double Desirability = ((Tweaker * Health * WeaponStrength) * 2) / Distance;

			//ensure the value is in the range 0 to 1
			RavenFeature.Clamp(Desirability, 0, 1);

			Desirability *= getBias();
			
			Desirability = 0;

			return Desirability;
		}
	}



	//------------------------------ SetGoal --------------------------------------
	public void setGoal(RavenBot pBot)
	{
		try {
			pBot.getBrain().addGoal_getItem(weaponType);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}

	//-------------------------- RenderInfo ---------------------------------------
	//-----------------------------------------------------------------------------
	public void RenderInfo(Vector2D Position, RavenBot pBot)
	{
		String s = new String();
		switch(weaponType)
		{
		case RAIL_GUN:
			s="RG: ";break;
		case ROCKET_LAUNCHER:
			s="RL: "; break;
		case SHOTGUN:
			s="SG: "; break;
		case BLASTER:
			s="BS: "; break;
		}

		GameCanvas.textAtPos(Position, s + String.valueOf( (calculateDesirability(pBot))));
	}






}
