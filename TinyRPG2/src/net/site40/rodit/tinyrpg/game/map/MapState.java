package net.site40.rodit.tinyrpg.game.map;

import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Pattern;

import android.graphics.Canvas;
import android.graphics.RectF;
import android.util.Log;
import net.site40.rodit.tinyrpg.game.Game;
import net.site40.rodit.tinyrpg.game.GameObject;
import net.site40.rodit.tinyrpg.game.entity.Entity;
import net.site40.rodit.tinyrpg.game.entity.EntityLiving;
import net.site40.rodit.tinyrpg.game.entity.EntityPlayer;
import net.site40.rodit.tinyrpg.game.entity.npc.EntityNPC;
import net.site40.rodit.tinyrpg.game.event.EventReceiver.EventType;
import net.site40.rodit.tinyrpg.game.item.Item;
import net.site40.rodit.tinyrpg.game.render.BitmapRenderer;
import net.site40.rodit.tinyrpg.game.render.ResourceManager;
import net.site40.rodit.tinyrpg.game.render.XmlResourceLoader;
import net.site40.rodit.util.TinyInputStream;
import net.site40.rodit.util.TinyOutputStream;
import net.site40.rodit.util.Util;

public class MapState extends GameObject {

	public static final int LOAD_STATE_SAVE = 1;

	private int loadState;

	private RPGMap map;
	private BitmapRenderer rotObj;
	private ArrayList<Entity> entities;

	private boolean spawnedEntities = false;

	public MapState(RPGMap map){
		this.map = map;
		if(map != null && map.hasRot())
			this.rotObj = genRotObject();
		this.entities = new ArrayList<Entity>();
	}

	public RPGMap getMap(){
		return map;
	}

	public void setMap(RPGMap map){
		this.map = map;
	}

	public BitmapRenderer getRotObj(){
		return rotObj;
	}

	public ArrayList<Entity> getEntities(){
		return entities;
	}

	public Entity getEntityByName(String name){
		for(Entity e : entities)
			if(e.getName().equals(name))
				return e;
		return null;
	}

	public void spawn(Game game, Entity e){
		spawn(game, e, true);
	}

	public void spawn(Game game, Entity e, boolean triggerEvent){
		if(this.getEntityByName(e.getName()) != null)
			Log.w("EntitySpawn", "Entity with name " + e.getName() + " already spawned.");

		if(!entities.contains(e))
			entities.add(e);
		game.addObject(e);
		if(triggerEvent){
			e.onSpawn(game);
			game.getEvents().onEvent(game, EventType.ENTITY_SPAWNED, e);
		}
		Log.i("MapState", "Spawned entity " + e.getName() + ".");
	}

	public void despawn(Game game, Entity e){
		e.onDespawn(game);
		entities.remove(e);
		game.removeObject(e);
		Log.i("MapState", "Despawned entity " + e.getName() + ".");
		game.getEvents().onEvent(game, EventType.ENTITY_DESPAWNED, e);
	}

	public ArrayList<Entity> getCollidingEntities(RectF bounds, Object... exclude){
		ArrayList<Entity> collisions = new ArrayList<Entity>();
		for(Entity e : entities)
			if(!e.isNoclip() && !Util.arrayContains(exclude, e, Object.class) && RectF.intersects(bounds, e.getCollisionBounds()))
				collisions.add(e);
		return collisions;
	}

	public Object getCollisionObject(float x, float y, Object... exclude){
		return getCollisionObjectD(x, y, false, exclude);
	}

	public Object getCollisionObjectD(float x, float y, boolean trace, Object... exclude){
		if(map == null)
			return null;
		ArrayList<Object> excList = new ArrayList<Object>();
		for(int i = 0; i < exclude.length; i++)
			excList.add(exclude[i]);
		for(Entity e : entities)
			if((trace ? e.getTraceBounds() : e.getCollisionBounds()).contains(x, y) && !excList.contains(e))
				return e;
		for(MapObject obj : map.getObjects("collisions"))
			if(obj.getBounds().contains(x, y) && !excList.contains(obj))
				return obj;
		return null;
	}

	public Object getCollisionObject(RectF r, Object... exclude){
		if(map == null)
			return null;
		ArrayList<Object> excList = new ArrayList<Object>();
		for(int i = 0; i < exclude.length; i++)
			excList.add(exclude[i]);
		for(Entity e : entities)
			if(RectF.intersects(r, e.getCollisionBounds()) && !excList.contains(e))
				return e;
		for(MapObject obj : map.getObjects("collisions"))
			if(RectF.intersects(r, obj.getBounds()) && !excList.contains(obj))
				return obj;
		return null;
	}

	public boolean checkMove(Game game, Entity e, float x, float y){
		if(e.isNoclip())
			return true;
		RectF nBounds = e.getCollisionBounds(x, y);//new RectF(x, y, x + e.getWidth(), y + e.getHeight());
		Object obj = getCollisionObject(nBounds, e);
		if(obj != null && obj instanceof Entity && obj != e){
			Entity ent = (Entity)obj;
			if(ent.isNoclip())
				return true;
			else{
				ent.onCollide(game, e);
				return false;
			}
		}
		if(obj == null || obj == e)
			return true;
		return false;
	}
	
	private BitmapRenderer genRotObject(){
		return new BitmapRenderer(map.getRenderOnTop()){
			@Override
			public RenderLayer getRenderLayer(){
				return RenderLayer.TOP_OVERRIDE_PLAYER;
			}
		};
	}

	@Override
	public void update(Game game){
		if(map != null && !map.isLoaded())
			map = XmlResourceLoader.loadMap(game.getResources(), map.getFile());
		if(map == null)
			return;
		if(!spawnedEntities){
			if(loadState == LOAD_STATE_SAVE){
				if(map.hasRot()){
					this.rotObj = genRotObject();
					game.addObject(rotObj);;
				}
				for(Entity e : entities){
					spawn(game, e, e.getScript().endsWith("spawn_conditional.js"));
					if(e instanceof EntityNPC)
						((EntityNPC)e).createNametag(game);
				}
				spawnedEntities = true;
				loadState = 0;
				return;
			}
			for(MapObject obj : map.getObjects("entities")){
				int type = obj.getInt("type");
				String name = obj.getString("name");
				boolean noclip = obj.getBool("noclip");
				String inventory = obj.getString("inventory");
				long money = obj.getLong("money");
				String script = obj.getString("script");
				Entity spawn = null;
				switch(type){
				default:
				case Entity.ENTITY_DEFAULT:
					spawn = new Entity();
					break;
				case Entity.ENTITY_LIVING:
					spawn = new EntityLiving();
					break;
				case Entity.ENTITY_PLAYER:
					spawn = new EntityPlayer();
					break;
				case Entity.ENTITY_NPC:
					spawn = new EntityNPC();
					break;
				}
				spawn.setX(obj.getX());
				spawn.setY(obj.getY());
				spawn.setWidth(obj.getWidth());
				spawn.setHeight(obj.getHeight());
				spawn.setName(name);
				spawn.setMoney(money);
				spawn.setNoclip(noclip);
				spawn.setScript(script);
				for(String ikvp : inventory.split(Pattern.quote(";"))){
					String[] parts = ikvp.split(Pattern.quote(","));
					if(parts.length != 2)
						continue;
					String iName = parts[0];
					int count = Integer.valueOf(parts[1]);
					spawn.getInventory().getStack(Item.get(iName)).setAmount(count);
				}
				spawn.getRuntimeProperties().putAll(obj.getProperties());
				spawn(game, spawn);
			}
			spawnedEntities = true;
		}
	}

	@Override
	public void draw(Game game, Canvas canvas){
		if(map == null || map.getBackground() == null || map.getBackground().isRecycled())
			return;

		if(rotObj == null && map.hasRot()){
			this.rotObj = genRotObject();
			game.addObject(rotObj);
		}

		super.preRender(game, canvas);

		canvas.drawBitmap(map.getBackground(), 0, 0, null);

		super.postRender(game, canvas);
	}

	@Override
	public RenderLayer getRenderLayer(){
		return RenderLayer.BOTTOM;
	}

	@Override
	public boolean shouldScale(){
		return true;
	}

	public void dispose(ResourceManager resources){
		if(map != null){
			map.dispose(resources);
			resources.release(map);
			map = null;
		}
	}
	
	public void load(Game game, TinyInputStream in)throws IOException{
		map = new RPGMap(in.readString(), false);
		int entCount = in.readInt();
		for(int i = 0; i < entCount; i++){
			String clsName = in.readString();
			try{
				Class<? extends Entity> cls = (Class<? extends Entity>)Class.forName(clsName);
				Entity e = cls.newInstance();
				e.load(game, in);
				entities.add(e);
			}catch(Exception e){
				Log.e("MapState/Load", "Exception while initializing entity:");
				e.printStackTrace();
			}
		}
		loadState = LOAD_STATE_SAVE;
	}

	public void save(Game game, TinyOutputStream out)throws IOException{
		//synchronized(entities){
			ArrayList<Entity> entCopy = new ArrayList<Entity>(entities);
			out.writeString(map.getFile());
			int entCount = entCopy.size();
			if(entCopy.contains(game.getPlayer()))
				entCount--;
			out.write(entCount);
			for(Entity e : entCopy){
				if(e == game.getPlayer())
					continue;
				out.writeString(e.getClass().getCanonicalName());
				e.save(out);
			}
		//}
	}
}
