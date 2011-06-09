package raven.game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import raven.game.interfaces.IRavenBot;
import raven.math.Vector2D;
import raven.ui.GameCanvas;

public class RavenSensoryMemory {
	protected class MemoryRecord {
		/** records the time the opponent was last sensed (seen or heard).
		 * This is used to determine if a bot can 'remember' this record or
		 * not. (if System.nanoTime() - timeLastSensed is greater than the
		 * bot's memory span, the data in this record is made unavailable
		 * to clients) */
		public double timeSinceLastSensed;
		/** it can be useful to know how long an opponent has been visible.
		 * This variable is tagged with the current time whenever an opponent 
		 * first becomes visible. It's then a simple matter to calculate how
		 * long the opponent has been in view
		 * (currentTime - timeBecameVisible) */
		public double timeSinceBecameVisible;
		/** it can be useful to know the last time an opponent was seen */
		public double timeSinceLastVisible;
		/** a vector marking the position where the opponent was last sensed.
		 * This can be used to help hunt down an opponent if it goes out of
		 * view */
		public Vector2D lastSensedPosition;
		/** true if opponent is within the field of view of the owner */
		public boolean withinFOV;
		/** set to true if there is no obstruction between the opponent and
		 * the owner, permitting a shot. */
		public boolean shootable;

		public MemoryRecord() {
			timeSinceLastSensed = timeSinceBecameVisible = -999;
			timeSinceLastVisible = 0;
			withinFOV = shootable = false;			
		}
	}

	/** the owner of this instance */
	private RavenBot owner;

	/** this container is used to simulate memory of sensory events. A
	 * MemoryRecord is created for each opponent in the environment. Each
	 * record is updated whenever the opponent is encountered. (when it is
	 * seen or heard) */
	private Map<IRavenBot, MemoryRecord> memoryMap;

	/** a bot has a memory span equivalent to this value. When a bot requests
	 * a list of all recently sensed opponents this value is used to determine
	 * if the bot is able to remember an opponent or not. */
	private double memorySpan;

	private void makeNewRecordIfNotAlreadyPresent(IRavenBot bot) {
		if (!memoryMap.containsKey(bot)) {
			memoryMap.put(bot, new MemoryRecord());
		}
	}

	public RavenSensoryMemory(RavenBot owner, double memorySpan) {
		this.owner = owner;
		// Store memory length in seconds
		this.memorySpan = memorySpan;
		this.memoryMap = new HashMap<IRavenBot, MemoryRecord>();
	}

	/**
	 * this method is used to update the memory map whenever an opponent makes
	 * a noise
	 * @param noiseMaker the bot that made the noise
	 */
	public void updateWithSoundSource(IRavenBot noiseMaker) {
		if (!owner.equals(noiseMaker)) {
			makeNewRecordIfNotAlreadyPresent(noiseMaker);

			MemoryRecord info = memoryMap.get(noiseMaker);

			if (owner.getWorld().isLOSOkay(owner.pos(), noiseMaker.pos())) {
				info.shootable = true;

				info.lastSensedPosition = noiseMaker.pos();
			} else {
				info.shootable = false;
			}

			info.timeSinceLastSensed = 0;
		}
	}

	/**
	 * this removes a bot's record from memory
	 * @param removedBot the bot to forget about
	 */
	public void removeBotFromMemory(IRavenBot removedBot) {
		memoryMap.remove(removedBot);
	}

	/** this method iterates through all the opponents in the game world and
	 * updates the records of those that are in the owner's FOV */
	public void updateVision(double delta) {
		// for each bot in the world test to see if it is visible to the owner of
		// this class
		List<IRavenBot> bots = owner.getWorld().getBots();

		for (IRavenBot bot : bots) {
			// make sure the bot being examined is not this bot
			if (!bot.equals(owner)) {
				// make sure it is part of the memory map
				makeNewRecordIfNotAlreadyPresent(bot);

				// get a reference to this bot's data
				MemoryRecord info = memoryMap.get(bot);

				// test if there is LOS between bots
				if (owner.getWorld().isLOSOkay(owner.pos(), bot.pos())) {
					info.shootable = true;

					// test if the bot is within FOV
					if (Vector2D.isSecondInFOVOfFirst(owner.pos(), owner.facing(), bot.pos(), owner.fieldOfView())) {
						info.timeSinceLastSensed = 0;
						info.lastSensedPosition = new Vector2D(bot.pos());
						info.timeSinceLastVisible = 0;
						info.timeSinceBecameVisible += delta;

						if (info.withinFOV == false) {
							info.withinFOV = true;
							info.timeSinceBecameVisible = delta;
						}
					} else {
						info.withinFOV = false;
						info.timeSinceLastVisible += delta;
						info.timeSinceLastSensed += delta;
					}
				} else {
					info.shootable = false;
					info.withinFOV = false;
				}
			}
		}
	}

	// Queries

	public boolean isOpponentShootable(IRavenBot opponent) {
		MemoryRecord info = memoryMap.get(opponent);
		return (info == null) ? false : info.shootable;
	}

	public boolean isOpponentWithinFOV(IRavenBot currentTarget) {
		MemoryRecord info = memoryMap.get(currentTarget);
		return (info == null) ? false : info.withinFOV;
	}

	public Vector2D getLastRecordedPositionOfOpponent(IRavenBot opponent) {
		MemoryRecord info = memoryMap.get(opponent);
		if (info == null) {
			throw new RuntimeException("RavenSensoryMemory#getLastRecordedPositionOfOpponent: Attempting to get position of unrecorded bot");
		} else {
			return info.lastSensedPosition;
		}
	}

	public double getTimeOpponentHasBeenVisible(IRavenBot opponent) {
		MemoryRecord info = memoryMap.get(opponent);
		return (info == null) ? 0 : info.timeSinceBecameVisible;		
	}

	public double getTimeSinceLastSensed(IRavenBot opponent) {
		MemoryRecord info = memoryMap.get(opponent);
		return (info == null) ? 0 : info.timeSinceLastSensed;		
	}

	public double getTimeOpponentHasBeenOutOfView(IRavenBot opponent) {
		MemoryRecord info = memoryMap.get(opponent);
		return (info == null) ? Double.MAX_VALUE : info.timeSinceLastVisible;

	}

	public List<IRavenBot> getListOfRecentlySensedOpponents() {
		List<IRavenBot> opponents = new ArrayList<IRavenBot>();

		for (IRavenBot bot : memoryMap.keySet()) {
			if (memoryMap.get(bot).timeSinceLastSensed < memorySpan) {
				opponents.add(bot);
			}
		}

		return opponents;
	}

	public void renderBoxesAroundRecentlySensed() {
		List<IRavenBot> opponents = getListOfRecentlySensedOpponents();

		for (IRavenBot bot : opponents) {
			Vector2D p = bot.pos();
			double b = bot.getBRadius();

			GameCanvas.line(p.x-b, p.y-b, p.x+b, p.y-b);
			GameCanvas.line(p.x+b, p.y-b, p.x+b, p.y+b);
			GameCanvas.line(p.x+b, p.y+b, p.x-b, p.y+b);
			GameCanvas.line(p.x-b, p.y+b, p.x-b, p.y-b);
		}
	}
}
