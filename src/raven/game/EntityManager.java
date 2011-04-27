package raven.game;

import java.util.HashMap;
import java.util.Map;

import raven.utils.LogManager;

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
	
	private EntityManager() {}
	
	public static void registerEntity(BaseGameEntity entity) {
		LogManager.GetInstance().Info("ENTITY MANAGER - Registered entity of type " + entity.entityType() + " and ID " + entity.ID()); 
		getInstance().entityMap.put(entity.ID(), entity);
	}

	public static BaseGameEntity getEntityFromID(int receiverID) {
		return getInstance().entityMap.get(receiverID);
	}
	
	public static void removeEntity(BaseGameEntity entity) {
		getInstance().entityMap.remove(entity);
		LogManager.GetInstance().Info("ENTITY MANAGER - Registered entity of type " + entity.entityType() + " and ID " + entity.ID()); 
	}

	public static void reset() {
		LogManager.GetInstance().Info("ENTITY MANAGER - Cleared entity listing"); 
		getInstance().entityMap.clear();
	}



}
