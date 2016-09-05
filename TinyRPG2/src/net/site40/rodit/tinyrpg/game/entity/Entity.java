package net.site40.rodit.tinyrpg.game.entity;

import java.io.IOException;
import java.util.HashMap;

import net.site40.rodit.tinyrpg.game.Game;
import net.site40.rodit.tinyrpg.game.Ticker;
import net.site40.rodit.tinyrpg.game.chat.IChatSender;
import net.site40.rodit.tinyrpg.game.item.Inventory;
import net.site40.rodit.tinyrpg.game.item.Item;
import net.site40.rodit.tinyrpg.game.render.Sprite;
import net.site40.rodit.tinyrpg.game.shop.Shop;
import net.site40.rodit.util.TinyInputStream;
import net.site40.rodit.util.TinyOutputStream;
import net.site40.rodit.util.Util;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.graphics.RectF;
import android.text.TextUtils;
import android.util.Log;

public class Entity extends Sprite implements IChatSender{
	
	public static final int ENTITY_DEFAULT = 0;
	public static final int ENTITY_LIVING = 1;
	public static final int ENTITY_PLAYER = 2;
	public static final int ENTITY_NPC = 3;

	protected boolean noclip;
	protected long money;
	protected Inventory inventory;
	protected String script;

	protected HashMap<String, String> runtimeProperties;

	protected Function jsOnSpawn;
	protected Function jsOnDespawn;
	protected Function jsOnCollide;
	protected Function jsOnAction;
	protected Ticker ticker;

	public Entity(){
		super();
		this.x = 0f;
		this.y = 0f;
		this.width = 16f;
		this.height = 16f;
		this.noclip = false;
		this.money = 0L;
		this.inventory = new Inventory();
		this.script = "";
		this.runtimeProperties = new HashMap<String, String>();
		this.ticker = new Ticker(Long.MAX_VALUE);
	}
	
	public boolean showName(){
		return false;
	}
	
	public String getDisplayName(){
		return name;
	}

	public boolean isNoclip(){
		return noclip;
	}

	public void setNoclip(boolean noclip){
		this.noclip = noclip;
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

	public void subtractMoney(long money){
		this.money -= money;
		if(this.money < 0L)
			this.money = 0L;
	}

	public Inventory getInventory(){
		return inventory;	
	}

	public void setInventory(Inventory inventory){
		this.inventory = inventory;
	}

	public String getScript(){
		return script;
	}

	public void setScript(String script){
		this.script = script;
	}

	public HashMap<String, String> getRuntimeProperties(){
		return runtimeProperties;
	}

	public String getRuntimeProperty(String key){
		return runtimeProperties.get(key);
	}

	public void setRuntimeProperty(String key, String value){
		runtimeProperties.put(key, value);
	}

	public void registerCallbacks(Object onSpawn, Object onDespawn, Object onCollide, Object onAction){
		this.jsOnSpawn = (Function)Context.jsToJava(onSpawn, Function.class);
		this.jsOnDespawn = (Function)Context.jsToJava(onDespawn, Function.class);
		this.jsOnCollide = (Function)Context.jsToJava(onCollide, Function.class);
		this.jsOnAction = (Function)Context.jsToJava(onAction, Function.class);
	}

	private void initCallbacks(Game game){
		if(!TextUtils.isEmpty(script) && jsOnSpawn == null && jsOnDespawn == null && jsOnCollide == null && jsOnAction == null)
			game.getScripts().execute(game, script, new String[] { "self" }, new Object[] { this });
	}

	public void onSpawn(Game game){
		initCallbacks(game);
		Log.i("SCRIPTENTITY", "script:" + script);
		if(jsOnSpawn != null)
			game.getScripts().executeFunction(game, jsOnSpawn, this, new String[0], new Object[0], new Object[0]);
	}

	public void onDespawn(Game game){
		initCallbacks(game);
		if(jsOnDespawn != null)
			game.getScripts().executeFunction(game, jsOnDespawn, this, new String[0], new Object[0], new Object[0]);
	}
	
	public void onCollide(Game game, Entity collide){
		initCallbacks(game);
		if(jsOnCollide != null)
			game.getScripts().executeFunction(game, jsOnCollide, this, new String[0], new Object[0], new Object[] { collide });
	}
	
	public void onAction(Game game, Entity actor){
		initCallbacks(game);
		if(jsOnAction != null)
			game.getScripts().executeFunction(game, jsOnAction, this, new String[0], new Object[0], new Object[] { actor });
	}

	public RectF getCollisionBounds(){
		return this.getBounds();
	}
		
	public RectF getCollisionBounds(float x, float y){
		RectF collision = getCollisionBounds();
		collision.left = x;
		collision.top = y;
		return collision;
	}

	public void linkConfig(Document document){		
		Element root = (Element)document.getElementsByTagName("entity").item(0);
		this.x = Util.tryGetFloat(root.getAttribute("x"), this.x);
		this.y = Util.tryGetFloat(root.getAttribute("y"), this.y);
		this.width = Util.tryGetFloat(root.getAttribute("width"), this.width);
		this.height = Util.tryGetFloat(root.getAttribute("height"), this.height);
		String nResource = root.getAttribute("resource");
		this.resource = TextUtils.isEmpty(nResource) ? this.resource : nResource;
		String nName = root.getAttribute("name");
		this.name = TextUtils.isEmpty(nName) ? this.name : nName;
		this.direction = Util.tryGetDirection(root.getAttribute("direction"), this.direction);
		this.moveState = Util.tryGetMoveState(root.getAttribute("state"), this.moveState);
		this.noclip = Util.tryGetBool(root.getAttribute("noclip"), this.noclip);
		this.money = Util.tryGetLong(root.getAttribute("money"), this.money);
		
		Element inv = (Element)root.getElementsByTagName("inventory").item(0);
		if(inv != null){
			boolean clearInv = Util.tryGetBool(inv.getAttribute("clear"), false);
			if(clearInv)
				this.inventory = new Inventory();
			NodeList itemNodes = inv.getElementsByTagName("item");
			for(int i = 0; i < itemNodes.getLength(); i++){
				Node n = itemNodes.item(i);
				if(n.getNodeType() != Node.ELEMENT_NODE)
					continue;
				Element itemEl = (Element)n;
				String itemName = itemEl.getAttribute("name");
				int count = Util.tryGetInt(itemEl.getAttribute("count"), 1);
				inventory.add(Item.get(itemName), count);
			}
		}
		
		Element shopEl = (Element)root.getElementsByTagName("shop").item(0);
		if(shopEl != null){
			Shop shop = new Shop(this);
			shop.setPurchaseMultiplier(Util.tryGetFloat(shopEl.getAttribute("buyMulti"), shop.getPurchaseMultiplier()));
			shop.setSellMultiplier(Util.tryGetFloat(shopEl.getAttribute("sellMulti"), shop.getSellMultiplier()));
			Shop.register(shop);
		}

		this.script = root.getAttribute("script");
	}
	
	@Override
	public void load(Game game, TinyInputStream in)throws IOException{
		super.load(game, in);
		this.noclip = in.readBoolean();
		this.money = in.readLong();
		this.inventory = new Inventory();
		inventory.load(in);
		this.script = in.readString();
		this.runtimeProperties = new HashMap<String, String>();
		int runtimePropCount = in.readInt();
		while(runtimeProperties.size() < runtimePropCount)
			setRuntimeProperty(in.readString(), in.readString());
	}
	
	@Override
	public void save(TinyOutputStream out)throws IOException{
		super.save(out);
		out.write(noclip);
		out.write(money);
		inventory.save(out);
		out.writeString(script);
		out.write(runtimeProperties.size());
		for(String key : runtimeProperties.keySet()){
			out.writeString(key);
			out.writeString(runtimeProperties.get(key));
		}
	}
	
	@Override
	public void update(Game game){
		super.update(game);
		
		if(ticker.shouldRun(game))
			tick(game);
	}
	
	public void tick(Game game){}

	public boolean isPlayer(){
		return this instanceof EntityPlayer;
	}
}
