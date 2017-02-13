package net.site40.rodit.tinyrpg.game.start;

import java.util.ArrayList;

import net.site40.rodit.tinyrpg.game.entity.EntityPlayer;
import net.site40.rodit.tinyrpg.game.entity.EntityStats;
import net.site40.rodit.tinyrpg.game.item.Inventory;
import net.site40.rodit.tinyrpg.game.item.Item;
import net.site40.rodit.tinyrpg.game.item.ItemEquippable;
import net.site40.rodit.util.Util;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.util.Log;

public class StartClass {

	private static ArrayList<StartClass> classes = new ArrayList<StartClass>();

	public static void register(StartClass startClass){
		if(!classes.contains(startClass))
			classes.add(startClass);
	}

	public void deregister(StartClass startClass){
		classes.remove(startClass);
	}

	public static ArrayList<StartClass> getClasses(){
		return classes;
	}

	private String name;
	private String showName;
	private String description;
	private String abilityDescription;
	private Item[] startGearEquip;
	private Inventory startGearInv;
	private EntityStats stats;

	public StartClass(){
		this.name = "start_null";
		this.showName = "Null Start Class";
		this.description = "";
		this.abilityDescription = "None";
		this.startGearEquip = new Item[10];
		this.startGearInv = new Inventory();
		this.stats = new EntityStats();
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

	public String getDescritpion(){
		return description;
	}

	public void setDecription(String description){
		this.description = description;
	}

	public String getAbilityDescription(){
		return abilityDescription;
	}

	public void setAbilityDescription(String abilityDescription){
		this.abilityDescription = abilityDescription;
	}

	public Item[] getStartGearEquip(){
		return startGearEquip;
	}

	public void setStartGearEquip(Item[] startGearEquip){
		this.startGearEquip = startGearEquip;
	}

	public Inventory getStartGearInv(){
		return startGearInv;
	}

	public void setStartGear(Inventory startGearInv){
		this.startGearInv = startGearInv;
	}

	public EntityStats getStats(){
		return stats;
	}

	public void setStats(EntityStats stats){
		this.stats = stats;
	}
	
	public void apply(EntityPlayer player){
		player.setInventory(new Inventory(startGearInv));
		for(int i = 0; i < ItemEquippable.SLOT_HAIR; i++){
			if(startGearEquip[i] != null)
				player.getInventory().add(startGearEquip[i]);
			player.setEquipped(i, player.getInventory().getExistingStack(startGearEquip[i]));
		}
		player.setStats(new EntityStats(stats));
		player.setStartClass(name);
	}

	public void deserializeXmlElement(Element e){
		this.name = e.getAttribute("name");
		this.showName = e.getAttribute("showName");
		this.description = e.getAttribute("description");
		this.abilityDescription = e.getAttribute("abilityDescription");
		NodeList nStartGear = e.getElementsByTagName("item");
		for(int i = 0; i < nStartGear.getLength(); i++){
			Node n = nStartGear.item(i);
			if(n.getNodeType() != Node.ELEMENT_NODE)
				continue;
			Element el = (Element)n;
			Item item = Item.get(el.getAttribute("name"));
			int amount = Util.tryGetInt(el.getAttribute("amount"), 1);
			if(item == null){
				Log.w("StartClass", "Start class item does not exist (" + el.getAttribute("name") + "). Not including in startGear.");
				continue;
			}
			boolean equip = Util.tryGetBool(el.getAttribute("equip"), true);
			if(equip && item instanceof ItemEquippable){
				int[] equipSlots = ((ItemEquippable)item).getEquipSlots();
				for(int slotIndex = 0; slotIndex < equipSlots.length; slotIndex++){
					int slot = equipSlots[slotIndex];
					Item cItem = startGearEquip[slot];
					if(cItem == null){
						startGearEquip[slot] = item;
						break;
					}
				}
			}else
				startGearInv.add(item, amount);
		}
		stats.setLevel(Util.tryGetInt(e.getAttribute("level"), 1));
		stats.setSpeed(Util.tryGetFloat(e.getAttribute("speed"), EntityStats.DEFAULT_STAT));
		stats.setStrength(Util.tryGetFloat(e.getAttribute("strength"), EntityStats.DEFAULT_STAT));
		stats.setDefence(Util.tryGetFloat(e.getAttribute("defence"), EntityStats.DEFAULT_STAT));
		stats.setLuck(Util.tryGetFloat(e.getAttribute("luck"), EntityStats.DEFAULT_STAT));
		stats.setMagika(Util.tryGetFloat(e.getAttribute("magika"), EntityStats.DEFAULT_STAT));
		stats.setForge(Util.tryGetFloat(e.getAttribute("forge"), EntityStats.DEFAULT_STAT));
	}
}
