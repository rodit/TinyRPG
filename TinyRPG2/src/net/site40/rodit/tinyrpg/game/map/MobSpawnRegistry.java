package net.site40.rodit.tinyrpg.game.map;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import net.site40.rodit.tinyrpg.game.Game;
import net.site40.rodit.tinyrpg.game.battle.Battle;
import net.site40.rodit.tinyrpg.game.battle.Team;
import net.site40.rodit.tinyrpg.game.entity.EntityLiving;
import net.site40.rodit.util.Util;

import org.w3c.dom.Element;

public class MobSpawnRegistry{

	private static Comparator<MobSpawn> spawnComparator = new Comparator<MobSpawn>(){
		public int compare(MobSpawn spawn0, MobSpawn spawn1){
			return (int)(spawn0.encounterChance - spawn1.encounterChance);
		}
	};

	private HashMap<String, ArrayList<MobSpawn>> mobSpawns;

	public MobSpawnRegistry(){
		this.mobSpawns = new HashMap<String, ArrayList<MobSpawn>>();
	}

	public ArrayList<MobSpawn> getMobSpawns(String key){
		ArrayList<MobSpawn> spawns = mobSpawns.get(key);
		if(spawns == null)
			mobSpawns.put(key, spawns = new ArrayList<MobSpawn>());
		return spawns;
	}

	public void registerMobSpawn(String key, MobSpawn spawn){
		ArrayList<MobSpawn> spawns = getMobSpawns(key);
		spawns.add(spawn);
		Collections.sort(spawns, spawnComparator);
	}

	public static class MobSpawn{

		public static final float ENCOUNTER_CHANCE_MULTI = 0.01f;

		private String spawnAreaKey;
		private float encounterChance;
		private int minSpawnCount;
		private int maxSpawnCount;
		private String spawnConfig;

		public MobSpawn(){
			this(0f, 0, 0, "", "");
		}

		public MobSpawn(float encounterChance, int minSpawnCount, int maxSpawnCount, String spawnConfig, String spawnAreaKey){
			this.encounterChance = encounterChance;
			this.minSpawnCount = minSpawnCount;
			this.maxSpawnCount = maxSpawnCount;
			this.spawnConfig = spawnConfig;
			this.spawnAreaKey = spawnAreaKey;
		}

		public float getEncounterChance(){
			return encounterChance;
		}

		public int getMinSpawnCount(){
			return minSpawnCount;
		}

		public int getMaxSpawnCount(){
			return maxSpawnCount;
		}

		public String getSpawnConfig(){
			return spawnConfig;
		}

		public String getSpawnAreaKey(){
			return spawnAreaKey;
		}
		
		public boolean shouldEncounterFrame(Game game){
			float endChance = encounterChance * ENCOUNTER_CHANCE_MULTI;
			if(game.getRandom().should(endChance))
				return true;
			return false;
		}

		public Battle encounter(Game game, Team defence){
			EntityLiving member = defence.getMembers().get(0);
			return game.getHelper().battle(game.getMap().getMap().getRegion(member.getX(), member.getY()), genAttack(game), defence);
		}

		protected Team genAttack(Game game){
			Team team = new Team();
			int count = game.getRandom().nextInt(minSpawnCount, maxSpawnCount);
			for(int i = 0; i < count; i++){
				EntityLiving living = (EntityLiving)game.getHelper().createEntity(spawnConfig);
				team.add(living);
			}
			return team;
		}

		public void deserializeXmlElement(Element e){
			this.encounterChance = Util.tryGetFloat(e.getAttribute("encounterChance"), encounterChance);
			this.minSpawnCount = Util.tryGetInt(e.getAttribute("minSpawnCount"), minSpawnCount);
			this.maxSpawnCount = Util.tryGetInt(e.getAttribute("maxSpawnCount"), maxSpawnCount);
			this.spawnConfig = e.getAttribute("spawnConfig");
			this.spawnAreaKey = e.getAttribute("spawnAreaKey");
		}
	}
}
