package raven.game;

import java.util.HashMap;
import java.util.Map;

public class EntityManager {
	private static class EntityManagerHolder {
		public static final EntityManager INSTANCE = new EntityManager();
	}

	public static EntityManager getInstance() {
		return EntityManagerHolder.INSTANCE;
	}
	
	private Map<Integer, BaseGameEntity> entityMap = new HashMap<Integer, BaseGameEntity>();
	
	private EntityManager() {}
	
	public static void registerEntity(RavenBot bot) {
		getInstance().entityMap.put(bot.ID(), bot);
	}

	public static BaseGameEntity getEntityFromID(int receiverID) {
		return getInstance().entityMap.get(receiverID);
	}
	
	public static void removeEntity(BaseGameEntity entity) {
		getInstance().entityMap.remove(entity);
	}

	public static void reset() {
		getInstance().entityMap.clear();
	}



}
