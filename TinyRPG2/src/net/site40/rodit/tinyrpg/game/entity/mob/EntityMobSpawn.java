package net.site40.rodit.tinyrpg.game.entity.mob;

import java.io.IOException;
import java.util.ArrayList;

import org.w3c.dom.Document;

import android.graphics.RectF;
import net.site40.rodit.tinyrpg.game.Game;
import net.site40.rodit.tinyrpg.game.battle.Battle;
import net.site40.rodit.tinyrpg.game.battle.Team;
import net.site40.rodit.tinyrpg.game.entity.Entity;
import net.site40.rodit.tinyrpg.game.entity.EntityLiving;
import net.site40.rodit.util.TinyInputStream;
import net.site40.rodit.util.TinyOutputStream;

public class EntityMobSpawn extends Entity{

	private ArrayList<MobSpawnGroup> mobSpawns;
	private long updateInterval;

	private ArrayList<EntityMob> spawnQueue;

	public EntityMobSpawn(){
		super();
		this.mobSpawns = new ArrayList<MobSpawnGroup>();
		setUpdateInterval(500l);
	}

	public long getUpdateInterval(){
		return updateInterval;
	}

	public void setUpdateInterval(long updateInterval){
		ticker.setInterval(this.updateInterval = updateInterval);
	}

	public ArrayList<EntityMob> getSpawn(Game game){
		ArrayList<EntityMob> toSpawn = new ArrayList<EntityMob>();
		for(MobSpawnGroup group : mobSpawns)
			toSpawn.addAll(group.provideSpawn(game));
		return toSpawn;	
	}

	@Override
	public void tick(Game game){
		if(spawnQueue == null)
			spawnQueue = getSpawn(game);
		if(spawnQueue.size() > 0 && RectF.intersects(game.getPlayer().getCollisionBounds(), this.getCollisionBounds()) && game.getPlayer().hasMoved()){
			encounter(game, spawnQueue, new Team(game.getPlayer()));
			spawnQueue = null;
		}
	}
	
	public Battle encounter(Game game, ArrayList<EntityMob> spawn, Team defence){
		EntityLiving member = defence.getMembers().get(0);
		return game.getHelper().battle(game.getMap().getMap().getRegion(member.getX(), member.getY()), new Team(spawn.toArray(new EntityMob[0])), defence);
	}
	
	@Override
	public void linkConfig(Document document){
		
	}

	public void load(TinyInputStream in)throws IOException{
		int count = in.readInt();
		for(int i = 0; i < count; i++){
			MobSpawnGroup group = new MobSpawnGroup();
			group.load(in);
			mobSpawns.add(group);
		}
		setUpdateInterval(in.readLong());
	}

	public void save(TinyOutputStream out)throws IOException{
		out.write(mobSpawns.size());
		for(MobSpawnGroup spawn : mobSpawns)
			spawn.save(out);
		out.write(updateInterval);
	}

	public static class MobSpawnGroup{
		
		private String config;
		private int minSpawn;
		private int maxSpawn;
		private float weight;

		public MobSpawnGroup(){
			this("", 0, 0, 0f);
		}

		public MobSpawnGroup(String config, int minSpawn, int maxSpawn, float weight){
			this.config = config;
			this.minSpawn = minSpawn;
			this.maxSpawn = maxSpawn;
			this.weight = weight;
		}
		
		public void setConfig(String config){
			this.config = config;
		}
		
		public void setMinSpawn(int minSpawn){
			this.minSpawn = minSpawn;
		}
		
		public void setMaxSpawn(int maxSpawn){
			this.maxSpawn = maxSpawn;
		}
		
		public void setWeight(float weight){
			this.weight = weight;
		}
		
		public ArrayList<EntityMob> provideSpawn(Game game){
			ArrayList<EntityMob> spawn = new ArrayList<EntityMob>();
			int count = game.getRandom().nextInt(minSpawn, maxSpawn);
			int weightedCount = count;
			for(int i = 0; i < count; i++)
				if(game.getRandom().should(1f - weight) && weightedCount > minSpawn)
					weightedCount--;
			for(int i = 0; i < weightedCount; i++)
				spawn.add((EntityMob)game.getHelper().createEntity(config));
			return spawn;
		}

		public void load(TinyInputStream in)throws IOException{
			config = in.readString();
			minSpawn = in.readInt();
			maxSpawn = in.readInt();
			weight = in.readFloat();
		}

		public void save(TinyOutputStream out)throws IOException{
			out.writeString(config);
			out.write(minSpawn);
			out.write(maxSpawn);
			out.write(weight);
		}
	}
}
