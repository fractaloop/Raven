/**
 * 
 */
package raven.game.interfaces;

import raven.game.messaging.Telegram;
import raven.goals.GoalThink;
import raven.math.Vector2D;

/**
 * @author brendan
 *
 */
public interface ITeam {


	//public GoalThink getBrain();
	public boolean handleMessage(Telegram msg);
	public void update(double delta);
	public Vector2D getTeamSpawnPoint();
	public void DraftBot(IRavenBot draftee);
}
