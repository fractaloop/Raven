package raven.goals;

import raven.game.RavenBot;
import raven.game.RavenObject;
import raven.game.messaging.Telegram;
import raven.game.triggers.Trigger;

public class Goal_GetItem extends GoalComposite<RavenBot> {

	RavenObject           m_iItemToGet;

	Trigger<RavenBot>       m_pGiverTrigger;

	//true if a path to the item has been formulated
	boolean                 m_bFollowingPath;

	//returns true if the bot sees that the item it is heading for has been
	//picked up by an opponent

	public Goal_GetItem(RavenBot m_pOwner, RavenObject item) {
		super(m_pOwner, ItemTypeToGoalType(item));
		m_iItemToGet = item;
		m_bFollowingPath = false;

	}



	@Override
	public raven.goals.Goal.CurrentStatus process(double delta){

		activateIfInactive();

		if (hasItemBeenStolen())
		{
			terminate();
		}

		else
		{
			//process the subgoals
			m_iStatus = ProcessSubgoals(delta);
		}

		return m_iStatus;

	}

	@Override
	public void activate(){
		m_iStatus = Goal.CurrentStatus.active;

		//  m_pGiverTrigger = 0; NEED TO ASSIGN TRIGGER HERE

		//request a path to the item
		m_pOwner.getPathPlanner().requestPathToItem(m_iItemToGet);

		//the bot may have to wait a few update cycles before a path is calculated
		//so for appearances sake it just wanders
		AddSubgoal(new Goal_Wander(m_pOwner));
	}


	@SuppressWarnings("unchecked")
	public boolean HandleMessage(Telegram msg){
		//first, pass the message down the goal hierarchy
		boolean bHandled = ForwardMessageToFrontMostSubgoal(msg);

		//if the msg was not handled, test to see if this goal can handle it
		if (bHandled == false)
		{
			switch(msg.msg)	{
				case MSG_PATH_READY:
					//clear any existing goals
					removeAllSubgoals();
					AddSubgoal(new Goal_FollowPath(m_pOwner, m_pOwner.getPathPlanner().getPath()));
					//get the pointer to the item
					m_pGiverTrigger = (Trigger<RavenBot>) msg.extraInfo;
					return true; //msg handled
				case MSG_NO_PATH_AVAILABLE:
					m_iStatus = Goal.CurrentStatus.failed;
					return true; //msg handled
				default: 
					return false;
			}
		}
		//handled by subgoals
		return true;
	}

	@Override
	public void terminate(){m_iStatus = Goal.CurrentStatus.completed;}

	public boolean hasItemBeenStolen(){ 
		if (m_pGiverTrigger != null && !m_pGiverTrigger.isActive() && m_pOwner.hasLOSto(m_pGiverTrigger.pos())) {
				return true;
		} else return false;
	}

	static Goal.GoalType ItemTypeToGoalType(RavenObject gt){
		switch(gt) {
			case HEALTH:
				return Goal.GoalType.goal_get_health;
			case SHOTGUN:
				return Goal.GoalType.goal_get_shotgun;
			case RAIL_GUN:
				return Goal.GoalType.goal_get_railgun;
			case ROCKET_LAUNCHER:
				return Goal.GoalType.goal_get_rocket_launcher;
			default:
				return GoalType.unknown_type;
		}//end switch
	}
}
