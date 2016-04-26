package net.site40.rodit.tinyrpg.game.script;

import java.util.regex.Pattern;

import net.site40.rodit.tinyrpg.game.Dialog;
import net.site40.rodit.tinyrpg.game.Dialog.DialogCallback;
import net.site40.rodit.tinyrpg.game.Game;
import net.site40.rodit.tinyrpg.game.Scheduler.ScheduledEvent;
import net.site40.rodit.tinyrpg.game.battle.Battle;
import net.site40.rodit.tinyrpg.game.entity.Entity;
import net.site40.rodit.tinyrpg.game.entity.EntityLiving;
import net.site40.rodit.tinyrpg.game.entity.EntityStats;
import net.site40.rodit.tinyrpg.game.event.EventReceiver;
import net.site40.rodit.tinyrpg.game.event.EventReceiver.EventType;
import net.site40.rodit.tinyrpg.game.gui.Gui;
import net.site40.rodit.tinyrpg.game.gui.GuiLoading;
import net.site40.rodit.tinyrpg.game.item.Item;
import net.site40.rodit.tinyrpg.game.map.MapState;
import net.site40.rodit.tinyrpg.game.map.RPGMap;
import net.site40.rodit.tinyrpg.game.map.Region;
import net.site40.rodit.tinyrpg.game.render.DialogText;
import net.site40.rodit.tinyrpg.game.render.Sprite;
import net.site40.rodit.tinyrpg.game.render.SpriteNonScale;
import net.site40.rodit.tinyrpg.game.render.TextRenderer;
import net.site40.rodit.util.GenericCallback;
import net.site40.rodit.util.Util;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.text.TextUtils;
import android.util.Log;


public class ScriptHelper {

	private Game game;

	public ScriptHelper(Game game){
		this.game = game;
	}
	
	public Item getItem(String name){
		return Item.get(name);
	}
	
	public String[] safeSplit(String in, String del){
		return in.split(Pattern.quote(del));
	}

	public String[] empty(){
		return new String[0];
	}
	
	public int[] emptyi(){
		return new int[0];
	}

	public String[] array(String... args){
		return args;
	}

	public int[] arrayi(int... args){
		return args;
	}

	public float[] arrayf(float... args){
		return args;
	}

	public Object[] arrayo(Object... args){
		return args;
	}
	
	public float percentageMulti(float percent){
		return 1 + percent / 100f;
	}

	public int parseInt(String num){
		return Integer.valueOf(num);
	}

	public long parseLong(String num){
		return Long.valueOf(num);
	}

	public boolean lessThan(float f0, float f1){
		return f0 > f1;
	}

	public boolean greaterThan(float f0, float f1){
		return f0 > f1;
	}

	public boolean should(int threshhold){
		return game.getRandom().nextInt() >= threshhold;
	}

	public float pow(float num, float pow){
		return (float)Math.pow(num, pow);
	}

	public static float fPointF(float f0, float f1){
		return EntityStats.fPointF(f0, f1);
	}

	public String replace(String haystack, String needle, String replace){
		return haystack.replace(needle, replace);
	}

	public String fixNewLines(String str){
		return str.replace("\\n", "\n");
	}
	
	public void runTalk(String resource){
		DialogText dialog = game.getResources().getDialogText(resource);
		if(dialog != null)
			dialog.run(game);
		else
			dialog("My dialog is broken for some reason. I can't talk right now.~Sorry about that. Contact the developers immediately for a fix.");
	}

	public Dialog dialog(String body){
		return dialog(body, new String[0]);
	}

	public Dialog dialog(String body, String[] options){
		return dialog(body, options, null);
	}

	public Dialog dialog(String body, String[] options, Object function){
		return dialog(body, options, function, new Object[0]);
	}

	public Dialog dialog(String body, String[] options, Object function, Object[] args){
		if(TextUtils.isEmpty(body) && options.length == 0)
			return null;
		Function func = (Function)Context.jsToJava(function, Function.class);
		Dialog d = new Dialog(body, options, func);
		d.setArgs(args);
		game.addObject(d);
		return d;
	}

	public Dialog dialog(String body, String[] options, DialogCallback callback){
		Dialog d = new Dialog(body, options, callback);
		d.setOptions(options);
		game.addObject(d);
		return d;
	}
	
	public Object runScript(String file){
		return runScript(file, new String[0], new Object[0]);
	}

	public Object runScript(String file, String[] varNames, Object[] varVals){
		return game.getScripts().execute(game, file, varNames, varVals);
	}

	@SuppressWarnings("unchecked")
	public void showGui(String className){
		if(!className.startsWith("net.site40.rodit.tinyrpg.game.gui."))
			className = "net.site40.rodit.tinyrpg.game.gui." + className;
		Class<? extends Gui> cls = null;
		try{
			cls = (Class<? extends Gui>)Class.forName(className);
		}catch(Exception e){
			e.printStackTrace();
		}
		game.getGuis().show(cls);
		Log.d("ScriptHelper", "Shown gui " + className + ".");
	}

	@SuppressWarnings("unchecked")
	public void hideGui(String className){
		if(!className.startsWith("net.site40.rodit.tinyrpg.game.gui."))
			className = "net.site40.rodit.tinyrpg.game.gui." + className;
		Class<? extends Gui> cls = null;
		try{
			cls = (Class<? extends Gui>)Class.forName(className);
		}catch(Exception e){
			e.printStackTrace();
		}
		game.getGuis().hide(cls);
		Log.d("ScriptHelper", "Hidden gui " + className + ".");
	}
	
	@SuppressWarnings("unchecked")
	public Gui getGui(String className){
		if(!className.startsWith("net.site40.rodit.tinyrpg.game.gui."))
			className = "net.site40.rodit.tinyrpg.game.gui." + className;
		Class<? extends Gui> cls = null;
		try{
			cls = (Class<? extends Gui>)Class.forName(className);
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
		return game.getGuis().get(cls);
	}

	public void setMap(final String name){
		game.getGuis().show(GuiLoading.class);
		game.runAsyncTask(new Runnable(){
			public void run(){
				game.setMap(null);
				game.setMap(new MapState((RPGMap)game.getResources().getObject(name)));
			}
		}, new GenericCallback(){
			public void callback(){
				game.getGuis().hide(GuiLoading.class);
			}
		});
	}

	public void log(String msg){
		Log.d("Script", msg);
	}

	public Sprite createObject(String resource, float x, float y, float width, float height){
		return createObject(resource, x, y, width, height, true);
	}

	public Sprite createObject(String resource, float x, float y, float width, float height, boolean scale){
		return scale ? new Sprite(x, y, width, height, resource, "script_obj_" + game.getRandom().nextString(4)) : new SpriteNonScale(x, y, width, height, resource, "script_obj_" + game.getRandom().nextString(4));
	}
	
	public TextRenderer createText(String text, float x, float y, float size){
		Paint paint = Game.getDefaultPaint();
		paint.setTextAlign(Align.CENTER);
		paint.setTextSize(size);
		paint.setColor(Color.WHITE);
		return new TextRenderer(text, x, y, paint);
	}

	public void givePlayerItem(String itemName, int count){
		game.getPlayer().getInventory().add(Item.get(itemName), count);
	}

	public ScheduledEvent scheduleFunction(Object function, final Object self, long delay){
		return scheduleFunction(function, self, delay, new Object[0]);
	}
	
	public ScheduledEvent scheduleFunction(Object function, final Object self, long delay, final Object[] args){
		final Function fnc = (Function)Context.jsToJava(function, Function.class);
		Runnable runnable = new Runnable(){
			@Override
			public void run(){
				game.getScripts().executeFunction(game, fnc, self, new String[0], new Object[0], args);
			}
		};
		return game.getScheduler().schedule(runnable, game.getTime(), delay);
	}
	
	@SuppressLint("DefaultLocale")
	public void scheduleEvent(String name, String event, Object function){
		EventType type = EventType.valueOf(event.toUpperCase());
		EventReceiver receiver = new EventReceiver(name, "", type, function){
			@Override
			public void onEvent(Game game, Object... args){
				super.onEvent(game, args);
				game.unregisterEvent(this);
			}
		};
		game.registerEvent(receiver);
	}
	
	public EventReceiver getEvent(String name){
		for(EventReceiver receiver : game.getEvents().getReceivers())
			if(receiver.getName().equals(name))
				return receiver;
		return null;
	}

	public void var_dump(Object o){
		String dump = "[VAR DUMP]\nHash Code: " + o.toString() + "\nClass: " + o.getClass().getName();
		if(o instanceof Entity){
			Entity e = (Entity)o;
			dump += "\n[ENTITY]";
			dump += "\nName: " + e.getName();
			dump += "\nBounds: " + e.getBounds().toString();
		}
		Log.d("ScriptHelper", dump);
	}
	
	public EntityLiving createEntity(String name, String resource, String script, int maxHealth){
		EntityLiving ent = new EntityLiving(maxHealth);
		ent.setName(name);
		ent.setResource(resource);
		ent.setScript(script);
		return ent;
	}
	
	public static final String ENTITY_PACKAGE = "net.site40.rodit.tinyrpg.game.entity";
	@SuppressWarnings("unchecked")
	public Entity createEntity(String config){
		Document document = game.getResources().readDocument(config);
		String className = ((Element)document.getElementsByTagName("entity").item(0)).getAttribute("class");
		if(!className.startsWith(ENTITY_PACKAGE))
			className = ENTITY_PACKAGE + "." + className;
		Entity entity = null;
		try{
			Class<? extends Entity> cls = (Class<? extends Entity>)Class.forName(className);
			entity = cls.newInstance();
		}catch(Exception e){}
		if(entity != null)
			entity.linkConfig(document);
		game.setGlobal(entity.getName() + "_talk_count", 0);
		return entity;
	}

	@SuppressLint("DefaultLocale")
	public Battle battle(String region, EntityLiving attack, EntityLiving defence){
		Battle b = new Battle(Region.valueOf(region.toUpperCase()), attack, defence);
		game.setBattle(b);
		return b;
	}
	
	private Util util;
	public Util getUtil(){
		return util == null ? util = new Util() : util;
	}
}
