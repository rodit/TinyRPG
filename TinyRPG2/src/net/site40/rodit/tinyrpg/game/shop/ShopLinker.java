package net.site40.rodit.tinyrpg.game.shop;

import java.util.HashMap;

import net.site40.rodit.tinyrpg.game.Game;
import net.site40.rodit.tinyrpg.game.entity.EntityLiving;
import net.site40.rodit.tinyrpg.game.gui.windows.WindowShop;
import net.site40.rodit.tinyrpg.game.item.Inventory;

public class ShopLinker {
	
	private static HashMap<EntityLiving, ShopLinker> linkers = new HashMap<EntityLiving, ShopLinker>();

	public static ShopLinker getLinker(EntityLiving owner){
		return linkers.get(owner);
	}
	
	public static void registerLinker(EntityLiving owner, ShopLinker linker){
		linkers.put(owner, linker);
	}
	
	private Shop shop;
	private EntityLiving owner;
	private long money;
	private Inventory currentStock;
	
	private EntityLiving stockOwner;
	
	public ShopLinker(Shop shop, EntityLiving owner){
		this.shop = shop;
		this.owner = owner;
		this.currentStock = new Inventory(shop.getStock());
		this.stockOwner = new EntityLiving();
		stockOwner.setInventory(currentStock);
	}
	
	public Shop getShop(){
		return shop;
	}
	
	public EntityLiving getLinkedOwner(){
		return owner;
	}
	
	public EntityLiving getStockOwner(){
		return stockOwner;
	}
	
	public Inventory getStock(){
		return currentStock;
	}
	
	public long getMoney(){
		return money;
	}
	
	public void setMoney(long money){
		this.money = money;
	}
	
	public void addMoney(long money){
		this.money += money;
	}
	
	public void subMoney(long money){
		this.money -= money;
	}
	
	public void restock(){
		money = Math.min(money, shop.getMoney());
		currentStock = new Inventory(shop.getStock());
	}
	
	public void open(Game game){
		WindowShop shopWindow = new WindowShop(game, this);
		shopWindow.show();
		game.getWindows().register(shopWindow);
	}
}
