package net.site40.rodit.tinyrpg.game.event;

import net.site40.rodit.tinyrpg.game.Game;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;

import android.text.TextUtils;

public class EventReceiver {

	public static enum EventType{
		BATTLE_START, BATTLE_END, ITEM_EQUIP, ITEM_UNEQUIP, ITEM_DROP, MAP_CHANGE, KEY_DOWN, KEY_UP, ENTITY_SPAWNED, ENTITY_DESPAWNED;
	}

	private String name;
	private String script;
	private EventType type;

	private Function jsOnEvent;
	
	public EventReceiver(String name, String script, EventType type){
		this(name, script, type, null);
	}

	public EventReceiver(String name, String script, EventType type, Object jsOnEvent){
		this.name = name;
		this.script = script;
		this.type = type;
		this.jsOnEvent = (Function)Context.jsToJava(jsOnEvent, Function.class);
	}

	public String getName(){
		return name;
	}

	public String getScript(){
		return script;
	}

	public EventType getType(){
		return type;
	}

	public void registerCallbacks(Object onEvent){
		jsOnEvent = (Function)Context.jsToJava(onEvent, Function.class);
	}

	public void initCallbacks(Game game){
		if(!TextUtils.isEmpty(script) && jsOnEvent == null)
			game.getScripts().execute(game, script, new String[] { "self" }, new Object[] { this });
	}

	public void onEvent(Game game, Object... args){
		initCallbacks(game);
		if(jsOnEvent == null)
			return;
		game.getScripts().executeFunction(game, jsOnEvent, this, new String[0], new Object[0], args);
	}
}
