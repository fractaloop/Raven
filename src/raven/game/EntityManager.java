package raven.game;

import java.util.HashMap;
import java.util.Map;

import raven.game.interfaces.IRavenBot;
import raven.utils.Log;

public class EntityManager {
	private static class EntityManagerHolder {
		public static final EntityManager INSTANCE = new EntityManager();
	}
	
	private static int availableID = 0;

	public static synchronized int getAvailableID() {
		int toReturn = availableID;
		availableID++;
		return toReturn;
	}
	public static EntityManager getInstance() {
		return EntityManagerHolder.INSTANCE;
	}
	
	private Map<Integer, BaseGameEntity> entityMap = new HashMap<Integer, BaseGameEntity>();
	private Map<Integer, IRavenBot> botMap = new HashMap<Integer, IRavenBot>();
	
	private EntityManager() {}
	
	public static void registerEntity(BaseGameEntity entity) {
		Log.trace("ENTITY MANAGER", "Registered entity of type " + entity.entityType() + " and ID " + entity.ID()); 
		getInstance().entityMap.put(entity.ID(), entity);
	}
	
	public static void registerEntity(IRavenBot entity) {
		Log.trace("ENTITY MANAGER", "Registered entity of type " + entity.entityType() + " and ID " + entity.ID()); 
		getInstance().botMap.put(entity.ID(), entity);
	}

	public static BaseGameEntity getEntityFromID(int receiverID) {
		return getInstance().entityMap.get(receiverID);
	}
	
	public static void removeEntity(BaseGameEntity entity) {
		getInstance().entityMap.remove(entity);
		Log.trace("ENTITY MANAGER", "Removed entity of type " + entity.entityType() + " and ID " + entity.ID()); 
	}

	public static void reset() {
		Log.trace("ENTITY MANAGER", "Cleared entity listing"); 
		getInstance().entityMap.clear();
	}



}
