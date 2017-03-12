package net.site40.rodit.tinyrpg.game.script;

import java.util.regex.Pattern;

import net.site40.rodit.tinyrpg.game.Dialog;
import net.site40.rodit.tinyrpg.game.Dialog.DialogCallback;
import net.site40.rodit.tinyrpg.game.Game;
import net.site40.rodit.tinyrpg.game.Scheduler.ScheduledEvent;
import net.site40.rodit.tinyrpg.game.battle.Battle;
import net.site40.rodit.tinyrpg.game.battle.Team;
import net.site40.rodit.tinyrpg.game.entity.Damage;
import net.site40.rodit.tinyrpg.game.entity.Damage.SourceStack;
import net.site40.rodit.tinyrpg.game.entity.Entity;
import net.site40.rodit.tinyrpg.game.entity.EntityLiving;
import net.site40.rodit.tinyrpg.game.entity.EntityStats;
import net.site40.rodit.tinyrpg.game.event.EventReceiver;
import net.site40.rodit.tinyrpg.game.event.EventReceiver.EventType;
import net.site40.rodit.tinyrpg.game.faction.Faction;
import net.site40.rodit.tinyrpg.game.gui.Gui;
import net.site40.rodit.tinyrpg.game.gui.GuiLoading;
import net.site40.rodit.tinyrpg.game.gui.windows.Window;
import net.site40.rodit.tinyrpg.game.item.Item;
import net.site40.rodit.tinyrpg.game.map.Region;
import net.site40.rodit.tinyrpg.game.object.GameObject;
import net.site40.rodit.tinyrpg.game.render.DialogText;
import net.site40.rodit.tinyrpg.game.render.Strings;
import net.site40.rodit.tinyrpg.game.render.Strings.GameData;
import net.site40.rodit.tinyrpg.game.render.TextRenderer;
import net.site40.rodit.tinyrpg.game.render.effects.EffectCompletionHolder;
import net.site40.rodit.tinyrpg.game.render.effects.FadeInEffect;
import net.site40.rodit.tinyrpg.game.render.effects.FadeOutEffect;
import net.site40.rodit.tinyrpg.game.script.ScriptManager.KVP;
import net.site40.rodit.tinyrpg.game.shop.ShopLinker;
import net.site40.rodit.util.ArrayUtil;
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

	public static Entity fakeEntity = new Entity();

	private Game game;

	public ScriptHelper(Game game){
		this.game = game;
	}

	public Item getItem(String name){
		return Item.get(name);
	}

	public Faction getFaction(String name){
		return Faction.get(name);
	}

	public String[] genArray(int capacity){
		return new String[capacity];
	}

	public void writeArray(String[] array, int index, String value){
		array[index] = value;
	}

	public String[] safeSplit(String in, String del){
		return in.split(Pattern.quote(del));
	}

	public String[] empty(){
		return GameData.EMPTY_STRING_ARRAY;
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
			dialog(Strings.Dialog.TALK_BROKEN);
	}

	public boolean checkGlobal(String name, String defaultValue){
		if(game.getGlobal(name) == null){
			game.setGlobal(name, defaultValue);
			return false;
		}
		return true;
	}

	public Dialog dialog(String body){
		return dialog(body, GameData.EMPTY_STRING_ARRAY);
	}

	public Dialog dialog(String body, String[] options){
		return dialog(body, options, (Function)null);
	}
	
	public Dialog dialog(String body, String[] options, Object function){
		return dialog(body, options, function, GameData.EMPTY_ARRAY);
	}

	public Dialog dialog(String body, String[] options, Object function, Object[] args){
		if(TextUtils.isEmpty(body) && options.length == 0)
			return null;
		if(function instanceof DialogCallback)
			return dialogJCallback(body, options, (DialogCallback)function, args);
		Function func = (Function)Context.jsToJava(function, Function.class);
		Dialog d = new Dialog(body, options, func);
		d.setArgs(args);
		game.addObject(d);
		return d;
	}
	
	public Dialog dialogJCallback(String body, String[] options, DialogCallback callback){
		return dialogJCallback(body, options, callback, GameData.EMPTY_ARRAY);
	}

	public Dialog dialogJCallback(String body, String[] options, DialogCallback callback, Object[] args){
		Dialog d = new Dialog(body, options, callback);
		d.setOptions(options);
		d.setArgs(args);
		game.addObject(d);
		return d;
	}

	public Object runScript(String file){
		return runScript(file, GameData.EMPTY_STRING_ARRAY, GameData.EMPTY_ARRAY);
	}

	public Object runScript(String file, String[] varNames, Object[] varVals){
		return game.getScript().runScript(game, file, KVP.get(varNames, varVals));
	}

	public static final String GUI_PACKAGE = "net.site40.rodit.tinyrpg.game.gui.";
	@SuppressWarnings("unchecked")
	public void showGui(String className){
		if(!className.startsWith(GUI_PACKAGE))
			className = GUI_PACKAGE + className;
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
		if(!className.startsWith(GUI_PACKAGE))
			className = GUI_PACKAGE + className;
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
		if(!className.startsWith(GUI_PACKAGE))
			className = GUI_PACKAGE + className;
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
		setMap(name, false);
	}

	private boolean asyncMapLoading = false;
	public void setMap(final String name, final boolean fromSave){
		game.setGlobalb("transitioning", true);
		if(asyncMapLoading){
			game.getInput().allowMovement(false);
			game.getGuis().show(GuiLoading.class);
			game.runAsyncTask(new Runnable(){
				public void run(){
					game.switchMap(name);
					//game.setMap(null);
					//game.setMap(new MapState((RPGMap)game.getResources().getObject(name)), fromSave);
				}
			}, new GenericCallback(){
				public void callback(){
					game.getGuis().hide(GuiLoading.class);
					game.getInput().allowMovement(true);
				}
			});
		}else{
			game.getInput().allowMovement(false);
			FadeOutEffect effect = new FadeOutEffect(250L);
			EffectCompletionHolder holder = new EffectCompletionHolder(effect, new Runnable(){
				public void run(){
					game.switchMap(name);

					FadeInEffect fadeIn = new FadeInEffect(250L);
					EffectCompletionHolder holder2 = new EffectCompletionHolder(fadeIn, new Runnable(){
						@Override
						public void run(){
							game.getInput().allowMovement(true);
						}
					});
					game.getPostProcessor().add(fadeIn);
					game.getPostProcessor().add(holder2);
					game.skipFrames(1);
				}
			});
			game.getPostProcessor().add(effect);
			game.getPostProcessor().add(holder);
		}
	}

	public void log(String msg){
		Log.d("Script", msg);
	}

	public GameObject createObject(String resource, float x, float y, float width, float height){
		return createObject(resource, x, y, width, height, true);
	}

	public GameObject createObject(String resource, float x, float y, float width, float height, boolean scale){
		GameObject obj = new GameObject(){
			@Override
			public boolean shouldScale(){ return false; }
		};
		obj.setBounds(x, y, width, height);
		obj.setResource(resource);
		obj.setName("script_obj_" + game.getRandom().nextString(4));
		return obj;
	}

	public TextRenderer createText(String text, float x, float y, float size){
		Paint paint = Game.getDefaultPaint();
		paint.setTextAlign(Align.CENTER);
		paint.setTextSize(size);
		paint.setColor(Color.WHITE);
		return new TextRenderer(text, x, y, paint);
	}

	public void givePlayerItem(String itemName, int count){
		Log.i("a", itemName);
		game.getPlayer().getInventory().add(Item.get(itemName), count);
	}

	public ScheduledEvent scheduleFunction(Object function, final Object self, long delay){
		return scheduleFunction(function, self, delay, GameData.EMPTY_ARRAY);
	}

	public ScheduledEvent scheduleFunction(Object function, final Object self, long delay, final Object[] args){
		final Function fnc = (Function)Context.jsToJava(function, Function.class);
		Runnable runnable = new Runnable(){
			@Override
			public void run(){
				game.getScript().runFunction(game, fnc, self, KVP.EMPTY, args);
			}
		};
		return game.getScheduler().schedule(runnable, game.getTime(), delay);
	}

	@SuppressLint("DefaultLocale")
	public EventReceiver registerEvent(String name, String event, Object function, final Object... extraArgs){
		EventType type = EventType.valueOf(event.toUpperCase());
		EventReceiver receiver = new EventReceiver(name, "", type, function){
			@Override
			public void onEvent(Game game, Object... args){
				super.onEvent(game, ArrayUtil.concat(args, extraArgs));
				game.unregisterEvent(this);
			}
		};
		game.registerEvent(receiver);
		return receiver;
	}

	public EventReceiver getEvent(String name){
		for(EventReceiver receiver : game.getEvents().getReceivers())
			if(receiver.getName().equals(name))
				return receiver;
		return null;
	}
	
	public void openShop(EntityLiving owner){
		ShopLinker shop = ShopLinker.getLinker(owner);
		if(shop != null)
			shop.open(game);
		else
			Log.e("ScriptHelper", "Tried to open unlinked shop (entity=" + owner.getName() + ").");
	}

	public static final String WINDOW_PACKAGE = "net.site40.rodit.tinyrpg.game.gui.windows";
	@SuppressWarnings("unchecked")
	public void showWindow(String windowName, Object... args){
		if(!windowName.startsWith(WINDOW_PACKAGE))
			windowName = WINDOW_PACKAGE + "." + windowName;
		Class<? extends Window> windowCls = null;
		Window windowObj = null;
		try{
			windowCls = (Class<? extends Window>)Class.forName(windowName);
			Class<?>[] constructTypes = new Class<?>[args.length + 1];
			constructTypes[0] = Game.class;
			for(int i = 0; i < args.length; i++)
				constructTypes[i + 1] = args[i].getClass();
			Object[] nargs = new Object[args.length + 1];
			nargs[0] = game;
			for(int i = 1; i < nargs.length; i++)
				nargs[i] = args[i - 1];
			windowObj = windowCls.getConstructor(constructTypes).newInstance(nargs);
		}catch(Exception e){
			Log.e("ScriptHelper", "Error while showing window " + windowName + " - " + e.getMessage());
			e.printStackTrace();
			return;
		}
		if(windowObj == null)
			return;
		game.getWindows().register(windowObj);
		windowObj.show();
	}

	@SuppressWarnings("unchecked")
	public void closeWindow(String windowName){
		try{
			Window win = game.getWindows().get((Class<? extends Window>)Class.forName(windowName));
			if(win != null)
				win.close();
		}catch(Exception e){
			Log.e("ScriptHelper", "Error while closing window " + windowName + " - " + e.getMessage());
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	public void hideWindow(String windowName){
		if(!windowName.startsWith(WINDOW_PACKAGE))
			windowName = WINDOW_PACKAGE + "." + windowName;
		try{
			Class<? extends Window> cls = (Class<? extends Window>)Class.forName(windowName);
			game.getWindows().get(cls).hide();
		}catch(Exception e){
			Log.e("ScriptHelper", "Error while hiding window " + windowName + " - " + e.getMessage());
			e.printStackTrace();
		}
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

	public static final String ENTITY_PACKAGE = "net.site40.rodit.tinyrpg.game.entity.";
	@SuppressWarnings("unchecked")
	public Entity createEntity(String config){
		Document document = game.getResources().readDocument(config);
		String className = ((Element)document.getElementsByTagName("entity").item(0)).getAttribute("class");
		if(!className.startsWith(ENTITY_PACKAGE))
			className = ENTITY_PACKAGE + className;
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
	public Region getRegion(String region){
		return Region.valueOf(region.toUpperCase());
	}

	public Damage damage(Object[] source, String type, float damage){
		return new Damage(new SourceStack(source), Util.tryGetDamageType(type), damage);
	}

	public Team createTeam(EntityLiving... members){
		return new Team(members);
	}

	public Battle battle(String region, EntityLiving attack, EntityLiving defence){
		return battle(region, createTeam(attack), createTeam(defence));
	}

	public Battle battle(String region, Team attack, Team defence){
		return battle(getRegion(region), attack, defence);
	}
	
	public Battle battle(Region region, Team attack, Team defence){
		Battle b = new Battle(region, attack, defence);
		game.setBattle(b);
		return b;
	}

	public void queueFunction(Function func, Object self, Object[] args){
		queueFunction(func, self, KVP.EMPTY, args);
	}
	
	public void queueFunction(Function func, final Object self, final KVP<?>[] kvps, final Object[] args){
		game.getScript().runFunction(game, func, self, kvps, args);
	}

	private Util util;
	public Util getUtil(){
		return util == null ? util = new Util() : util;
	}
}
