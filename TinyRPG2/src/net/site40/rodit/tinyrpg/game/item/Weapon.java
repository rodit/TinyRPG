package net.site40.rodit.tinyrpg.game.item;

import net.site40.rodit.tinyrpg.game.Game;
import net.site40.rodit.tinyrpg.game.entity.Entity;
import net.site40.rodit.tinyrpg.game.entity.EntityLiving;
import net.site40.rodit.tinyrpg.game.script.ScriptManager.KVP;
import net.site40.rodit.util.Util;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.w3c.dom.Element;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.TextUtils;

public class Weapon extends ItemEquippable{
	
	private float damage;
	private boolean twoHanded;
	
	private Function jsOnHit;
	
	public Weapon(){
		this("", "", "", "", "", Rarity.UNKNOWN, 0L, 0f, false);
	}
	
	public Weapon(String name, String showName, String description, String script, String resource, Rarity rarity, long value, float damage, boolean twoHanded){
		super(name, showName, description, script, resource, rarity, 0L, SLOT_HAND_0, SLOT_HAND_1);
		this.damage = damage;
		this.twoHanded = twoHanded;
	}
	
	public float getDamage(){
		return damage;
	}
	
	public void setDamage(float damage){
		this.damage = damage;
	}
	
	public boolean isTwoHanded(){
		return twoHanded;
	}
	
	public void setTwoHanded(boolean twoHanded){
		this.twoHanded = twoHanded;
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
			game.getScript().runScript(game, script, new KVP<Weapon>("self", this));
	}
	
	public void onHit(Game game, Entity user, Entity receiver){
		initCallbacks(game);
		if(jsOnHit != null)
			game.getScript().runFunction(game, jsOnHit, this, KVP.EMPTY, user, receiver);
	}
	
	@Override
	public void deserializeXmlElement(Element e){
		super.deserializeXmlElement(e);
		damage = Util.tryGetFloat(e.getAttribute("damage"), damage);
		twoHanded = Util.tryGetBool(e.getAttribute("twoHanded"), twoHanded);
	}
	
	public boolean isMagic(){
		return name.contains("staff");
	}
}
