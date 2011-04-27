package raven.game;


/**
 * Generic type definitions for Raven objects.
 * 
 * @author Logan Lowell
 *
 */
public enum RavenObject {
	WALL(0, "Wall"),
	BOT(1, "Bot"),
	UNUSED(2, "Knife"),
	WAYPOINT(3, "Waypoint"),
	HEALTH(4, "Health"),
	SPAWN_POINT(5, "Spawn Point"),
	RAIL_GUN(6, "Rail Gun"),
	ROCKET_LAUNCHER(7, "Rocket Launcher"),
	SHOTGUN(8, "Shotgun"),
	BLASTER(9, "Blaster"),
	OBSTACLE(10, "Obstacle"),
	SLIDING_DOOR(11, "Sliding Door"),
	TRIGGER(12, "Door Trigger"),
	PROJECTILE(13, "Projectile");
	
	private final int index;
	private final String description;
	
	RavenObject(int index, String description) {
		this.index = index;
		this.description = description;
	}
	
	public int getIndex() {
		return index;
	}
	
	public String getDescription() {
		return description;
	}
	
	public int toInteger() {
		return index;
	}

	public static RavenObject resolveType(int entityType) {
		for (RavenObject object : RavenObject.class.getEnumConstants()) {
			if (object.toInteger() == entityType)
				return object;
		}
		return RavenObject.UNUSED;
	}
};
