package net.site40.rodit.tinyrpg.game.render;

import java.util.ArrayList;

import net.site40.rodit.tinyrpg.game.Game;
import net.site40.rodit.tinyrpg.game.combat.Attack;
import net.site40.rodit.tinyrpg.game.effect.Effect;
import net.site40.rodit.tinyrpg.game.forge.ForgeRegistry;
import net.site40.rodit.tinyrpg.game.forge.ForgeRegistry.ForgeRecipy.ForgeType;
import net.site40.rodit.tinyrpg.game.item.Item;
import net.site40.rodit.tinyrpg.game.item.ItemStack;
import net.site40.rodit.tinyrpg.game.map.MapLoader;
import net.site40.rodit.tinyrpg.game.map.MapObject;
import net.site40.rodit.tinyrpg.game.map.MobSpawnRegistry.MobSpawn;
import net.site40.rodit.tinyrpg.game.map.RPGMap;
import net.site40.rodit.tinyrpg.game.quest.Quest;
import net.site40.rodit.tinyrpg.game.quest.QuestManager;
import net.site40.rodit.tinyrpg.game.start.StartClass;
import net.site40.rodit.util.Util;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.graphics.Bitmap;
import android.util.Log;
import davidiserovich.TMXLoader.TMXLoader;
import davidiserovich.TMXLoader.TileMapData;

public class XmlResourceLoader {
	
	public static int loadCount = 0;
	
	public static boolean useBinaryMap = true;

	public static RPGMap loadMap(ResourceManager resources, String file){
		if(useBinaryMap)
			return MapLoader.loadMap(resources, file);
		RPGMap map = new RPGMap(file, true);
		TileMapData data = TMXLoader.readTMX(file, resources);
		Bitmap[] bmps = TMXLoader.createBitmap(data, resources, 0, data.layers.size());
		map.setBackground(bmps[0]);
		map.setRenderOnTop(bmps[1]);
		for(TileMapData.TMXObject obj : data.objects){
			if(obj.objectGroup == null)
				continue;
			MapObject mob = new MapObject(obj.objectGroup, obj.x, obj.y, obj.width, obj.height);
			mob.setString("name", obj.name);
			for(String key : obj.properties.keySet())
				mob.setString(key, obj.properties.get(key));
			map.getObjects().add(mob);
		}
		resources.putObject(file, map);
		bmps = null;
		data = null;
		System.gc();
		return map;
	}

	public static final String ITEM_PACKAGE = "net.site40.rodit.tinyrpg.game.item";
	public static void loadItems(ResourceManager resources, String file){
		Document doc = resources.readDocument(file);
		NodeList inodes = doc.getElementsByTagName("item");
		int loaded = 0;
		for(int i = 0; i < inodes.getLength(); i++){
			Node n = inodes.item(i);
			if(n.getNodeType() != Element.ELEMENT_NODE)
				continue;
			Element e = (Element)n;
			String type = e.getAttribute("type");
			if(!type.startsWith(ITEM_PACKAGE))
				type = ITEM_PACKAGE + "." + type;
			Class<?> cls = null;
			Item instance = null;
			try{
				cls = Class.forName(type);
				instance = (Item)cls.newInstance();
			}catch(Exception ex){
				ex.printStackTrace();
			}
			if(cls == null || instance == null){
				Log.e("ItemLoader", "Could not find item class with name " + type + ".");
				continue;
			}
			instance.deserializeXmlElement(e);
			Item.register(instance);
			loaded++;
		}
		
		loadCount++;
		
		Log.i("ItemLoader", "Loaded " + loaded + " items from " + file + ".");
	}
	
	public static final String ATTACK_PACKAGE = "net.site40.rodit.tinyrpg.game.combat";
	public static void loadAttacks(ResourceManager resources, String file){
		Document doc = resources.readDocument(file);
		NodeList inodes = doc.getElementsByTagName("attack");
		int loaded = 0;
		for(int i = 0; i < inodes.getLength(); i++){
			Node n = inodes.item(i);
			if(n.getNodeType() != Element.ELEMENT_NODE)
				continue;
			Element e = (Element)n;
			String type = e.getAttribute("type");
			if(!type.startsWith(ATTACK_PACKAGE))
				type = ATTACK_PACKAGE + "." + type;
			Class<?> cls = null;
			Attack instance = null;
			try{
				cls = Class.forName(type);
				instance = (Attack)cls.newInstance();
			}catch(Exception ex){
				ex.printStackTrace();
			}
			if(cls == null || instance == null){
				Log.e("AttackLoader", "Could not find attack class with name " + type + ".");
				continue;
			}
			instance.deserializeXmlElement(e);
			Attack.register(instance);
			loaded++;
		}
		
		loadCount++;
		
		Log.i("AttackLoader", "Loaded " + loaded + " attacks from " + file + ".");
	}

	public static final String EFFECT_PACKAGE = "net.site40.rodit.tinyrpg.game.effect";
	public static void loadEffects(ResourceManager resources, String file){
		Document doc = resources.readDocument(file);
		NodeList nodes = doc.getElementsByTagName("effect");
		int loaded = 0;
		for(int i = 0; i < nodes.getLength(); i++){
			Node n = nodes.item(i);
			if(n.getNodeType() != Node.ELEMENT_NODE)
				continue;
			Element element = (Element)n;
			String type = element.getAttribute("class");
			if(!type.startsWith(EFFECT_PACKAGE))
				type = EFFECT_PACKAGE + "." + type;
			Class<?> cls = null;
			Effect instance = null;
			try{
				cls = Class.forName(type);
				instance = (Effect)cls.newInstance();
			}catch(Exception ex){
				ex.printStackTrace();
			}
			if(instance == null){
				Log.e("EffectLoader", "Could not find effect class with name " + type + ".");
				continue;
			}
			instance.deserializeXmlElement(element);
			Effect.register(instance);
			loaded++;
		}
		
		loadCount++;
		
		Log.i("EffectLoader", "Loaded " + loaded + " effects from " + file + ".");
	}
	
	public static void loadQuests(QuestManager quests, ResourceManager resources, String file){
		Document doc = resources.readDocument(file);
		NodeList qNodes = doc.getElementsByTagName("quest");
		int loaded = 0;
		for(int i = 0; i < qNodes.getLength(); i++){
			Node n = qNodes.item(i);
			if(n.getNodeType() != Node.ELEMENT_NODE)
				continue;
			Element e = (Element)n;
			Quest quest = new Quest();
			quest.deserializeXmlElement(e);
			quests.addQuest(quest);
			loaded++;
		}
		
		loadCount++;
		
		Log.i("QuestLoader", "Loaded " + loaded + " quests from " + file + ".");
	}
	
	public static void loadForge(ForgeRegistry forge, ResourceManager resources, String file){
		Document doc = resources.readDocument(file);
		NodeList rNodes = doc.getElementsByTagName("recipe");
		int loaded = 0;
		for(int i = 0; i < rNodes.getLength(); i++){
			Node n = rNodes.item(i);
			if(n.getNodeType() != Node.ELEMENT_NODE)
				continue;
			Element e = (Element)n;
			String id = e.getAttribute("id");
			long cost = Util.tryGetLong(e.getAttribute("cost"));
			float minForge = Util.tryGetFloat(e.getAttribute("minForge"));
			ForgeType type = Util.tryGetForgeType(e.getAttribute("type"));
			
			ArrayList<ItemStack> inputs = new ArrayList<ItemStack>();
			NodeList inputNodes = e.getElementsByTagName("input");
			for(int x = 0; x < inputNodes.getLength(); x++){
				Node iNode = inputNodes.item(x);
				if(iNode.getNodeType() != Node.ELEMENT_NODE)
					continue;
				Element ie = (Element)iNode;
				Item item = Item.get(ie.getAttribute("name"));
				int amount = Util.tryGetInt(ie.getAttribute("amount"));
				inputs.add(new ItemStack(item, amount));
			}
			
			ArrayList<ItemStack> outputs = new ArrayList<ItemStack>();
			NodeList outputNodes = e.getElementsByTagName("output");
			for(int x = 0; x < outputNodes.getLength(); x++){
				Node oNode = outputNodes.item(x);
				if(oNode.getNodeType() != Node.ELEMENT_NODE)
					continue;
				Element oe = (Element)oNode;
				Item item = Item.get(oe.getAttribute("name"));
				int amount = Util.tryGetInt(oe.getAttribute("amount"));
				outputs.add(new ItemStack(item, amount));
			}
			
			ItemStack[] inputArray = new ItemStack[inputs.size()];
			ItemStack[] outputArray = new ItemStack[outputs.size()];
			for(int j = 0; j < inputs.size() || j < outputs.size(); j++){
				if(j < inputs.size())
					inputArray[j] = inputs.get(j);
				if(j < outputs.size())
					outputArray[j] = outputs.get(j);
			}
			
			forge.register(id, inputArray, outputArray, cost, minForge, type);
			
			loaded++;
		}
				
		Log.i("ForgeLoader", "Loaded " + loaded + " forge recipes from " + file + ".");
	}
	
	public static void loadStartClasses(ResourceManager resources, String file){
		Document doc = resources.readDocument(file);
		NodeList sNodes = doc.getElementsByTagName("class");
		int loaded = 0;
		for(int i = 0; i < sNodes.getLength(); i++){
			Node n = sNodes.item(i);
			if(n.getNodeType() != Node.ELEMENT_NODE)
				continue;
			Element e = (Element)n;
			StartClass start = new StartClass();
			start.deserializeXmlElement(e);
			StartClass.register(start);
			loaded++;
		}
		Log.i("StartClassLoader", "Loaded " + loaded + " started classes from " + file + ".");
	}
	
	public static void loadMobSpawns(Game game, String file){
		Document doc = game.getResources().readDocument(file);
		NodeList msNodes = doc.getElementsByTagName("mobspawn");
		int loaded = 0;
		for(int i = 0; i < msNodes.getLength(); i++){
			Node n = msNodes.item(i);
			if(n.getNodeType() != Node.ELEMENT_NODE)
				continue;
			Element e = (Element)n;
			MobSpawn spawn = new MobSpawn();
			spawn.deserializeXmlElement(e);
			game.getMobSpawns().registerMobSpawn(spawn.getSpawnAreaKey(), spawn);
			loaded++;
		}
	}
}
