package raven.game;

import raven.game.interfaces.ITeam;
import raven.game.messaging.Telegram;
//import raven.game.RavenBot;
import raven.game.interfaces.IRavenBot;
//import raven.goals.GoalThink;

import java.util.List;
import raven.math.*;
import java.util.ArrayList;


/* To Do
//List bots	
//List spawn points
//process team goal (make new goals for teams)
//Handle messages
 * Other stuff- kill entity manager
 * make "loose bots"? Ask
 * add to iraven interface (?) In Progress
 *
 * 
 * Teams are based on a leader
 * protect leader
 * Find leader
 * Scatter(Leader is dead)
 * Kill enemy leader
 * Murder defenders
 * 
 * Just different flavors of pursuit, or evade.
 * Also there needs to be a way to tell the leader to move more slowly
 * have more hit points, etc.  
 */


public class Team extends BaseGameEntity implements ITeam
{
	//Private vars
	
	////A list of bots on the team, should be references, I'll ask
	private	List<IRavenBot> teamBots; 

	///We need a valid location 
	private ArrayList<Vector2D> teamSpawnPoints;

	//Goal queue? 
	//private GoalThink teamBrain;
	
	public Team(int id)
	{
			super(id);
			//Can I just modify this constructor?
			
			//Just so we don't get some null point exception
			//Just for testing.
			teamSpawnPoints.add( new Vector2D(0,0));
			
			//teamBrain = new GoalThink(this);
	
	}

	
	//We want a way to add a bot to the list of bots on this team
	//But, we're in the middle of gutting out the entity manager
	//so we should probably just accept a reference to a bot.
	public void DraftBot(IRavenBot draftee) {
	//Ask if this works as a reference
	}
	
	///We may want to add a clear/remove team association. 

	
	
	@Override
	public void render()
	{}
	
	@Override
	public boolean handleMessage(Telegram msg) {
		// We need to implement the ability to handle a 
		// broad or multicast.
		// we could treat every message to the team as multicast
		
	return false;
	}
	//		TEAM_RED,TEAM_GREEN

	public Vector2D getTeamSpawnPoint(){
		return teamSpawnPoints.get(0);
	}
	
	////We need new goals/brains
	/*
	public GoalThink getBrain() {
		return teamBrain;
	}
	*/
}
