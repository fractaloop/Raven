package raven.goals;

import java.util.Random;
import java.util.Vector;

import raven.game.RavenBot;
import raven.game.messaging.Telegram;
import raven.math.Vector2D;

public class GoalThink extends GoalComposite<RavenBot> {
Vector<Goal_Evaluator> m_Evaluators = new Vector<Goal_Evaluator>();
double HealthBias = 0;
double ShotgunBias = 0;
double RocketLauncherBias = 0;
double RailgunBias = 0;
double ExploreBias = 0;
double AttackBias  = 0;


	
	
	
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
		  m_Evaluators.add(new GetWeaponGoal_Evaluator(ShotgunBias,
				  GetWeaponGoal_Evaluator.Wep.SHOTGUN));
		  m_Evaluators.add(new GetWeaponGoal_Evaluator(RailgunBias,
		                                                     GetWeaponGoal_Evaluator.Wep.RAILGUN));
		  m_Evaluators.add(new GetWeaponGoal_Evaluator(RocketLauncherBias,
				  GetWeaponGoal_Evaluator.Wep.ROCKETLAUNCHER));
		
		
		
		
	}

	public void Terminate(){
		
	}
	
	public void Activate(){
		
	}
	
    void AddGoal_MoveToPostion(Vector2D pos){
    	
    }
 
    


	
	
	public void Process() {
		// TODO Auto-generated method stub
	}

	public void Arbitrate() {
		// TODO Auto-generated method stub
	}

	public boolean handleMessage(Telegram msg) {
		// TODO Auto-generated method stub
		return false;
	}



	public void removeAllSubgoals() {
		// TODO Auto-generated method stub
	}

	public void render() {
		// TODO Auto-generated method stub
	}
	
	public void RenderEvaluations(Integer left, Integer top){
		
	}

	public void renderAtPos(Vector2D p) {
		// TODO Auto-generated method stub
	}

	public void queueGoal_moveToPosition(Vector2D p) {
		// TODO Auto-generated method stub
	}

	public void addGoal_moveToPosition(Vector2D p) {
		// TODO Auto-generated method stub
	}
	
	
	public void addGoal_attackTarget(){
		
	}
	
	public void addGoal_explore() {
		// TODO Auto-generated method stub
	}
    public void addGoal_getItem(int ItemType){
	    	
	}
	
	
	
}
