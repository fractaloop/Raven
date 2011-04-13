package raven.goals;

import raven.game.RavenBot;
import raven.game.RavenObject;
import raven.game.messaging.Telegram;
import raven.game.triggers.Trigger;
import raven.math.Vector2D;

public class Goal_GetItem extends GoalComposite<RavenBot> {

		  RavenObject           m_iItemToGet;

		  Trigger<RavenBot>       m_pGiverTrigger;

		  //true if a path to the item has been formulated
		  boolean                 m_bFollowingPath;

		  //returns true if the bot sees that the item it is heading for has been
		  //picked up by an opponent
		  
			public Goal_GetItem(RavenBot m_pOwner, RavenObject inp){
				m_iItemToGet = inp;
			//_pGiverTrigger = NEED TO PUT A TRIGGER HERE
				m_bFollowingPath = false;
				
			}
			
			
			
			public raven.goals.Goal.curStatus Process(){

				  activateIfInactive();

				  if (hasItemBeenStolen())
				  {
				    Terminate();
				  }

				  else
				  {
				    //process the subgoals
				    m_iStatus = ProcessSubgoals();
				  }

				  return m_iStatus;
			
			}

		  public void Activate(){
			  {
				  m_iStatus = Goal.curStatus.active;
				  
				//  m_pGiverTrigger = 0; NEED TO ASSIGN TRIGGER HERE
				  
				  //request a path to the item
				  getM_pOwner().getPathPlanner().RequestPathToItem(m_iItemToGet);

				  //the bot may have to wait a few update cycles before a path is calculated
				  //so for appearances sake it just wanders
				  AddSubgoal(new Goal_Wander(getM_pOwner()));

				}
			  
			  
			  
			  
			  
			  
			  
			  
			  
			  
		  }


		  @SuppressWarnings("unchecked")
		public boolean HandleMessage(Telegram msg){
			  //first, pass the message down the goal hierarchy
			  boolean bHandled = ForwardMessageToFrontMostSubgoal(msg);

			  //if the msg was not handled, test to see if this goal can handle it
			  if (bHandled == false)
			  {
			    switch(msg.msg)
			    {
			    case MSG_PATH_READY:

			      //clear any existing goals
			      removeAllSubgoals();

			      AddSubgoal(new Goal_FollowPath(getM_pOwner(), getM_pOwner().getPathPlanner().getPath()));

			      //get the pointer to the item
			      m_pGiverTrigger = (Trigger<RavenBot>) msg.extraInfo;

			      return true; //msg handled


			    case MSG_NO_PATH_AVAILABLE:

			      m_iStatus = Goal.curStatus.failed;

			      return true; //msg handled

			    default: return false;
			    }
			  }

			  //handled by subgoals
			  return true;
		  }

		  
		  
		  
		  
		  
		  
		  
		  
		  
		  
		  
		  
		  
		  
		  public void Terminate(){m_iStatus = Goal.curStatus.completed;}
		



	public boolean hasItemBeenStolen(){
		{
			  if (m_pGiverTrigger != null &&
			      !m_pGiverTrigger.isActive() &&
			      getM_pOwner().hasLOSto(m_pGiverTrigger.pos()) )
			  {
			    return true;
			  }

			  return false;
			}

}
	
	
	

	Goal.goalType ItemTypeToGoalType(RavenObject gt) throws Exception
	{
	  switch(gt)
	  {
	  case HEALTH:

	    return Goal.goalType.goal_get_health;

	  case SHOTGUN:

	    return Goal.goalType.goal_get_shotgun;
 
	  case RAIL_GUN:

	    return Goal.goalType.goal_get_railgun;

	  case ROCKET_LAUNCHER:

	    return Goal.goalType.goal_get_rocket_launcher;

	  default: 
		  throw new Exception("Goal_GetItem cannot determine item type");

	  }//end switch
	}



	@Override
	public void renderAtPos(Vector2D p) {
		// do nothing
		
	}



	@Override
	public void render() {
		// do nothing
		
	}
	
	
	}
