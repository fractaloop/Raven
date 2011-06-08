package raven.goals;

import java.util.Vector;

import raven.game.RavenBot;
import raven.game.RavenObject;
import raven.math.Vector2D;
import raven.utils.Log;

public class GoalThink extends GoalComposite<RavenBot> {
	private Vector<Goal_Evaluator> m_Evaluators = new Vector<Goal_Evaluator>();
	private double HealthBias = 0.0;
	private double ShotgunBias = 0.0;
	private double RocketLauncherBias = 0.0;
	private double RailgunBias = 0.0;
	private double ExploreBias = 0;
	private double AttackBias  = 0;

	public GoalThink(RavenBot ravenBot) {
		super(ravenBot, Goal.GoalType.goal_think);
		Log.debug("GoalThink", "created new brain attached to bot " + ravenBot.ID());

		// random values are between 0.5 and 1.5
		HealthBias = Math.random() + 0.5;
		ShotgunBias = Math.random() + 0.5;
		RocketLauncherBias = Math.random() + 0.5;
		RailgunBias = Math.random() + 0.5;
		ExploreBias = Math.random() + 0.5;
		AttackBias  = Math.random() + 0.5;

		m_Evaluators.add(new GetHealthGoal_Evaluator(HealthBias));
		m_Evaluators.add(new ExploreGoal_Evaluator(ExploreBias));
		m_Evaluators.add(new AttackTargetGoal_Evaluator(AttackBias));

		try {
			m_Evaluators.add(new GetWeaponGoal_Evaluator(ShotgunBias,
					RavenObject.SHOTGUN));
			m_Evaluators.add(new GetWeaponGoal_Evaluator(RailgunBias,
					RavenObject.RAIL_GUN));
			m_Evaluators.add(new GetWeaponGoal_Evaluator(RocketLauncherBias,
					RavenObject.ROCKET_LAUNCHER));
		} catch(Exception ex) {
			System.out.println(ex.getMessage());
		}
	}

	public void Terminate(){

	}
	
	@Override
	public void activate(){
		if (!m_pOwner.isPossessed()) {
			Arbitrate();
		}
		m_iStatus = Goal.CurrentStatus.active;
	}

	public void activateIfInactive() {
		if (isInactive()) {
			activate();   
		}
	}

	@Override
	public CurrentStatus process(double delta) {
		activateIfInactive();

		raven.goals.Goal.CurrentStatus SubgoalStatus = ProcessSubgoals(delta);
		
		if (SubgoalStatus == Goal.CurrentStatus.completed || SubgoalStatus == Goal.CurrentStatus.failed)
		{
			if (!m_pOwner.isPossessed())
			{
				m_iStatus = Goal.CurrentStatus.inactive;
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
		Goal_Evaluator MostDesirable = new ExploreGoal_Evaluator(0.1);
		
		//iterate through all the evaluators to see which produces the highest score
		for( Goal_Evaluator eval : m_Evaluators) {
			double desire = eval.calculateDesirability(m_pOwner);
			if( desire >= best ) {
				best = desire;
				MostDesirable = eval;
			}
		}
		MostDesirable.setGoal(m_pOwner);
	}

	//---------------------------- notPresent --------------------------------------
	//
	//  returns true if the goal type passed as a parameter is the same as this
	//  goal or any of its subgoals
	//-----------------------------------------------------------------------------
	public boolean notPresent(GoalType goal)
	{
		if (!m_SubGoals.isEmpty()) {
			return !m_SubGoals.get(0).GetType().equals(goal);
		}
		
		return true;
	}

	public void queueGoal_moveToPosition(Vector2D pos) {
		m_SubGoals.add(new Goal_MoveToPosition(m_pOwner, pos));
		Log.debug("GoalThink", "Queued new Goal_MoveToPosition to bot " + m_pOwner.ID());
	}

	public void addGoal_moveToPosition(Vector2D p, Vector2D pos) {
		AddSubgoal( new Goal_MoveToPosition(m_pOwner, pos));
		Log.debug("GoalThink", "Added new Goal_MoveToPosition to bot " + m_pOwner.ID());
	}

	public void addGoal_explore() {
		if (notPresent(GoalType.goal_explore)) {
			removeAllSubgoals();
			AddSubgoal( new Goal_Explore(m_pOwner));
			Log.debug("GoalThink", "Added new Goal_Explore to bot " + m_pOwner.ID());
		}
	}

	public void addGoal_getItem(RavenObject inp) throws Exception{
		if (notPresent(Goal.GoalType.goal_get)) {
			removeAllSubgoals();
			AddSubgoal( new Goal_GetItem(m_pOwner, inp));
			Log.debug("GoalThink", "Added new Goal_GetITem to bot " + m_pOwner.ID());
		}
	}

	public void addGoal_attackTarget(){
		if (notPresent(Goal.GoalType.goal_attack_target)){
			removeAllSubgoals();
			AddSubgoal( new Goal_AttackTarget(m_pOwner));
			Log.debug("GoalThink", "Added new Goal_AttackTarget to bot " + m_pOwner.ID());
		}
	}
	
	public void render(){
		// only render the current goal.
		if(!m_SubGoals.isEmpty()) {
			m_SubGoals.get(0).render();
		}
	}

	public void renderEvaluations(Integer left, Integer top){ }

	public void queueGoal_moveToPosition(Vector2D pos, Vector2D p) {
		m_SubGoals.add(new Goal_MoveToPosition(m_pOwner, p));
		Log.debug("GoalThink", "Queued new Goal_MoveToPosition to bot " + m_pOwner.ID());
	}

	@Override
	public void terminate() {}
}