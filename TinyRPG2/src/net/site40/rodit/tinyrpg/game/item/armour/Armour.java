package net.site40.rodit.tinyrpg.game.item.armour;

import net.site40.rodit.tinyrpg.game.Game;
import net.site40.rodit.tinyrpg.game.entity.Entity;
import net.site40.rodit.tinyrpg.game.item.ItemEquippable;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.w3c.dom.Element;

import android.text.TextUtils;

public class Armour extends ItemEquippable{
	
	private float armourValue;
	
	private Function jsOnHit;
	
	public Armour(){
		this("", "", "", "", "", Rarity.UNKNOWN, 0L, -1, 0f);
	}
	
	public Armour(String name, String showName, String description, String script, String resource, Rarity rarity, long value, int equipSlot, float armourValue){
		super(name, showName, description, script, resource, rarity, value, equipSlot);
		this.armourValue = armourValue;
	}
	
	public float getArmourValue(){
		return armourValue;
	}
	
	public void setArmourValue(float armourValue){
		this.armourValue = armourValue;
	}
	
	public void registerCallbacks(Object onEquip, Object onUnEquip, Object onHit){
		super.registerCallbacks(onEquip, onUnEquip);
		jsOnHit = (Function)Context.jsToJava(onHit, Function.class);
	}
	
	@Override
	public void initCallbacks(Game game){
		super.initCallbacks(game);
		if(!TextUtils.isEmpty(script) && jsOnHit == null)
			game.getScripts().execute(game, script, new String[] { "self" }, new Object[] { this });
	}
	
	public void onHit(Game game, Entity sender, Entity receiver){
		initCallbacks(game);
		if(jsOnHit != null)
			game.getScripts().executeFunction(game, jsOnHit, this, new String[0], new Object[0], new Object[] { sender, receiver });
	}
	
	@Override
	public void deserializeXmlElement(Element e){
		super.deserializeXmlElement(e);
		String armourValueS = e.getAttribute("armourValue");
		if(!TextUtils.isEmpty(armourValueS))
			this.armourValue = Float.valueOf(armourValueS);
	}
}
