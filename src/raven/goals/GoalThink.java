package raven.goals;

import java.util.Iterator;
import java.util.Random;
import java.util.Vector;

import raven.game.RavenBot;
import raven.game.RavenObject;
import raven.game.messaging.Telegram;
import raven.math.Vector2D;

public class GoalThink extends GoalComposite<RavenBot> {
private Vector<Goal_Evaluator> m_Evaluators = new Vector<Goal_Evaluator>();
private double HealthBias = 0.0;
private double ShotgunBias = 0.0;
private double RocketLauncherBias = 0.0;
private double RailgunBias = 0.0;
private double ExploreBias = 0;
private double AttackBias  = 0;


	
	
	
	public GoalThink(RavenBot ravenBot) {
		 Random randomGenerator = new Random();
// random values are between 0.0 and 1.0
		HealthBias = randomGenerator.nextDouble();
		ShotgunBias = randomGenerator.nextDouble();
		RocketLauncherBias = randomGenerator.nextDouble();
		RailgunBias = randomGenerator.nextDouble();
		ExploreBias = randomGenerator.nextDouble();
		AttackBias  = randomGenerator.nextDouble();
		
		m_Evaluators.add(new GetHealthGoal_Evaluator(HealthBias));
		  m_Evaluators.add(new ExploreGoal_Evaluator(ExploreBias));
		  m_Evaluators.add(new AttackTargetGoal_Evaluator(AttackBias));
		  
		  
		  try{
		  m_Evaluators.add(new GetWeaponGoal_Evaluator(ShotgunBias,
				  RavenObject.SHOTGUN));
		  m_Evaluators.add(new GetWeaponGoal_Evaluator(RailgunBias,
		                                                     RavenObject.RAIL_GUN));
		  m_Evaluators.add(new GetWeaponGoal_Evaluator(RocketLauncherBias,
				  RavenObject.ROCKET_LAUNCHER));
		  }catch(Exception ex){System.out.println(ex.getMessage());}
		
		
		
	}

	public void Terminate(){
		
	}
	@Override
	public void Activate(){
		  if (!m_pOwner.isPossessed())
		  {
		    Arbitrate();
		  }

		  m_iStatus = Goal.curStatus.active;
		
	}
    
	public void activateIfInactive()
	{
		if (isInactive())
		{
			Activate();   
		}
	}

	
	
	public raven.goals.Goal.curStatus Process() {
		  activateIfInactive();
		  
		  raven.goals.Goal.curStatus SubgoalStatus = ProcessSubgoals();

		  if (SubgoalStatus == Goal.curStatus.completed || SubgoalStatus == Goal.curStatus.failed)
		  {
		    if (!m_pOwner.isPossessed())
		    {
		      m_iStatus = Goal.curStatus.inactive;
		    }
		  }

		  return m_iStatus;
	}
	

	
	
	

	public void Arbitrate() {
		//----------------------------- Update ----------------------------------------
		// 
		//  this method iterates through each goal option to determine which one has
		//  the highest desirability.
		//-----------------------------------------------------------------------------
		  double best = 0;
		  Goal_Evaluator MostDesirable = null;

		  //iterate through all the evaluators to see which produces the highest score
		  Iterator<Goal_Evaluator> curDes = m_Evaluators.iterator();
		  Goal_Evaluator current = null;
		  while(curDes.hasNext()){
			  current = curDes.next();
		    double desirabilty = current.CalculateDesirability(m_pOwner);

		    if (desirabilty >= best)
		    {
		      best = desirabilty;
		      MostDesirable = current;
		    }
		  }

		//  assert(MostDesirable && "<Goal_Think::Arbitrate>: no evaluator selected");

		  current.setGoal(m_pOwner);
		}


	
	
	
	//---------------------------- notPresent --------------------------------------
	//
	//  returns true if the goal type passed as a parameter is the same as this
	//  goal or any of its subgoals
	//-----------------------------------------------------------------------------
	public boolean notPresent(raven.goals.Goal.goalType goal)
	{
	  if(m_SubGoals.contains(goal)){
		return true;  
	  }
	  return false;
	}
	
	
	

	public boolean handleMessage(Telegram msg) {
		// TODO Auto-generated method stub
		return false;
	}









	public void queueGoal_moveToPosition(Vector2D pos) {
		 m_SubGoals.add(new Goal_MoveToPosition(m_pOwner, pos));
	}
	
	public void addGoal_moveToPosition(Vector2D p, Vector2D pos) {

		  AddSubgoal( new Goal_MoveToPosition(m_pOwner, pos));
	}
	

	
	public void addGoal_explore() {

		  if (notPresent(Goal.goalType.goal_explore))
		  {
		    removeAllSubgoals();
		    AddSubgoal( new Goal_Explore(m_pOwner));
		  }
	}
	
    public void addGoal_getItem(RavenObject inp){
    	  if (notPresent(Goal.goalType.goal_get))
    	  {
    	    removeAllSubgoals();
    	    AddSubgoal( new Goal_GetItem(m_pOwner, inp));
	      }
    }
    
    
	
	public void addGoal_attackTarget(){
		  if (notPresent(Goal.goalType.goal_attack_target))
		  {
		    removeAllSubgoals();
		    AddSubgoal( new Goal_AttackTarget(m_pOwner));
		  }
	}
	public void render(){
		
	}

	public void renderEvaluations(Integer left, Integer top){
		
	}

	public void queueGoal_moveToPosition(Vector2D pos, Vector2D p) {
		m_SubGoals.add(new Goal_MoveToPosition(m_pOwner, p));
	}
	
}
