package net.site40.rodit.tinyrpg.game.entity.npc;

import net.site40.rodit.tinyrpg.game.Game;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import android.graphics.Canvas;
import android.text.TextUtils;

public class EntityNPC extends EntityAI{
	
	protected String displayName;
	protected Nametag nametag;
	
	public EntityNPC(){
		this("New Entity");
	}
	
	public EntityNPC(String displayName){
		super();
		this.displayName = displayName;
	}
	
	public String getDisplayName(){
		return displayName;
	}
	
	public void setDisplayName(String displayName){
		this.displayName = displayName;
	}
	
	@Override
	public void onSpawn(Game game){
		super.onSpawn(game);
		if(nametag == null){
			nametag = new Nametag(this);
			game.getObjects().add(nametag);
		}
	}
	
	@Override
	public void onDespawn(Game game){
		super.onDespawn(game);
		game.getObjects().remove(nametag);
	}
	
	@Override
	public void draw(Game game, Canvas canvas){
		super.draw(game, canvas);
	}
	
	@Override
	public void linkConfig(Document document){
		super.linkConfig(document);
		Element root = (Element)document.getElementsByTagName("entity").item(0);
		String nDisplayName = root.getAttribute("displayName");
		this.displayName = TextUtils.isEmpty(nDisplayName) ? displayName : nDisplayName;
	}
}
