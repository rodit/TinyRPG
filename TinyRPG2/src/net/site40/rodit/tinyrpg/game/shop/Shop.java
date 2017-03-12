package net.site40.rodit.tinyrpg.game.shop;

import java.util.ArrayList;

import net.site40.rodit.tinyrpg.game.item.Inventory;
import net.site40.rodit.tinyrpg.game.item.Item;
import net.site40.rodit.util.Util;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class Shop {
	
	private static ArrayList<Shop> shops = new ArrayList<Shop>();
	
	public static void register(Shop shop){
		if(!shops.contains(shop))
			shops.add(shop);
	}
	
	public static void unregister(Shop shop){
		shops.remove(shop);
	}
	
	public static Shop get(String shopName){
		for(Shop shop : shops)
			if(shop.name.equals(shopName))
				return shop;
		return null;
	}
	
	private String name;
	private Inventory stock;
	private long money;
	
	public Shop(){
		this("");
	}
	
	public Shop(String name){
		this(name, new Inventory());
	}
	
	public Shop(String name, Inventory stock){
		this.name = name;
		this.stock = stock;
	}
	
	public String getName(){
		return name;
	}

	public Inventory getStock(){
		return stock;
	}
	
	public long getMoney(){
		return money;
	}
	
	public void deserializeXmlElement(Element e){
		this.name = e.getAttribute("name");
		this.money = Util.tryGetLong(e.getAttribute("money"));
		if(stock == null)
			this.stock = new Inventory();
		NodeList itemNodes = e.getElementsByTagName("item");
		for(int i = 0; i < itemNodes.getLength(); i++){
			Node n = itemNodes.item(i);
			if(n.getNodeType() != Node.ELEMENT_NODE)
				continue;
			Element itemEl = (Element)n;
			String itemName = itemEl.getAttribute("name");
			int count = Util.tryGetInt(itemEl.getAttribute("count"), 1);
			stock.add(Item.get(itemName), count);
		}
	}
}
