package net.site40.rodit.tinyrpg.game.effect;

import java.io.IOException;
import java.util.ArrayList;

import net.site40.rodit.tinyrpg.game.Game;
import net.site40.rodit.tinyrpg.game.entity.EntityLiving;
import net.site40.rodit.util.TinyInputStream;
import net.site40.rodit.util.TinyOutputStream;
import net.site40.rodit.util.Util;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.w3c.dom.Element;

import android.text.TextUtils;

public class Effect {
	
	private static ArrayList<Effect> effects = new ArrayList<Effect>();
	
	public static void register(Effect effect){
		if(!effects.contains(effect))
			effects.add(effect);
	}
	
	public static void unregister(Effect effect){
		effects.remove(effect);
	}
	
	public static Effect get(String name){
		for(Effect effect : effects)
			if(effect.name.equals(name))
				return effect;
		return null;
	}
	
	protected String name;
	protected String showName;
	protected String resource;
	protected String script;
	protected int level;
	protected boolean negative;
	
	protected boolean started;
	protected boolean stopped;
	
	protected Function jsStart;
	protected Function jsEnd;
	
	public Effect(){
		this("", "", "", "", 1);
	}
	
	public Effect(String name, String showName, String resource, String script, int level){
		this.name = name;
		this.showName = showName;
		this.resource = resource;
		this.script = script;
		this.level = level;
	}
	
	public Effect(Effect effect){
		copy(effect);
	}
	
	public void copy(Effect effect){
		this.name = effect.name;
		this.showName = effect.showName;
		this.resource = effect.resource;
		this.script = effect.script;
		this.level = effect.level;
		this.negative = effect.negative;
		this.jsStart = effect.jsStart;
		this.jsEnd = effect.jsEnd;
	}
	
	public String getName(){
		return name;
	}
	
	public String getShowName(){
		return showName;
	}
	
	public String getResource(){
		return resource;
	}
	
	public String getScript(){
		return script;
	}
	
	public int getLevel(){
		return level;
	}
	
	public void setLevel(int level){
		this.level = level;
	}
	
	public boolean isNegative(){
		return negative;
	}
	
	public boolean isStarted(){
		return started;
	}
	
	public boolean isStopped(){
		return stopped;
	}
	
	public void registerCallbacks(Object start, Object stop){
		this.jsStart = (Function)Context.jsToJava(start, Function.class);
		this.jsEnd = (Function)Context.jsToJava(stop, Function.class);
	}
	
	public void initCallbacks(Game game){
		if(!TextUtils.isEmpty(script) && jsStart == null && jsEnd == null)
			game.getScripts().execute(game, script, new String[] { "self" }, new Object[] { this });
	}
	
	public void start(Game game, EntityLiving entity){
		this.started = true;
		initCallbacks(game);
		if(jsStart != null)
			game.getScripts().executeFunction(game, jsStart, this, new String[0], new Object[0], new Object[] { entity });
	}
	
	public void stop(Game game, EntityLiving entity){
		this.stopped = true;
		initCallbacks(game);
		if(jsStart != null)
			game.getScripts().executeFunction(game, jsEnd, this, new String[0], new Object[0], new Object[] { entity });
	}
	
	public void deserializeXmlElement(Element e){
		this.name = e.getAttribute("name");
		this.showName = e.getAttribute("showName");
		this.resource = e.getAttribute("resource");
		this.script = e.getAttribute("script");
		this.level = Util.tryGetInt(e.getAttribute("level"), 1);
		this.negative = Util.tryGetBool(e.getAttribute("negative"), false);
	}
	
	public void load(Game game, TinyInputStream in, EntityLiving ent)throws IOException{
		this.name = in.readString();
		this.showName = in.readString();
		this.resource = in.readString();
		this.script = in.readString();
		this.level = in.readInt();
		this.negative = in.readBoolean();
		this.started = in.readBoolean();
		this.stopped = in.readBoolean();
	}
	
	public void save(TinyOutputStream out)throws IOException{
		out.writeString(name);
		out.writeString(showName);
		out.writeString(resource);
		out.writeString(script);
		out.write(level);
		out.write(negative);
		out.write(started);
		out.write(stopped);
	}
}
