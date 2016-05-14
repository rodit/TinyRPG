package net.site40.rodit.tinyrpg.game.shop;

import java.util.ArrayList;

import net.site40.rodit.tinyrpg.game.Game;
import net.site40.rodit.tinyrpg.game.SuperCalc;
import net.site40.rodit.tinyrpg.game.entity.Entity;
import net.site40.rodit.tinyrpg.game.entity.EntityLiving;
import net.site40.rodit.tinyrpg.game.entity.npc.TempShopOwner;
import net.site40.rodit.tinyrpg.game.gui.GuiShop;
import net.site40.rodit.tinyrpg.game.item.Inventory;
import net.site40.rodit.tinyrpg.game.item.ItemStack;

public class Shop {
	
	private static ArrayList<Shop> shops = new ArrayList<Shop>();
	
	public static void register(Shop shop){
		if(!shops.contains(shop))
			shops.add(shop);
	}
	
	public static void unregister(Shop shop){
		shops.remove(shop);
	}
	
	public static Shop get(String ownerName){
		for(Shop shop : shops)
			if(shop.owner != null && shop.owner.getName().equals(ownerName))
				return shop;
		return null;
	}
	
	public static Shop get(Entity owner){
		for(Shop shop : shops)
			if(shop.owner == owner)
				return shop;
		return null;
	}

	private Entity owner;
	private Inventory initialInventory;
	private Inventory inventory;
	private float purchaseMultiplier;
	private float sellMultiplier;
	private long initialMoney;

	public Shop(){
		this(new Inventory(), 1f, 1f);
	}

	public Shop(Entity owner){
		this(owner.getInventory(), 1f, 1f);
		this.owner = owner;
		this.initialMoney = owner.getMoney();
	}

	public Shop(Inventory inventory, float purchaseMultiplier, float sellMultiplier){
		this.initialInventory = new Inventory(inventory);
		this.inventory = inventory;
		this.purchaseMultiplier = purchaseMultiplier;
		this.sellMultiplier = sellMultiplier;
	}

	public Entity getOwner(){
		return owner;
	}

	public void setOwner(Entity owner){
		this.owner = owner;
	}

	public Inventory getInventory(){
		return inventory;
	}

	public float getPurchaseMultiplier(){
		return purchaseMultiplier;
	}
	
	public void setPurchaseMultiplier(float purchaseMultiplier){
		this.purchaseMultiplier = purchaseMultiplier;
	}

	public float getSellMultiplier(){
		return sellMultiplier;
	}
	
	public void setSellMultiplier(float sellMultiplier){
		this.sellMultiplier = sellMultiplier;
	}

	public long howMuchToBuy(ItemStack item, EntityLiving buyer){
		double endMulti = 1d - (buyer == null ? 0d : (double)buyer.getStats().getLuck() / 10d);
		return (long)(endMulti * (double)item.getItem().getValue() * (double)purchaseMultiplier * (double)item.getAmount());
	}
	
	public long howMuchToSell(ItemStack item, EntityLiving seller){
		return (long)((double)SuperCalc.getItemValue(item.getItem(), seller) * (double)item.getAmount() * (double)sellMultiplier);
	}
	
	public boolean purchase(ItemStack item, Entity buyer){
		long price = howMuchToBuy(item, buyer instanceof EntityLiving ? (EntityLiving)buyer : null);
		if(buyer.getMoney() < price)
			return false;
		else{
			if(inventory.getItems().contains(item))
				inventory.getItems().remove(item);
			else
				inventory.add(item.getItem(), -item.getAmount());
			buyer.getInventory().add(item);
			return true;
		}
	}
	
	public boolean sell(ItemStack item, Entity seller){
		long price = howMuchToSell(item, seller instanceof EntityLiving ? (EntityLiving)seller : null);
		if(owner != null && owner.getMoney() < price)
			return false;
		else{
			if(owner != null)
				owner.subtractMoney(price);
			if(owner.getInventory().getItems().contains(item))
				owner.getInventory().getItems().remove(item);
			else
				owner.getInventory().add(item.getItem(), -item.getAmount());
			inventory.add(item);
			return true;
		}
	}
	
	public void restock(){
		inventory = new Inventory(initialInventory);
		if(owner != null)
			owner.addMoney(initialMoney);
	}
	
	public void open(Game game){
		game.setGlobal("current_container", new TempShopOwner(this));
		game.setGlobal("current_shop", this);
		game.getGuis().show(GuiShop.class);
	}
}
