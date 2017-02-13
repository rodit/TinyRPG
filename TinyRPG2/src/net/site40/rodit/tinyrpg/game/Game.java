package net.site40.rodit.tinyrpg.game;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;

import net.site40.rodit.tinyrpg.game.Benchmark.BenchmarkClass;
import net.site40.rodit.tinyrpg.game.MemoryProfiler.MemoryProfile;
import net.site40.rodit.tinyrpg.game.audio.AudioManager;
import net.site40.rodit.tinyrpg.game.battle.Battle;
import net.site40.rodit.tinyrpg.game.chat.Chat;
import net.site40.rodit.tinyrpg.game.entity.Entity;
import net.site40.rodit.tinyrpg.game.entity.EntityPlayer;
import net.site40.rodit.tinyrpg.game.event.EventHandler;
import net.site40.rodit.tinyrpg.game.event.EventReceiver;
import net.site40.rodit.tinyrpg.game.event.EventReceiver.EventType;
import net.site40.rodit.tinyrpg.game.forge.ForgeRegistry;
import net.site40.rodit.tinyrpg.game.gui.Gui;
import net.site40.rodit.tinyrpg.game.gui.GuiManager;
import net.site40.rodit.tinyrpg.game.gui.GuiMessage;
import net.site40.rodit.tinyrpg.game.gui.windows.WindowManager;
import net.site40.rodit.tinyrpg.game.item.Item;
import net.site40.rodit.tinyrpg.game.map.MapState;
import net.site40.rodit.tinyrpg.game.map.MobSpawnRegistry;
import net.site40.rodit.tinyrpg.game.map.RPGMap;
import net.site40.rodit.tinyrpg.game.mod.ModManager;
import net.site40.rodit.tinyrpg.game.mod.TinyMod;
import net.site40.rodit.tinyrpg.game.object.GameObject;
import net.site40.rodit.tinyrpg.game.quest.QuestManager;
import net.site40.rodit.tinyrpg.game.render.ResourceManager;
import net.site40.rodit.tinyrpg.game.render.ResourceStreamProvider.AssetStreamProvider;
import net.site40.rodit.tinyrpg.game.render.ResourceStreamProvider.ModStreamProvider;
import net.site40.rodit.tinyrpg.game.render.Strings.Benchmarks;
import net.site40.rodit.tinyrpg.game.render.XmlResourceLoader;
import net.site40.rodit.tinyrpg.game.render.effects.DayNightCycle;
import net.site40.rodit.tinyrpg.game.render.effects.EffectCompletionHolder;
import net.site40.rodit.tinyrpg.game.render.effects.FadeInEffect;
import net.site40.rodit.tinyrpg.game.render.effects.FadeOutEffect;
import net.site40.rodit.tinyrpg.game.render.effects.PostProcessor;
import net.site40.rodit.tinyrpg.game.render.effects.Weather;
import net.site40.rodit.tinyrpg.game.render.gl.GLUtil;
import net.site40.rodit.tinyrpg.game.render.gl.RoditGL;
import net.site40.rodit.tinyrpg.game.saves.Options;
import net.site40.rodit.tinyrpg.game.saves.Options.OptionChangedListener;
import net.site40.rodit.tinyrpg.game.saves.SaveManager;
import net.site40.rodit.tinyrpg.game.script.CutseneHelper;
import net.site40.rodit.tinyrpg.game.script.ScriptHelper;
import net.site40.rodit.tinyrpg.game.script.ScriptManager;
import net.site40.rodit.tinyrpg.game.script.ScriptManager.KVP;
import net.site40.rodit.tinyrpg.game.util.Direction;
import net.site40.rodit.tinyrpg.game.util.EagleTracer;
import net.site40.rodit.tinyrpg.game.util.Savable;
import net.site40.rodit.tinyrpg.game.util.ScreenUtil;
import net.site40.rodit.tinyrpg.mp.GameStatus;
import net.site40.rodit.tinyrpg.mp.MPController;
import net.site40.rodit.util.ExtendedRandom;
import net.site40.rodit.util.GenericCallback;
import net.site40.rodit.util.TinyInputStream;
import net.site40.rodit.util.TinyOutputStream;
import net.site40.rodit.util.Util;
import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.graphics.Point;
import android.graphics.PointF;
import android.opengl.Matrix;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;

public class Game implements Savable, OptionChangedListener{

	public static final byte[] FILE_SIGNATURE = new byte[] { 7, 1, 5, 7 };
	public static final String VERSION = "TinyRPG0";

	private static Paint DEFAULT_PAINT;
	public static Paint getDefaultPaint(){
		return new Paint(DEFAULT_PAINT);
	}

	public static final boolean DEBUG = true;
	public static final boolean DEBUG_DRAW = false;
	public static final boolean DEBUG_MEM = false;
	public static final boolean FRAME_SYNC = false;

	public static final float SCALE_FACTOR = 2.5f;
	public static final float SCALE_FACTOR_1 = 1f / SCALE_FACTOR;

	public static final boolean USE_OPENGL = false;

	private Thread gameThread;

	private Context context;
	private View view;
	private ExtendedRandom random;
	private Options options;
	private Scheduler scheduler;
	private EagleTracer tracer;
	private ScreenUtil screen;
	private ResourceManager resources;
	private QuestManager quests;
	private ForgeRegistry forge;
	private ArrayList<GameObject> objects;
	private ArrayList<GameObject> objRemove;
	private ArrayList<Dialog> dialogs;
	private Input input;
	private ScriptManager script;
	private GuiManager guis;
	private WindowManager windows;
	private MapState map;
	private MobSpawnRegistry mobSpawns;
	private EntityPlayer player;
	private LinkedHashMap<String, Object> globals;
	private Battle battle;
	private SaveManager saves;
	private AudioManager audio;
	private Chat chat;
	private PostProcessor pp;
	//TODO: See DayNightCycle class - now using map's static light map.
	//private Lighting lighting;
	private Weather weather;
	private DayNightCycle dayNight;
	private ModManager mods;
	private MPController mp;

	private ScriptHelper helper;

	private EventHandler events;

	private volatile long time;
	private volatile long delta;
	private long updateTime;
	private long drawTime;

	private boolean paused;

	private float scrollX = 0;
	private float scrollY = 0;

	private float screenScaleX = 0f;
	private float screenScaleY = 0f;

	private int objectModCount = 0;

	private RoditGL rodit;

	public Game(Context context, View view){
		Display display = ((Activity)context).getWindowManager().getDefaultDisplay();
		Point point = new Point();
		display.getSize(point);
		screenScaleX = point.x / 1280f;
		screenScaleY = point.y / 720f;

		if(DEBUG){
			Benchmark.start(Benchmarks.INIT);
			//TODO: Set log level
		}

		this.context = context;
		this.view = view;
		this.random = new ExtendedRandom();
		this.options = new Options();
		this.scheduler = new Scheduler();
		this.tracer = new EagleTracer();
		this.screen = new ScreenUtil(context);
		this.resources = new ResourceManager(context.getAssets());
		resources.registerProvider(new AssetStreamProvider(context.getAssets()));
		resources.registerProvider(new ModStreamProvider(this));
		this.quests = new QuestManager();
		this.forge = new ForgeRegistry();
		DEFAULT_PAINT = new Paint();
		DEFAULT_PAINT.setTypeface(resources.getFont("font/default.ttf"));
		DEFAULT_PAINT.setColor(Color.WHITE);
		DEFAULT_PAINT.setTextSize(Values.FONT_SIZE_MEDIUM);
		DEFAULT_PAINT.setAntiAlias(true);
		DEFAULT_PAINT.setFilterBitmap(false);
		this.objects = new ArrayList<GameObject>();
		this.objRemove = new ArrayList<GameObject>();
		this.dialogs = new ArrayList<Dialog>();
		this.input = new Input();
		this.script = new ScriptManager(this);
		this.guis = new GuiManager();
		this.windows = new WindowManager();
		this.map = new MapState(null);
		this.mobSpawns = new MobSpawnRegistry();
		this.player = new EntityPlayer();
		this.globals = new LinkedHashMap<String, Object>();
		globals.put("game", this);
		globals.put("helper", helper = new ScriptHelper(this));
		globals.put("cutsene", new CutseneHelper());
		globals.put("quests", quests);
		globals.put("util", new Util());
		globals.put("gui_quest", null);
		globals.put("gui_quest_parent", "");
		globals.put("tile_movement", "true");
		//GAME VARS
		globals.put("intro_done", "false");
		globals.put("start_map", "map/player_home_bedroom.tmx");
		globals.put("last_bookshelf_skill", "0");
		globals.put("bookshelf_skill_count", "0");
		globals.put("player_canswim", false);
		globals.put("player_swimming", false);
		//ENTITY VARS
		globals.put("merek_speak_count", "0");
		globals.put("test_dialog_index", "0");

		script.initializeGlobals(this);

		this.saves = new SaveManager(this);
		this.chat = new Chat();
		this.pp = new PostProcessor();
		//this.lighting = new Lighting();
		//pp.add(lighting);
		this.weather = new Weather();
		pp.add(weather);
		this.dayNight = new DayNightCycle(this);
		addObject(dayNight);
		this.audio = new AudioManager(context);

		XmlResourceLoader.loadItems(resources, "config/items.xml");
		XmlResourceLoader.loadAttacks(resources, "config/attacks.xml");
		XmlResourceLoader.loadEffects(resources, "config/effects.xml");
		XmlResourceLoader.loadQuests(quests, resources, "config/quests.xml");
		XmlResourceLoader.loadForge(forge, resources, "config/forge.xml");
		XmlResourceLoader.loadStartClasses(resources, "config/classes.xml");
		XmlResourceLoader.loadMobSpawns(this, "config/mobs.xml");

		Log.i("XmlResourceLoader", "Total load count = " + XmlResourceLoader.loadCount + ".");

		windows.initialize(this);

		this.events = new EventHandler();

		this.time = this.delta = 0L;

		script.runScript(this, "script/init/start.js", KVP.EMPTY);

		for(Item key : Item.getItems())
			player.getInventory().add(key, key.getStackSize());

		this.mods = new ModManager();
		for(TinyMod mod : mods.listMods(this)){
			try{
				if(mods.isModEnabled(this, mod))
					mod.load(this);
			}catch(Exception e){
				Log.e("Game", "Exception while loading mod.");
				e.printStackTrace();
			}
		}

		this.mp = new MPController(this);

		try{
			saves.loadOptions(this);
		}catch(IOException e){
			Log.e("Game", "IOException while loading options:");
			e.printStackTrace();
		}

		options.setChangeListener(this);

		if(DEBUG)
			Log.i("Benchmark", "Game initialization took " + Benchmark.stop(Benchmarks.INIT) + "ms.");
	}

	public Context getContext(){
		return context;
	}

	public View getView(){
		return view;
	}

	public ExtendedRandom getRandom(){
		return random;
	}

	public Options getOptions(){
		return options;
	}

	public Scheduler getScheduler(){
		return scheduler;
	}

	public EagleTracer getTracer(){
		return tracer;
	}

	public Object trace(Entity e){
		if(isShowingDialog())
			return null;
		return tracer.trace(map, e, e.getDirection() == Direction.D_DOWN ? 20f : 10f);
	}

	public ScreenUtil getScreen(){
		return screen;
	}

	public ResourceManager getResources(){
		return resources;
	}

	public QuestManager getQuests(){
		return quests;
	}

	public ForgeRegistry getForge(){
		return forge;
	}

	public ArrayList<GameObject> getObjects(){
		return objects;
	}

	public GameObject getObject(String name){
		for(GameObject obj : objects)
			if(obj.getName() != null && obj.getName().equals(name))
				return obj;
		return null;
	}

	public void addObject(GameObject object){
		synchronized(objects){
			if(!objects.contains(object)){
				objects.add(object);
				objectModCount++;
			}
		}
		synchronized(dialogs){
			if(object instanceof Dialog && !dialogs.contains(object))
				dialogs.add((Dialog)object);
		}
	}

	public void removeObject(GameObject object){
		if(object == null)
			return;
		Log.d("Game", "Removed object type " + object.getClass().getName() + " (direct=" + HANDLE_OBJECTS_DIRECTLY + ")");
		if(HANDLE_OBJECTS_DIRECTLY){
			objects.remove(object);
			objectModCount++;
		}else{
			synchronized(objRemove){
				objRemove.add(object);
				objectModCount++;
			}
		}
		synchronized(dialogs){
			dialogs.remove(object);
		}
	}

	public boolean isShowingDialog(){
		return dialogs.size() > 0;
	}

	public Input getInput(){
		return input;
	}

	public ScriptManager getScript(){
		return script;
	}

	public GuiManager getGuis(){
		return guis;
	}

	public WindowManager getWindows(){
		return windows;
	}

	public MapState getMap(){
		return map;
	}

	public MobSpawnRegistry getMobSpawns(){
		return mobSpawns;
	}

	public Battle getBattle(){
		return battle;
	}

	public void setBattle(final Battle battle){
		if(battle == null){
			this.battle = null;
			return;
		}
		FadeOutEffect out = new FadeOutEffect(250L);
		EffectCompletionHolder holder = new EffectCompletionHolder(out, new Runnable(){
			@Override
			public void run(){
				Game.this.battle = battle;
				skipFrames(1);
				FadeInEffect in = new FadeInEffect(250L);
				EffectCompletionHolder inHolder = new EffectCompletionHolder(in, new Runnable(){
					@Override
					public void run(){
						battle.start();
					}
				});
				pp.add(in);
				pp.add(inHolder);
			}
		});
		pp.add(out);
		pp.add(holder);
	}

	public SaveManager getSaves(){
		return saves;
	}

	public Chat getChat(){
		return chat;
	}

	public PostProcessor getPostProcessor(){
		return pp;
	}

	//	public Lighting getLighting(){
	//		return lighting;
	//	}

	public Weather getWeather(){
		return weather;
	}

	public AudioManager getAudio(){
		return audio;
	}

	public MPController getMP(){
		return mp;
	}

	public boolean isOnline(){
		return mp.getClient().getClient().isConnected();
	}

	public boolean isRemote(){
		return mp.getStatus() == GameStatus.REMOTE;
	}

	public void setGl(RoditGL rodit){
		this.rodit = rodit;
	}

	public RoditGL getGl(){
		return rodit;
	}

	public void releaseMap(boolean save){
		if(map == null)
			return;
		if(save){
			try{
				saves.saveMap(this, map);
			}catch(IOException e){
				Log.e("Game", "Error while saving map.");
				e.printStackTrace();
			}
		}
		removeObject(map);
		if(map.getRotObj() != null)
			removeObject(map.getRotObj());
		map.dispose(resources);
		map = null;
	}

	static boolean HANDLE_OBJECTS_DIRECTLY = false;
	public void switchMap(String mapName){
		Log.d("THREAD", "CURRENT: " + Thread.currentThread().getId() + " GAME: " + gameThread.getId());
		if(this.isGameThread())
			HANDLE_OBJECTS_DIRECTLY = true;
		RPGMap loadMap = (RPGMap)resources.getObject(mapName);
		if(loadMap == null){
			Log.e("Game", "Failed to load map: " + mapName + ". Resource was probably not found.");
			return;
		}
		setGlobalb("player_swimming", false);
		if(player == null)
			player = new EntityPlayer();
		MapState current = this.map;
		removeObject(current);
		if(current != null && current.getRotObj() != null)
			removeObject(current.getRotObj());
		if(current != null && current.getMap() != loadMap && current.getMap() != null){
			try{
				if(current != null)
					saves.saveMap(this, current);
			}catch(IOException e){
				Log.e("Game", "Error while saving map.");
				if(DEBUG)
					e.printStackTrace();
			}
			ArrayList<Entity> despawn = new ArrayList<Entity>(current.getEntities());
			for(Entity e : despawn){
				if(e != player)
					current.despawn(this, e);
			}
			if(current != null)
				current.dispose(resources);
			flushObjects();
		}
		MapState state = null;
		if(saves.isSaveLoaded()){
			try{
				state = saves.loadMap(this, mapName);
			}catch(IOException e){
				Log.w("Game", "Failed to find save state for map " + mapName + ". Loading default state instead");
			}
		}
		if(state == null)
			state = new MapState(loadMap);
		if(state != null){
			this.map = state;
			addObject(map);
			if(map.getMap().hasRot() && map.getRotObj() != null)
				addObject(map.getRotObj());
			map.spawn(this, player);
			if(isOnline())
				mp.sendMapUpdate(mapName);
		}
		HANDLE_OBJECTS_DIRECTLY = false;
		setGlobalb("transitioning", false);
		System.gc();
	}

	public void setMapOld(MapState newMap){
		setMapOld(newMap, false);
	}

	public void setMapOld(MapState newMap, boolean fromSave){
		if(player == null)
			player = new EntityPlayer();
		MapState current = this.map;
		events.onEvent(this, EventType.MAP_CHANGE, newMap, current);
		if(current != null && current != newMap){
			try{
				if(current.getMap() != null)
					saves.saveMap(this, current);
			}catch(IOException e){
				Log.e("Game/setMap", "IOException while saving map state - " + e.getMessage());
				e.printStackTrace();
			}
			ArrayList<Entity> forDespawn = new ArrayList<Entity>();
			forDespawn.addAll(current.getEntities());
			for(Entity e : forDespawn){
				if(e == player)
					continue;
				current.despawn(this, e);
			}
			removeObject(current);
			removeObject(current.getRotObj());
			current.dispose(resources);
		}
		if(!fromSave){
			try{
				MapState loaded = saves.loadMap(this, newMap == null ? "" : newMap.getMap().getFile());
				newMap = loaded == null ? newMap : loaded;
			}catch(IOException e){
				e.printStackTrace();
			}
		}
		this.map = newMap;
		if(newMap != null){
			addObject(newMap);
			if(newMap.getRotObj() != null)
				addObject(newMap.getRotObj());
			newMap.spawn(this, player);
			if(isOnline())
				mp.sendMapUpdate(newMap.getMap().getFile());
		}
		System.gc();
	}

	public void showMessage(String message, Gui returnGui){
		GuiMessage msgGui = (GuiMessage)guis.get(GuiMessage.class);
		msgGui.get("txtMessage").setText(message);
		setGlobal("gui_message_return", returnGui);
		guis.hide(returnGui);
		guis.show(GuiMessage.class);
	}

	public void scroll(float x, float y){
		scrollX += x;
		scrollY += y;
	}

	public void runAsyncTask(final Runnable runnable, final GenericCallback callback){
		new Thread(){
			@Override
			public void run(){
				runnable.run();
				if(callback != null)
					callback.callback();
			}
		}.start();
	}

	public EntityPlayer getPlayer(){
		return player;
	}

	public void setPlayer(EntityPlayer player){
		this.player = player;
	}

	public LinkedHashMap<String, Object> getGlobals(){
		return globals;
	}

	public Object getGlobal(String key){
		return globals.get(key);
	}

	public String getGlobals(String key){
		Object globalVal = getGlobal(key);
		return globalVal == null ? "" : String.valueOf(globalVal);
	}

	public int getGlobali(String key){
		return Util.tryGetInt(getGlobals(key));
	}

	public long getGloball(String key){
		return Util.tryGetLong(getGlobals(key));
	}

	public boolean getGlobalb(String key){
		return Util.tryGetBool(getGlobals(key));
	}

	public float getGlobalf(String key){
		return Util.tryGetFloat(getGlobals(key));
	}

	public void incGlobal(String key, int amount){
		setGlobal(key, Util.tryGetInt(getGlobals(key), 0) + amount);
	}

	public void decGlobal(String key, int amount){
		setGlobal(key, Util.tryGetInt(getGlobals(key), 0) - amount);
	}

	public void setGlobali(String key, int i){
		setGlobal(key, String.valueOf(i));
	}

	public void setGloball(String key, long l){
		setGlobal(key, String.valueOf(l));
	}

	public void setGlobalb(String key, boolean b){
		setGlobal(key, String.valueOf(b));
	}

	public void setGlobalf(String key, float f){
		setGlobal(key, String.valueOf(f));
	}

	public void setGlobal(String key, Object value){
		globals.put(key, value);
	}

	public ModManager getMods(){
		return mods;
	}

	public ScriptHelper getHelper(){
		return helper;
	}

	public EventHandler getEvents(){
		return events;
	}

	public void registerEvent(EventReceiver receiver){
		events.add(receiver);
	}

	public void unregisterEvent(EventReceiver receiver){
		events.remove(receiver);
	}

	public long getTime(){
		return time;
	}

	public long getDelta(){
		return delta;
	}

	public void pause(){
		paused = true;
		audio.pauseAll();
	}

	public void unpause(){
		paused = false;
		audio.unpauseAll();
	}

	public boolean isPaused(){
		return paused;
	}

	public void cleanExit(){
		((Activity)context).finish();
	}

	public void flushObjects(){
		synchronized(objects){
			for(GameObject obj : objRemove){
				objects.remove(obj);
				if(obj != null)
					obj.dispose(this);
			}
			synchronized(objRemove){
				objRemove.clear();
			}
		}
	}

	private long uNow;
	private ArrayList<GameObject> uCopy = new ArrayList<GameObject>();
	public void update(){
		if(gameThread == null){
			gameThread = Thread.currentThread();
			Log.i("Game", "Game is running on thread with ID: " + gameThread.getId());
		}

		if(DEBUG)
			Benchmark.start(Benchmarks.UPDATE);
		if(DEBUG_MEM)
			MemoryProfiler.start("u");

		uNow = System.currentTimeMillis();
		delta = uNow - time;
		time = uNow;

		scheduler.update(this);
		pp.update(this);

		synchronized(objects){
			flushObjects();
			uCopy.clear();
			uCopy.addAll(objects);
			for(GameObject obj : uCopy){
				if(battle != null)
					if(!(obj instanceof Dialog))
						continue;
				obj.update(this);
			}
		}

		if(battle != null)
			battle.update(this);

		windows.update(this);
		guis.update(this);

		input.update(this);

		if(DEBUG)
			updateTime = Benchmark.stop(Benchmarks.UPDATE);
		if(DEBUG_MEM){
			MemoryProfile profile = MemoryProfiler.end("u");
			if(time - lastMemProfileDump >= 1000){
				if(memDumpStream == null){
					try{
						memDumpStream = new FileOutputStream(new File(context.getFilesDir(), "memdump.txt"), true);
					}catch(IOException e){}
				}
				try{
					profile.dump(memDumpStream);
				}catch(IOException e){}
			}
		}
	}

	public void pushTranslate(Canvas canvas){
		if(USE_OPENGL)
			Matrix.translateM(rodit.getPVMatrix(), 0, scrollX, scrollY, 0f);
		else
			canvas.translate(scrollX, scrollY);
	}

	public void popTranslate(Canvas canvas){
		if(USE_OPENGL)
			Matrix.translateM(rodit.getPVMatrix(), 0, -scrollX, -scrollY, 0f);
		else
			canvas.translate(-scrollX, -scrollY);
	}

	public void draw(){
		if(USE_OPENGL)
			GLUtil.prepareFrame();

		if(skipFrames > 0){
			skipFrames--;
			return;
		}

		scrollX = (player == null ? 0 : player.getBounds().getX()) * SCALE_FACTOR - 1280f / 2f;
		scrollY = (player == null ? 0 : player.getBounds().getY()) * SCALE_FACTOR - 720f / 2f;

		if(battle != null)
			battle.draw(this, null);

		popTranslate(null);
		Matrix.scaleM(rodit.getPVMatrix(), 0, SCALE_FACTOR, SCALE_FACTOR, 1f);

		synchronized(objects){
			if(objectModCount > 0){
				Collections.sort(objects, GameObject.RenderLayer.RENDER_COMPARATOR);
				objectModCount = 0;
			}

			ArrayList<GameObject> copy = new ArrayList<GameObject>(objects);
			for(GameObject obj : copy){
				if(battle != null)
					if(!(obj instanceof Dialog))
						continue;
				if(!obj.shouldScale())
					Matrix.scaleM(rodit.getPVMatrix(), 0, SCALE_FACTOR_1, SCALE_FACTOR_1, 1f);

				obj.draw(this, null);

				if(!obj.shouldScale())
					Matrix.scaleM(rodit.getPVMatrix(), 0, SCALE_FACTOR, SCALE_FACTOR, 1f);
			}
		}

		pushTranslate(null);
		windows.draw(this, null);
		guis.draw(null, this);

		if(FRAME_SYNC){
			if(delta > 17L && delta < 33L){
				long diff = 33 - delta;
				try{
					Thread.sleep(diff);
				}catch(InterruptedException e){
					e.printStackTrace();
				}
			}
		}

		frameCounter++;
	}

	long lastFps = 0;
	private int lastFpsI = 0;
	private int frameCounter = 0;
	private Paint fpsPaint;
	private HashMap<String, Integer> dbgData = new HashMap<String, Integer>();
	private String dbgName;
	private long dObjDrawTime;
	private long dGuiDrawTime;
	private long dMemUsed;
	private long dLastMem;
	private long dSyncDiff;
	public void draw(Canvas canvas){
		if(DEBUG)
			Benchmark.start(Benchmarks.DRAW);
		if(DEBUG_MEM)
			MemoryProfiler.start("d");

		if(screenScaleX > 1f || screenScaleY > 1f)
			canvas.scale(screenScaleX, screenScaleY);

		canvas.drawColor(Color.BLACK);
		if(skipFrames > 0){
			skipFrames--;
			return;
		}

		pp.preDraw(this, canvas);

		scrollX = (player == null ? 0 : player.getBounds().getX()) * SCALE_FACTOR - 1280f / 2f;
		scrollY = (player == null ? 0 : player.getBounds().getY()) * SCALE_FACTOR - 720f / 2f;

		if(battle != null)
			battle.draw(this, canvas);

		popTranslate(canvas);

		canvas.scale(SCALE_FACTOR, SCALE_FACTOR);

		if(DEBUG)
			Benchmark.start(Benchmarks.DRAW_OBJ);
		synchronized(objects){
			if(objectModCount > 0){
				Collections.sort(objects, GameObject.RenderLayer.RENDER_COMPARATOR);
				objectModCount = 0;
			}

			uCopy.clear();
			uCopy.addAll(objects);
			for(GameObject obj : uCopy){
				if(battle != null)
					if(!(obj instanceof Dialog))
						continue;
				if(!obj.shouldScale())
					canvas.scale(SCALE_FACTOR_1, SCALE_FACTOR_1);

				dbgName = null;
				if(DEBUG_DRAW){
					dbgName = obj.getName();
					if(dbgName != null){
						if(!dbgData.containsKey(dbgName))
							dbgData.put(dbgName, 0);
						Benchmark.start("sprite_" + dbgName);
					}
				}

				obj.draw(this, canvas);

				if(DEBUG_DRAW){
					if(dbgName != null){
						long drawTime = Benchmark.stop("sprite_" + dbgName);
						int i = dbgData.get(dbgName);
						if(i % 60 == 0)
							System.out.println("Sprite draw (" + dbgName + "): " + drawTime + "ms");
						dbgData.put(dbgName, i + 1);
					}
				}

				if(!obj.shouldScale())
					canvas.scale(SCALE_FACTOR, SCALE_FACTOR);

				if(DEBUG_DRAW){
					Style style = obj.getPaint().getStyle();
					int color = obj.getPaint().getColor();
					float size = obj.getPaint().getTextSize();
					obj.getPaint().setStyle(Style.STROKE);
					obj.getPaint().setColor(Color.RED);
					canvas.drawRect(obj.getBounds().get(), obj.getPaint());
					if(obj instanceof Entity){
						obj.getPaint().setColor(Color.GREEN);
						obj.getPaint().setStyle(Style.FILL);
						canvas.drawRect(((Entity)obj).getCollisionBounds(), obj.getPaint());
					}
					if(obj.getName() != null){
						obj.getPaint().setColor(Color.BLUE);
						obj.getPaint().setTextSize(8f);
						canvas.drawText(obj.getName(), obj.getBounds().getX(), obj.getBounds().getY() - 8f, obj.getPaint());
					}
					obj.getPaint().setTextSize(size);
					obj.getPaint().setStyle(style);
					obj.getPaint().setColor(color);
				}
			}
		}
		if(DEBUG)
			dObjDrawTime = Benchmark.stop(Benchmarks.DRAW_OBJ);
		pp.draw(this, canvas);

		canvas.scale(SCALE_FACTOR_1, SCALE_FACTOR_1);

		pushTranslate(canvas);
		if(DEBUG)
			Benchmark.start(Benchmarks.DRAW_GUI);
		windows.draw(this, canvas);
		guis.draw(canvas, this);
		pp.postDraw(this, canvas);
		if(DEBUG){
			dGuiDrawTime = Benchmark.stop(Benchmarks.DRAW_GUI);
			if(fpsPaint == null){
				fpsPaint = new Paint(DEFAULT_PAINT);
				fpsPaint.setColor(Color.WHITE);
				fpsPaint.setTextAlign(Align.RIGHT);
				fpsPaint.setTextSize(24f);
			}

			if(time - lastFps >= 1000L){
				lastFps = time;
				lastFpsI = frameCounter;
				frameCounter = 0;
			}
			canvas.drawText("FPS: " + lastFpsI, 1280f, 24f, fpsPaint);
			dMemUsed = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
			canvas.drawText("Memory: " + (dMemUsed / 1024l) + "K" + " (" + ((double)(dMemUsed - dLastMem) / 1024d) + ")", 1280f, 40f, fpsPaint);
			canvas.drawText("Objects: " + objects.size(), 1280f, 56f, fpsPaint);
			BenchmarkClass ppDraw = Benchmark.getBenchmark(Benchmarks.DRAW_PP);
			drawTimes(canvas, fpsPaint, drawTime, new long[] { dObjDrawTime, dGuiDrawTime, ppDraw != null ? ppDraw.getTime() : 0L });
			canvas.drawText("X: " + player.getBounds().getX() + " Y:" + player.getBounds().getY(), 1280f, 100f, fpsPaint);
			dLastMem = dMemUsed;
		}

		if(FRAME_SYNC){
			if(delta > 17L && delta < 33L){
				dSyncDiff = 33 - delta;
				try{
					Thread.sleep(dSyncDiff);
				}catch(InterruptedException e){
					e.printStackTrace();
				}
			}
		}

		frameCounter++;

		if(screenScaleX > 1f || screenScaleY > 1f)
			canvas.scale(1f / screenScaleX, 1f / screenScaleY);

		if(DEBUG)
			drawTime = Benchmark.stop(Benchmarks.DRAW);
		if(DEBUG_MEM){
			MemoryProfile profile = MemoryProfiler.end("d");
			if(time - lastMemProfileDump >= 1000){
				lastMemProfileDump = time;
				try{
					profile.dump(memDumpStream);
				}catch(IOException e){}
			}
		}
	}
	private FileOutputStream memDumpStream;
	private long lastMemProfileDump;

	private long dtHighest;
	private int dtHighestIndex;
	private void drawTimes(Canvas canvas, Paint paint, long drawTime, long[] times){
		dtHighest = 0l;
		dtHighestIndex = 0;
		for(int i = 0; i < times.length; i++){
			if(times[i] > dtHighest){
				dtHighest = times[i];
				dtHighestIndex = i;
			}
		}
		String toDraw = "Time: " + drawTime + "ms, ";
		for(int i = 0; i < times.length; i++)
			toDraw += times[i] + "ms, ";
		toDraw += " H:" + dtHighest + "ms, " + dtHighestIndex + "i";
		canvas.drawText(toDraw, 1280f, 72f, paint);
	}

	private PointF iScaledPoint = new PointF(1f, 1f);
	public void input(MotionEvent event){
		screen.scaleInput(event.getX(), event.getY(), iScaledPoint);
		event.setLocation(iScaledPoint.x, iScaledPoint.y);

		windows.touchInput(this, event);
		guis.input(event, this);
	}

	public void keyInput(KeyEvent event){
		windows.keyInput(this, event);
		guis.keyInput(event, this);
	}

	public FileInputStream openLocalRead(String file)throws IOException{
		return context.openFileInput(file);
	}

	public FileOutputStream openLocal(String file)throws IOException{
		return context.openFileOutput(file, Context.MODE_PRIVATE);
	}

	public void writeLocal(String file, byte[] data){
		try{
			FileOutputStream fout = context.openFileOutput(file, Context.MODE_PRIVATE);
			fout.write(data);
			fout.flush();
			fout.close();
		}catch(IOException e){
			e.printStackTrace();
		}
	}

	public byte[] readLocal(String file){
		try{
			FileInputStream fin = context.openFileInput(file);
			byte[] data = new byte[fin.available()];
			fin.read(data);
			fin.close();
			return data;
		}catch(IOException e){
			e.printStackTrace();
		}
		return null;
	}

	private volatile int skipFrames;
	public void skipFrames(int frames){
		skipFrames += frames;
	}

	@Override
	public void onOptionChanged(String key, String oldValue, String newValue){
		if(key.equals(Options.USE_HARDWARE_RENDER)){
			if(Util.tryGetBool(newValue))
				view.setLayerType(View.LAYER_TYPE_HARDWARE, null);
			else
				view.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
		}else if(key.equals(Options.BACKGROUND_MUSIC)){
			if(Util.tryGetBool(newValue))
				audio.unpauseAll();
			else
				audio.pauseAll();
		}
	}

	public void load(TinyInputStream in)throws IOException{
		byte[] sig = in.read(FILE_SIGNATURE.length);
		if(!Arrays.equals(sig, FILE_SIGNATURE))
			throw new IOException("Invalid save file signature.");
		String versionStr = in.readString();
		Log.i("Load", "Save version: " + versionStr);
		if(!versionStr.equals(VERSION))
			Log.w("Load", "Attempting to load save with version " + versionStr + " when game version is " + VERSION + ".");
		GlobalSerializer.deserialize(globals, in);
		player.load(this, in);
		String mapFileName = in.readString();
		switchMap(mapFileName);//, true);
		scheduler.load(in);
		quests.load(in);
	}

	public void save(TinyOutputStream out)throws IOException{
		out.write(FILE_SIGNATURE);
		out.writeString(VERSION);
		GlobalSerializer.serialize(globals, out);
		player.save(out);
		if(map != null && map.getMap() != null)
			out.writeString(map.getMap().getFile());
		else
			out.writeString("");
		saves.saveMap(this, map);
		scheduler.save(out);
		quests.save(out);
	}

	public boolean isGameThread(){
		return Thread.currentThread() == gameThread;
	}
}
