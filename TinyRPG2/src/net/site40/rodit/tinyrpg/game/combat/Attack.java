package net.site40.rodit.tinyrpg.game.combat;

import java.util.Collection;
import java.util.HashMap;

import net.site40.rodit.tinyrpg.game.Game;
import net.site40.rodit.tinyrpg.game.entity.EntityLiving;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.w3c.dom.Element;

import android.text.TextUtils;

public class Attack {

	private static HashMap<String, Attack> instances = new HashMap<String, Attack>();

	public static Collection<Attack> getItems(){
		return instances.values();
	}

	public static void register(Attack attack){
		instances.put(attack.getName(), attack);
	}

	public static void unregister(String name){
		instances.remove(name);
	}

	public static Attack get(String name){
		return instances.get(name);
	}

	private String name;
	private String showName;
	private String resource;
	private String script;
	private float damage;

	private Function jsOnUse;

	public Attack(){
		this("", "", "", "", 0f);
	}

	public Attack(String name, String showName, String resource, String script, float damage){
		this.name = name;
		this.showName = showName;
		this.resource = resource;
		this.script = script;
		this.damage = damage;
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

	public String getResource(){
		return resource;
	}

	public void setResource(String resource){
		this.resource = resource;
	}

	public String getScript(){
		return script;
	}

	public void setScript(String script){
		this.script = script;
	}

	public float getDamage(){
		return damage;
	}

	public void setDamage(float damage){
		this.damage = damage;
	}

	public void registerCallbacks(Object onUse){
		this.jsOnUse = (Function)Context.jsToJava(onUse, Function.class);
	}

	public void initCallbacks(Game game){
		if(!TextUtils.isEmpty(script) && jsOnUse == null)
			game.getScripts().execute(game, script, new String[] { "self" }, new Object[] { this });
	}

	public void onUse(Game game, EntityLiving user, EntityLiving target){
		initCallbacks(game);
		if(jsOnUse != null)
			game.getScripts().executeFunction(game, jsOnUse, this, new String[0], new Object[0], new Object[] { user, target });
	}

	public void deserializeXmlElement(Element e){
		name = e.getAttribute("name");
		showName = e.getAttribute("showName");
		resource = e.getAttribute("resource");
		script = e.getAttribute("script");
		String dmgStr = e.getAttribute("damage");
		if(!TextUtils.isEmpty(dmgStr))
			damage = Float.valueOf(dmgStr);
	}
}
