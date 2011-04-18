package raven.goals;

import raven.game.RavenBot;
import raven.game.RavenObject;
import raven.goals.RavenFeature;
import raven.math.Vector2D;
import raven.ui.GameCanvas;

public class GetHealthGoal_Evaluator extends Goal_Evaluator {
	GetHealthGoal_Evaluator(Double inp){
		super(inp);
	}
	
	
	

	//---------------------- CalculateDesirability -------------------------------------
	//-----------------------------------------------------------------------------
	public double calculateDesirability(RavenBot pBot)
	{
	  //first grab the distance to the closest instance of a health item
	  double Distance = RavenFeature.DistanceToItem(pBot, RavenObject.HEALTH);

	  //if the distance feature is rated with a value of 1 it means that the
	  //item is either not present on the map or too far away to be worth 
	  //considering, therefore the desirability is zero
	  if (Distance == 1)
	  {
	    return 0;
	  }
	  else
	  {
	    //value used to tweak the desirability
	    Double Tweaker = 0.2;
	  
	    //the desirability of finding a health item is proportional to the amount
	    //of health remaining and inversely proportional to the distance from the
	    //nearest instance of a health item.
	    double Desirability = Tweaker * (1-RavenFeature.Health(pBot)) / 
	                        (RavenFeature.DistanceToItem(pBot, RavenObject.HEALTH));
	 
	    //ensure the value is in the range 0 to 1
	    RavenFeature.Clamp(Desirability, 0, 1);
	  
	    //bias the value according to the personality of the bot
	    Desirability *= getM_iBias();

	    return Desirability;
	  }
	}



	//----------------------------- SetGoal ---------------------------------------
	//-----------------------------------------------------------------------------
	public void setGoal(RavenBot pBot)
	{
		
		try{
	  pBot.getBrain().addGoal_getItem(RavenObject.HEALTH);
	  
		}catch(Exception ex){
			System.out.println( ex.getMessage());
		}
	}

	//-------------------------- RenderInfo ---------------------------------------
	//-----------------------------------------------------------------------------
	public void RenderInfo(Vector2D Position, RavenBot pBot)
	{
	  GameCanvas.textAtPos(Position, "H: " + String.valueOf( (calculateDesirability(pBot))));
	  String s = String.valueOf( (1-RavenFeature.Health(pBot)) + ", " + String.valueOf(RavenFeature.DistanceToItem(pBot,RavenObject.HEALTH)));
	  
	  Position.add(new Vector2D(0,15));
	  GameCanvas.textAtPos(Position, s);
	  return;
	  

	}
	
	
	
	

}
