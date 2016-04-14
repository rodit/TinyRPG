package net.site40.rodit.tinyrpg.game.item;

import net.site40.rodit.tinyrpg.game.Game;
import net.site40.rodit.tinyrpg.game.entity.Entity;
import net.site40.rodit.tinyrpg.game.entity.EntityLiving;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.w3c.dom.Element;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.TextUtils;

public class Weapon extends ItemEquippable{
	
	private float damage;
	
	private Function jsOnHit;
	
	public Weapon(){
		this("", "", "", "", "", Rarity.UNKNOWN, 0L, 0f);
	}
	
	public Weapon(String name, String showName, String description, String script, String resource, Rarity rarity, long value, float damage){
		super(name, showName, description, script, resource, rarity, 0L, SLOT_HAND_0, SLOT_HAND_1);
		this.damage = damage;
	}
	
	public float getDamage(){
		return damage;
	}
	
	public void setDamage(float damage){
		this.damage = damage;
	}
	
	public void registerCallbacks(Object onEquip, Object onUnEquip, Object onHit){
		super.registerCallbacks(onEquip, onUnEquip);
		jsOnHit = (Function)Context.jsToJava(onHit, Function.class);
	}
	
	@Override
	public boolean drawSpriteOverlay(Canvas canvas, Game game, EntityLiving equipper, Paint paint){
		if(!super.drawSpriteOverlay(canvas, game, equipper, paint))
			return drawSpriteOverlay(canvas, game, equipper, paint, "sword_generic");
		return true;
	}
	
	@Override
	public void initCallbacks(Game game){
		super.initCallbacks(game);
		if(!TextUtils.isEmpty(script) && jsOnHit == null)
			game.getScripts().execute(game, script, new String[] { "self" }, new Object[] { this });
	}
	
	public void onHit(Game game, Entity user, Entity receiver){
		initCallbacks(game);
		if(jsOnHit != null)
			game.getScripts().executeFunction(game, jsOnHit, this, new String[0], new Object[0], new Object[] { user, receiver });
	}
	
	@Override
	public void deserializeXmlElement(Element e){
		super.deserializeXmlElement(e);
		String dmgAttrib = e.getAttribute("damage");
		if(!TextUtils.isEmpty(dmgAttrib))
			damage = Float.valueOf(dmgAttrib);
	}
	
	public boolean isMagic(){
		return name.contains("staff");
	}
}
