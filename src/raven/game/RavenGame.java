package raven.game;

import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

import raven.Raven;
import raven.game.armory.Bolt;
import raven.game.armory.Pellet;
import raven.game.armory.RavenProjectile;
import raven.game.armory.Rocket;
import raven.game.armory.Slug;
import raven.game.messaging.Dispatcher;
import raven.game.messaging.RavenMessage;
import raven.game.navigation.PathManager;
import raven.game.navigation.RavenPathPlanner;
import raven.math.Vector2D;
import raven.math.WallIntersectionTest;
import raven.script.RavenScript;
import raven.ui.GameCanvas;
import raven.utils.MapSerializer;

public class RavenGame {
	/** the current game map */
	private RavenMap map;

	/** bots that inhabit the current map */
	private ArrayList<RavenBot> bots;

	/** A user may control a bot manually. This is that bot */
	private RavenBot selectedBot;

	/** contains any active projectiles (slugs, rockets, shotgun pellets, etc) */
	private ArrayList<RavenProjectile> projectiles;

	/** manages all the path planning requests */
	PathManager<RavenPathPlanner> pathManager;

	/** true if the game is paused */
	boolean paused;

	/** true if a bot is removed from the game */
	boolean removeBot;

	/**
	 * When a bot is killed a "grave" is displayed for a few seconds. This class
	 * manages the graves.
	 */
	GraveMarkers graveMarkers;

	private void clear() {
		// delete the bots
		bots.clear();
		// delete any active projectiles
		projectiles.clear();
	}

	private boolean attemptToAddBot(RavenBot bot) {
		if (map.getSpawnPoints().size() <= 0) {
			System.err.println("Map has no spawn points!");
			return false;
		}

		// we'll make the same number of attempts to spawn a bot this update
		// as there are spawn points
		int attempts = map.getSpawnPoints().size();

		while (--attempts >= 0) {
			// select a random spawn point
			Vector2D pos = map.getRandomSpawnPoint();

			// check to see if it's occupied
			boolean available = true;
			for (RavenBot other : bots) {
				if (pos.distance(other.pos()) < other.getBRadius()) {
					available = false;
				}
			}

			if (available) {
				bot.spawn(pos);

				return true;
			}
		}

		return false;
	}

	private void notifyAllBotsOfRemoval(RavenBot bot) {
		for (RavenBot other : bots) {
			Dispatcher.dispatchMsg(Dispatcher.SEND_MSG_IMMEDIATELY,
					Dispatcher.SENDER_ID_IRRELEVANT, other.ID(),
					RavenMessage.MSG_USER_HAS_REMOVED_BOT, bot);
		}
	}

	/** Open a Swing file chooser to pick a Raven map */
	private String chooseMapFile() {
		JFileChooser chooser = new JFileChooser(new File("./maps"));
		chooser.setFileFilter(new FileFilter() {
			@Override
			public boolean accept(File file) {
				if (file.isDirectory()) {
					return false;
				}

				return file.getName().endsWith(".raven");
			}

			@Override
			public String getDescription() {
				return "Raven levels (*.raven)";
			}
		});
		int chooseResult = chooser.showOpenDialog(null);
		
		if (chooseResult == JFileChooser.APPROVE_OPTION) {
			return chooser.getSelectedFile().getPath();
		} else {
			return null;
		}
	}

	// /////////////////
	// Public methods

	public RavenGame(Graphics g) {
		bots = new ArrayList<RavenBot>();
		projectiles = new ArrayList<RavenProjectile>();
		GameCanvas.setGraphics(g);
		String path = "<undefined>";
		try {
			path = chooseMapFile();
			if (path == null)
				path = RavenScript.getString("StartMap");
			else
				loadMap(path);
			
			
		} catch (IOException e) {
			System.err.println("Failed to load map: " + path + ". Reason: \n" + e.getLocalizedMessage());
			System.exit(1);
		}
	}

	/** The usual suspects */
	public void render() {
		
		graveMarkers.render();
		
		// render the map
		map.render();
		
		// render all the bots unless the user has selected the option to only
		// render those bots that are in the fov of the selected bot
		if (selectedBot != null && RavenUserOptions.onlyShowBotsInTargetsFOV) {
			List<RavenBot> visibleBots = getAllBotsInFOV(selectedBot);
			
			for (RavenBot bot : visibleBots) {
				bot.render();
			}
		} else {
			// render all the bots
			for (RavenBot bot : bots) {
				bot.render();
			}			
		}
		
		// render any projectiles
		for (RavenProjectile projectile : projectiles) {
			projectile.render();
		}
		
		// render a red circle around the selected bot (blue if possessed)
		if (selectedBot != null) {
			if (selectedBot.isPossessed()) {
				GameCanvas.bluePen();
			} else {
				GameCanvas.redPen();
			}
			GameCanvas.hollowBrush();
			GameCanvas.circle(selectedBot.pos(), selectedBot.getBRadius() + 1);

			if (RavenUserOptions.showOpponentsSensedBySelectedBot) {
				selectedBot.getSensoryMem().renderBoxesAroundRecentlySensed();
			}
			
			// render a square around the bot's target
			if (RavenUserOptions.showTargetOfSelectedBot && selectedBot.getTargetBot() != null) {
				GameCanvas.thickRedPen();
				
				Vector2D p = selectedBot.getTargetBot().pos();
				double b = selectedBot.getTargetBot().getBRadius();
				
				GameCanvas.line(p.x - b, p.y - b, p.x + b, p.y - b);
				GameCanvas.line(p.x + b, p.y - b, p.x + b, p.y + b);
				GameCanvas.line(p.x + b, p.y + b, p.x - b, p.y + b);
				GameCanvas.line(p.x - b, p.y + b, p.x - b, p.y - b);
			}
			
			// render the path of the bot
			if (RavenUserOptions.showPathOfSelectedBot) {
				selectedBot.getBrain().render();
			}
			
			// display the bot's goal stack
			if (RavenUserOptions.showGoalsOfSelectedBot) {
				Vector2D p = new Vector2D(selectedBot.pos().x - 50, selectedBot.pos().y);
				
				selectedBot.getBrain().renderAtPos(p, selectedBot.getBrain().GetType().toString());
			}
		}
	}

	/**
	 * Update the game state over the given timestep in seconds.
	 * 
	 * @param delta
	 *            amount of time to advance in seconds
	 */
	public void update(double delta) {
		// don't update if the user has paused the game
		if (paused)
			return;
		
		graveMarkers.update(delta);
		
		// GetPlayerInput();
		
		// update all the queued searches in the path manager
		pathManager.updateSearches();
		
		// update any doors
		for (RavenDoor door : map.getDoors()) {
			door.update(delta);
		}
		
		// update any current projectiles
		HashSet<RavenProjectile> toRemove = new HashSet<RavenProjectile>();
		for (RavenProjectile projectile : projectiles) {
			if (!projectile.IsDead()) {
				toRemove.add(projectile);
			} else {
				projectile.update(delta);
			}
		}
		projectiles.removeAll(toRemove);
		
		// update the bots
		boolean spawnPossible = false;
		
		for (RavenBot bot : bots) {
			// if this bot's status is 'respawning' attempt to resurrect it
			// from an unoccupied spawn point
			if (bot.isSpawning() && spawnPossible) {
				spawnPossible = attemptToAddBot(bot);
			}
			// if this bot's status is 'dead' add a grave at its current
			// location then change its status to 'respawning'
			else if (bot.isDead()) {
				graveMarkers.addGrave(bot.pos());
				bot.setSpawning();
			}
		    // if this bot is alive update it.
			else if (bot.isAlive()) {
				bot.update(delta);
			}
		}
		
		// update the triggers
		map.updateTriggerSystem(delta, bots);
		
		// if the user has requested that the number of bots be decreased,
		// remove one
		if (removeBot) {
			if (!bots.isEmpty()) {
				RavenBot bot = bots.get(bots.size() - 1);
				if (bot.equals(selectedBot)) {
					selectedBot = null;
				}
				notifyAllBotsOfRemoval(bot);
				bots.remove(bot);
			}
			
			removeBot = false;
		}
	}

	/** Loads an environment from a file 
	 * @throws IOException */
	public boolean loadMap(String fileName) throws IOException {
		// clear any current bots and projectiles
		clear();

		// out with the old
		map = null;
		graveMarkers = null;
		pathManager = null;

		graveMarkers = new GraveMarkers(RavenScript.getDouble("GraveLifetime"));
		pathManager = new PathManager<RavenPathPlanner>(
				RavenScript.getInt("MaxSearchCyclesPerUpdateStep"));
		map = MapSerializer.deserializeMapFromPath(fileName);

		EntityManager.reset();

		addBots(RavenScript.getInt("NumBots"));
		map.render();
		return true;
	}

	public void addBots(int numBotsToAdd) {
		while (--numBotsToAdd > 0) {
			// create a bot. (its position is irrelevant at this point because
			// it will not be rendered until it is spawned)
			RavenBot bot = new RavenBot(this, new Vector2D());
			
			// switch the default steering behaviors on
			bot.getSteering().wallAvoidanceOn();
			bot.getSteering().separationOn();
			
			bots.add(bot);
			
			// register the bot with the entity manager
			EntityManager.registerEntity(bot);
		}
	}

	public void addRocket(RavenBot shooter, Vector2D target) {
		RavenProjectile rocket = new Rocket(shooter, target);
		
		projectiles.add(rocket);
	}

	public void addRailGunSlug(RavenBot shooter, Vector2D target) {
		RavenProjectile slug = new Slug(shooter, target);
		
		projectiles.add(slug);
	}

	public void addShotGunPellet(RavenBot shooter, Vector2D target) {
		RavenProjectile pellet = new Pellet(shooter, target);
		
		projectiles.add(pellet);
	}

	public void addBolt(RavenBot shooter, Vector2D target) {
		RavenProjectile bolt = new Bolt(shooter, target);
		
		projectiles.add(bolt);
	}

	/** removes the last bot to be added */
	public void removeBot() {
		removeBot = true;
	}

	/**
	 * returns true if a bot of size BoundingRadius cannot move from A to B
	 * without bumping into world geometry. It achieves this by stepping from
	 * A to B in steps of size BoundingRadius and testing for intersection
	 * with world geometry at each point.
	 */
	public boolean isPathObstructed(Vector2D a, Vector2D b, double boundingRadius) {
		Vector2D toB = b.sub(a);
		toB.normalize();
		
		Vector2D curPos = a;
		
		while (curPos.distanceSq(b) > boundingRadius * boundingRadius) {
			// advance curPos one step
			curPos = curPos.add(toB.mul(0.5).mul(boundingRadius));
			
			if (WallIntersectionTest.doWallsIntersectCircle(map.getWalls(), curPos, boundingRadius)) {
				return true;
			}
		}
		
		return false;
	}

	/** returns of bots in the FOV of the given bot */
	public List<RavenBot> getAllBotsInFOV(final RavenBot bot) {
		ArrayList<RavenBot> visibleBots = new ArrayList<RavenBot>();
		
		for (RavenBot other : bots) {
			// make sure time is not wasted checking against the same bot or
			// against a bot that is dead or re-spawning
			if (bot.equals(other) || !other.isAlive())
				continue;
		    
			// first of all test to see if this bot is within the FOV
			if (Vector2D.isSecondInFOVOfFirst(bot.pos(), bot.facing(), other.pos(), bot.fieldOfView())) {
				// cast a ray from between the bots to test visibility. If the
				// bot is visible add it to the vector
				if (!WallIntersectionTest.doWallsObstructLineSegment(bot.pos(), other.pos(), map.getWalls())) {
					visibleBots.add(other);
				}	
			}
		}
		return visibleBots;
	}

	/**
	 * returns true if the second bot is unobstructed by walls and in the field
	 * of view of the first.
	 */
	public boolean isSecondVisibleToFirst(final RavenBot first, final RavenBot second) {
		// if the two bots are equal or if one of them is not alive return
		// false
		if (!first.equals(second) && second.isAlive()) {
			if (Vector2D.isSecondInFOVOfFirst(first.pos(), first.facing(), second.pos(), second.fieldOfView())) {
				if (!WallIntersectionTest.doWallsObstructLineSegment(first.pos(), second.pos(), map.getWalls())) {
					return true;
				}
			}
		}
		
		return false;
	}

	/** returns true if the ray between A and B is unobstructed. */
	public boolean isLOSOkay(final Vector2D A, final Vector2D B) {
		return !WallIntersectionTest.doWallsObstructLineSegment(A, B, map.getWalls());
	}

	/**
	 * starting from the given origin and moving in the direction Heading this
	 * method returns the distance to the closest wall
	 * 
	 * Note: This function is not implemented in the C++ version!
	 */
	public double getDistanceToClosestWall(Vector2D origin, Vector2D heading) {
		return 0;
	}

	/**
	 * returns the position of the closest visible switch that triggers the door
	 * of the specified ID
	 */
	public Vector2D getPosOfClosestSwitch(Vector2D botPos, int doorID) {
		List<Integer> switchIDs = new ArrayList<Integer>();
		
		for (RavenDoor door : map.getDoors()) {
			if (door.ID() == doorID) {
				switchIDs = door.getSwitchIDs();
				break;
			}
		}
		
		Vector2D closest = null;
		double closestDist = Double.MAX_VALUE;
		
		for (Integer switchID : switchIDs) {
			BaseGameEntity trig = EntityManager.getEntityFromID(switchID);
			
			if (isLOSOkay(botPos, trig.pos())) {
				double dist = botPos.distanceSq(trig.pos());
				
				if (dist < closestDist) {
					closestDist = dist;
					closest = trig.pos();
				}
			}
		}
		
		return closest;
	}

	/**
	 * given a position on the map this method returns the bot found with its
	 * bounding radius of that position.If there is no bot at the position the
	 * method returns null
	 * 
	 * @param cursorPos
	 * @return
	 */
	public RavenBot getBotAtPosition(Vector2D cursorPos) {
		for (RavenBot bot : bots) {
			if (bot.pos().distance(cursorPos) < bot.getBRadius()) {
				if (bot.isAlive()) {
					return bot;
				}
			}
		}
		
		return null;
	}

	public void togglePause() {
		paused = !paused;
	}

	/**
	 * this method is called when the user clicks the right mouse button. The
	 * method checks to see if a bot is beneath the cursor. If so, the bot is
	 * recorded as selected.If the cursor is not over a bot then any selected
	 * bot/s will attempt to move to that position.
	 * 
	 * @param p
	 *            the location clicked
	 */
	public void clickRightMouseButton(Vector2D p) {
		RavenBot bot = getBotAtPosition(p);
		
		// if there is no selected bot just return
		if (bot == null && selectedBot == null)
			return;
		
		// if the cursor is over a different bot to the existing selection,
		// change selection
		if (bot != null && !bot.equals(selectedBot)) {
			if (selectedBot != null) {
				selectedBot.exorcise();
			}
			selectedBot = bot;
		}
		
		// if the user clicks on a selected bot twice it becomes possessed
		// (under the player's control)
		if (bot != null && bot.equals(selectedBot)) {
			selectedBot.takePossession();
			
			// clear any current goals
			selectedBot.getBrain().removeAllSubgoals();
		}
		
		if (selectedBot.isPossessed()) {
			// if the shift key is pressed down at the same time as clicking
			// then the movement command will be queued
			if (Raven.isKeyPressed(KeyEvent.VK_SHIFT)) {
				selectedBot.getBrain().queueGoal_moveToPosition(selectedBot.pos(), p);
			} else {
				selectedBot.getBrain().removeAllSubgoals();
				selectedBot.getBrain().addGoal_moveToPosition(selectedBot.pos(), p);
			}
		}
	}

	/**
	 * this method is called when the user clicks the left mouse button. If
	 * there is a possessed bot, this fires the weapon, else does nothing
	 * 
	 * @param p
	 *            the location clicked
	 */
	public void clickLeftMouseButton(Vector2D p) {
		if (selectedBot != null && selectedBot.isPossessed()) {
			selectedBot.fireWeapon(p);
		}
	}

	/** when called will release any possessed bot from user control */
	public void exorciseAnyPossessedBot() {
		if (selectedBot != null) {
			selectedBot.exorcise();
		}
	}

	/**
	 * if a bot is possessed the keyboard is polled for user input and any
	 * relevant bot methods are called appropriately
	 */
	public void getPlayerInput() {
		if (selectedBot != null && selectedBot.isPossessed()) {
			selectedBot.rotateFacingTowardPosition(Raven.getClientCursorPosition());
		}
	}

	/** Get the value of a selected bot. null if none is selected */
	public RavenBot possessedBot() {
		return selectedBot;
	}

	/** Change to a new weapon for a possessed bot. */
	public void changeWeaponOfPossessedBot(RavenObject weapon) {
		if (selectedBot != null) {
			switch (weapon) {
			case BLASTER:
				possessedBot().changeWeapon(RavenObject.BLASTER);
				break;
			case SHOTGUN:
				possessedBot().changeWeapon(RavenObject.SHOTGUN);
				break;
			case ROCKET_LAUNCHER:
				possessedBot().changeWeapon(RavenObject.ROCKET_LAUNCHER);
				break;
			case RAIL_GUN:
				possessedBot().changeWeapon(RavenObject.ROCKET_LAUNCHER);
				break;
			}
		}
	}

	//////////////
	// Accessors

	public RavenMap getMap() {
		return map;
	}

	public ArrayList<RavenBot> getBots() {
		return bots;
	}

	public PathManager<RavenPathPlanner> getPathManager() {
		return pathManager;
	}

	public int getNumBots() {
		return bots.size();
	}
	
	/** Some weird helper method */
	public void tagRavenBotsWithinViewRange(RavenBot ravenBot,
			double viewDistance) {
		// TODO Auto-generated method stub
		
	}

}
