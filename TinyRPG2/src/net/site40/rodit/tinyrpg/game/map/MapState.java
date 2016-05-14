package net.site40.rodit.tinyrpg.game.map;

import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Pattern;

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
import net.site40.rodit.util.ISavable;
import net.site40.rodit.util.TinyInputStream;
import net.site40.rodit.util.TinyOutputStream;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.util.Log;

public class MapState extends GameObject implements ISavable{
	
	public static final int LOAD_STATE_SAVE = 1;
	
	public static int MAP_LOAD_STATE = 0;

	private RPGMap map;
	private BitmapRenderer rotObj;
	private ArrayList<Entity> entities;

	private boolean spawnedEntities = false;

	public MapState(RPGMap map){
		this.map = map;
		if(map != null){
			this.rotObj = new BitmapRenderer(map.getRenderOnTop()){
				@Override
				public RenderLayer getRenderLayer(){
					return RenderLayer.TOP_OVERRIDE_PLAYER;
				}
			};
		}
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

	public Object getCollisionObject(float x, float y, Object... exclude){
		if(map == null)
			return null;
		ArrayList<Object> excList = new ArrayList<Object>();
		for(int i = 0; i < exclude.length; i++)
			excList.add(exclude[i]);
		for(Entity e : entities)
			if(e.getCollisionBounds().contains(x, y) && !excList.contains(e))
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

	@Override
	public void update(Game game){
		if(map != null && !map.isLoaded())
			map = XmlResourceLoader.loadMap(game.getResources(), map.getFile());
		if(map == null)
			return;
		if(!spawnedEntities){
			if(MAP_LOAD_STATE == LOAD_STATE_SAVE){
				for(Entity e : entities)
					spawn(game, e, false);
				spawnedEntities = true;
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

		if(rotObj == null){
			this.rotObj = new BitmapRenderer(map.getRenderOnTop()){
				@Override
				public RenderLayer getRenderLayer(){
					return RenderLayer.TOP_OVERRIDE_PLAYER;
				}
			};
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

	@Override
	public void serialize(TinyOutputStream out)throws IOException{
		out.writeString(map.getFile());
		out.write(entities.size());
		for(Entity e : entities)
			e.serialize(out);
	}

	@Override
	public void deserialize(TinyInputStream in)throws IOException{
		String mapFile = in.readString();
		map = new RPGMap(mapFile, false);
		int eLen = in.readInt();
		int read = 0;
		while(read < eLen){
			int type = in.readInt();
			Entity add = null;
			switch(type){
			case Entity.ENTITY_DEFAULT:
				add = new Entity();
				break;
			case Entity.ENTITY_LIVING:
				add = new EntityLiving();
				break;
			case Entity.ENTITY_PLAYER:
				add = new EntityPlayer();
				break;
			case Entity.ENTITY_NPC:
				add = new EntityNPC();
				break;
			default:
				add = new Entity();
				break;
			}
			add.deserialize(in);
			entities.add(add);
			read++;
		}
	}

	public void dispose(ResourceManager resources){
		if(map != null){
			map.dispose(resources);
			resources.release(map);
			map = null;
		}
	}
}
