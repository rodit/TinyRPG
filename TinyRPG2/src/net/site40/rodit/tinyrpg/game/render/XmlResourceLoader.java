package net.site40.rodit.tinyrpg.game.render;

import net.site40.rodit.tinyrpg.game.combat.Attack;
import net.site40.rodit.tinyrpg.game.item.Item;
import net.site40.rodit.tinyrpg.game.map.MapObject;
import net.site40.rodit.tinyrpg.game.map.RPGMap;
import net.site40.rodit.tinyrpg.game.quest.Quest;
import net.site40.rodit.tinyrpg.game.quest.QuestManager;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.graphics.Bitmap;
import android.util.Log;
import davidiserovich.TMXLoader.TMXLoader;
import davidiserovich.TMXLoader.TileMapData;

public class XmlResourceLoader {

	public static RPGMap loadMap(ResourceManager resources, String file){
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

	private static final String ITEM_PACKAGE = "net.site40.rodit.tinyrpg.game.item";
	public static void loadItems(ResourceManager resources, String file){
		Document doc = resources.readDocument(file);
		NodeList inodes = doc.getElementsByTagName("item");
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
		}
	}
	
	private static final String ATTACK_PACKAGE = "net.site40.rodit.tinyrpg.game.combat";
	public static void loadAttacks(ResourceManager resources, String file){
		Document doc = resources.readDocument(file);
		NodeList inodes = doc.getElementsByTagName("attack");
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
				Log.e("ItemLoader", "Could not find item class with name " + type + ".");
				continue;
			}
			instance.deserializeXmlElement(e);
			Attack.register(instance);
		}
	}
	
	public static void loadQuests(QuestManager quests, ResourceManager resources, String file){
		Document doc = resources.readDocument(file);
		NodeList qNodes = doc.getElementsByTagName("quest");
		for(int i = 0; i < qNodes.getLength(); i++){
			Node n = qNodes.item(i);
			if(n.getNodeType() != Node.ELEMENT_NODE)
				continue;
			Element e = (Element)n;
			Quest quest = new Quest();
			quest.deserializeXmlElement(e);
			quests.addQuest(quest);
		}
	}
}
