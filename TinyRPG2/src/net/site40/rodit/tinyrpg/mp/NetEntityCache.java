package net.site40.rodit.tinyrpg.mp;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import net.site40.rodit.tinyrpg.game.entity.Entity;

public class NetEntityCache {

	private Object entityLock;

	private Map<String, Entity> nameCache;
	
	public NetEntityCache(){
		this.entityLock = new Object();

		this.nameCache = Collections.synchronizedMap(new HashMap<String, Entity>());
	}

	public Entity get(String name){
		synchronized(entityLock){
			return nameCache.get(name);
		}
	}

	public void put(Entity entity){
		synchronized(entityLock){
			nameCache.put(entity.getName(), entity);
		}
	}

	public void putSpecial(String name, Entity entity){
		synchronized(entityLock){
			nameCache.put(name, entity);
		}
	}

	public void clear(){
		synchronized(entityLock){
			nameCache.clear();
		}
	}
	
	public void update(Entity e){
		Entity cached = get(e.getName());
		if(cached == null)
			put(e);
		else
			cached.copy(e);
	}
}
