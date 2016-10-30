package net.site40.rodit.tinyrpg.game.item;

import java.util.Collection;
import java.util.HashMap;

import net.site40.rodit.tinyrpg.game.Game;
import net.site40.rodit.tinyrpg.game.entity.Entity;
import net.site40.rodit.tinyrpg.game.event.EventReceiver.EventType;
import net.site40.rodit.util.Util;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.w3c.dom.Element;

import android.text.TextUtils;

public class Item {
	
	private static HashMap<String, Item> instances = new HashMap<String, Item>();
	
	public static Collection<Item> getItems(){
		return instances.values();
	}
	
	public static void register(Item item){
		instances.put(item.getName(), item);
	}
	
	public static void unregister(String name){
		instances.remove(name);
	}
	
	public static Item get(String name){
		return instances.get(name);
	}
	
	public static enum Rarity{
		VERY_COMMON, COMMON, UNCOMMON, RARE, VERY_RARE, ULTRA_RARE, UNKNOWN;
		
		@Override
		public String toString(){
			switch(this){
			case VERY_COMMON:
				return "Very Common";
			case COMMON:
				return "Common";
			case UNCOMMON:
				return "Uncommon";
			case RARE:
				return "Rare";
			case VERY_RARE:
				return "Very Rare";
			case ULTRA_RARE:
				return "Ultra Rare";
			case UNKNOWN:
			default:
				return "Unknown";
			}
		}
		
		public static Rarity fromString(String string){
			String compare = string.toLowerCase().trim();
			if(compare.contains("very") && compare.contains("common"))
				return VERY_COMMON;
			else if(compare.contains("uncommon"))
				return UNCOMMON;
			else if(compare.contains("common"))
				return COMMON;
			else if(compare.contains("ultra") && compare.contains("rare"))
				return ULTRA_RARE;
			else if(compare.contains("very") && compare.contains("rare"))
				return VERY_RARE;
			else if(compare.contains("rare"))
				return RARE;
			return UNKNOWN;
		}
	}

	protected String name;
	protected String showName;
	protected String description;
	protected String script;
	protected String resource;
	protected Rarity rarity;
	protected long value;
	protected boolean stackable;
	protected int stackSize;
	private int level;
	
	private Function jsOnEquip;
	private Function jsOnUnEquip;

	public Item(){
		this("", "", "", "", "", Rarity.VERY_COMMON, 0L);
	}

	public Item(String name, String showName, String description, String script, String resource, Rarity rarity, long value){
		this.name = name;
		this.showName = showName;
		this.description = description;
		this.script = script;
		this.resource = resource;
		this.rarity = rarity;
		this.value = value;
		this.stackable = true;
		this.stackSize = 99;
		this.level = 1;
	}

	public String getName(){
		return name;
	}

	public void setName(String name){
		this.name = name;
	}

	public String getShowName(){
		return showName;
	}

	public void setShowName(String showName){
		this.showName = showName;
	}

	public String getDescription(){
		return description;
	}

	public void setDescription(String description){
		this.description = description;
	}

	public String getScript(){
		return script;
	}
	
	public void setScript(String script){
		this.script = script;
	}

	public String getResource(){
		return resource;
	}
	
	public void setResource(String resource){
		this.resource = resource;
	}
	
	public Rarity getRarity(){
		return rarity;
	}

	public void setRarity(Rarity rarity){
		this.rarity = rarity;
	}

	public long getValue(){
		return value;
	}

	public void setValue(long value){
		this.value = value;
	}
	
	public int getLevel(){
		return level;
	}
	
	public void setLevel(int level){
		this.level = level;
	}
	
	public boolean canUse(){
		return true;
	}
	
	public boolean isConsumed(){
		return true;
	}
	
	public boolean isStackable(){
		return stackable;
	}
	
	public void setStackable(boolean stackable){
		this.stackable = stackable;
	}
	
	public int getStackSize(){
		return stackSize;
	}
	
	public void setStackSize(int stackSize){
		this.stackSize = stackSize;
	}
	
	public void registerCallbacks(Object onEquip, Object onUnEquip){
		this.jsOnEquip = (Function)Context.jsToJava(onEquip, Function.class);
		this.jsOnUnEquip = (Function)Context.jsToJava(onUnEquip, Function.class);
	}
	
	public void initCallbacks(Game game){
		if(!TextUtils.isEmpty(script) && jsOnEquip == null && jsOnUnEquip == null)
			game.getScripts().execute(game, script, new String[] { "self" }, new Object[] { this });
	}

	public void onEquip(Game game, Entity ent){
		initCallbacks(game);
		if(jsOnEquip != null)
			game.getScripts().executeFunction(game, jsOnEquip, this, new String[0], new Object[0], new Object[] { ent });
		game.getEvents().onEvent(game, EventType.ITEM_EQUIP, this, ent);
	}
	
	public void onUnEquip(Game game, Entity ent){
		initCallbacks(game);
		if(jsOnUnEquip != null)
			game.getScripts().executeFunction(game, jsOnUnEquip, this, new String[0], new Object[0], new Object[] { ent });
		game.getEvents().onEvent(game, EventType.ITEM_UNEQUIP, this, ent);
	}
	
	public void deserializeXmlElement(Element e){
		setName(e.getAttribute("name"));
		setShowName(e.getAttribute("showName"));
		setDescription(e.getAttribute("description"));
		setResource(e.getAttribute("resource"));
		setScript(e.getAttribute("script"));
		setRarity(Rarity.fromString(e.getAttribute("rarity")));
		setValue(Util.tryGetLong(e.getAttribute("value")));
		setStackable(Util.tryGetBool(e.getAttribute("stackable"), true));
		setStackSize(Util.tryGetInt(e.getAttribute("stackSize"), 99));
		String lvlAttrib = e.getAttribute("level");
		if(!TextUtils.isEmpty(lvlAttrib))
			setLevel(Integer.valueOf(lvlAttrib));
	}
}
