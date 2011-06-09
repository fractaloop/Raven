/**
 * 
 */
package raven.game.test;

import junit.framework.Assert;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Test;

import raven.game.RavenBot;
import raven.game.interfaces.IRavenBot;
import raven.game.triggers.TriggerHealthGiver;
import raven.math.Vector2D;

/**
 * @author chester
 *
 */
public class TriggerTest {
	
	private Mockery mocker = new Mockery();
	/**
	 * We will create a health trigger at a certain location and then create a bot on top, then call the triggers'
	 * tryActivate method, then check if the bot was given the new health.
	 */
	@Test
	public void Bot_Gets_Health_When_Activating_Trigger(){
		Vector2D triggerPosition = new Vector2D(0, 0);
		final Vector2D botPosition = new Vector2D(5, 5);
		// Create the trigger
		TriggerHealthGiver healthTrigger = new TriggerHealthGiver(triggerPosition, 10, 15);
		
		// Create the bot
		final IRavenBot bot = mocker.mock(IRavenBot.class);
		mocker.checking(new Expectations() {{
			oneOf(bot).isReadyForTriggerUpdate(); will(returnValue(true));
			oneOf(bot).isAlive(); will(returnValue(true));
			allowing(bot).pos(); will(returnValue(botPosition));
			oneOf(bot).getBRadius(); will(returnValue(10.0));
			oneOf(bot).increaseHealth(15);
		}});
		
		// need to verify that increaseHealth was called.
		healthTrigger.tryTrigger(bot);
		
	}
	
	@Test
	public void Bot_On_Trigger_Is_Touching_Trigger(){
		Vector2D triggerPosition = new Vector2D(0, 0);
		Vector2D botPosition = new Vector2D(5, 5);
		// Create the trigger
		TriggerHealthGiver healthTrigger = new TriggerHealthGiver(triggerPosition, 10, 15);
		
		// Create the bot
		RavenBot bot = new RavenBot(botPosition, 50);
		Assert.assertTrue(healthTrigger.isTouchingTrigger(bot));
	}
}
