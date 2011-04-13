package raven.goals;

import java.util.ArrayList;

import raven.game.RavenBot;
import raven.game.navigation.NavGraphEdge;
import raven.math.Vector2D;

public class Goal_FollowPath extends GoalComposite<RavenBot> {

	  ArrayList<NavGraphEdge>  m_Path = new ArrayList<NavGraphEdge>();
	  RavenBot owner;
	
	
	
	
	public Goal_FollowPath(RavenBot m_pOwner, ArrayList<NavGraphEdge> list) {
		this.owner = m_pOwner;
		this.m_Path = list;
	}

	  //the usual suspects
	  void activate() {
	}
	  public Integer process() {
		return 0;
	}
	  public void render() {
	}
	  public void terminate(){}

	@Override
	public void renderAtPos(Vector2D p) {
		//do nothing
		
	}
	
	
	
}
