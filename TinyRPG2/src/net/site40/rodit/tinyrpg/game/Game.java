package net.site40.rodit.tinyrpg.game;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;

import net.site40.rodit.tinyrpg.game.Benchmark.BenchmarkClass;
import net.site40.rodit.tinyrpg.game.audio.AudioManager;
import net.site40.rodit.tinyrpg.game.battle.Battle;
import net.site40.rodit.tinyrpg.game.chat.Chat;
import net.site40.rodit.tinyrpg.game.entity.Entity;
import net.site40.rodit.tinyrpg.game.entity.EntityPlayer;
import net.site40.rodit.tinyrpg.game.entity.npc.EntityNPC;
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
import net.site40.rodit.tinyrpg.game.quest.QuestManager;
import net.site40.rodit.tinyrpg.game.render.ResourceManager;
import net.site40.rodit.tinyrpg.game.render.ResourceStreamProvider.AssetStreamProvider;
import net.site40.rodit.tinyrpg.game.render.ResourceStreamProvider.ModStreamProvider;
import net.site40.rodit.tinyrpg.game.render.Sprite;
import net.site40.rodit.tinyrpg.game.render.XmlResourceLoader;
import net.site40.rodit.tinyrpg.game.render.effects.DayNightCycle;
import net.site40.rodit.tinyrpg.game.render.effects.Lighting;
import net.site40.rodit.tinyrpg.game.render.effects.PostProcessor;
import net.site40.rodit.tinyrpg.game.render.effects.Weather;
import net.site40.rodit.tinyrpg.game.saves.Options;
import net.site40.rodit.tinyrpg.game.saves.SaveManager;
import net.site40.rodit.tinyrpg.game.script.CutseneHelper;
import net.site40.rodit.tinyrpg.game.script.ScriptEngine;
import net.site40.rodit.tinyrpg.game.script.ScriptHelper;
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
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;

public class Game implements Savable{

	public static final byte[] FILE_SIGNATURE = new byte[] { 7, 1, 5, 7 };
	public static final String VERSION = "TinyRPG0";

	private static Paint DEFAULT_PAINT;
	public static Paint getDefaultPaint(){
		return new Paint(DEFAULT_PAINT);
	}

	public static final boolean DEBUG = true;
	public static final boolean DEBUG_DRAW = true;

	public static final float SCALE_FACTOR = 2.5f;
	public static final float SCALE_FACTOR_1 = 1f / SCALE_FACTOR;

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
	private ArrayList<IGameObject> objects;
	private ArrayList<IGameObject> objRemove;
	private ArrayList<Dialog> dialogs;
	private Input input;
	private ScriptEngine scripts;
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
	private Lighting lighting;
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

	public Game(Context context, View view){
		gameThread = Thread.currentThread();
		Log.i("Game", "Game is running on thread with ID: " + gameThread.getId());

		Display display = ((Activity)context).getWindowManager().getDefaultDisplay();
		Point point = new Point();
		display.getSize(point);
		screenScaleX = point.x / 1280f;
		screenScaleY = point.y / 720f;

		if(DEBUG){
			Benchmark.start("init");
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
		this.objects = new ArrayList<IGameObject>();
		this.objRemove = new ArrayList<IGameObject>();
		this.dialogs = new ArrayList<Dialog>();
		this.input = new Input();
		this.scripts = new ScriptEngine();
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
		//ENTITY VARS
		globals.put("merek_speak_count", "0");
		globals.put("test_dialog_index", "0");
		
		this.saves = new SaveManager(this);
		this.chat = new Chat();
		this.pp = new PostProcessor();
		this.lighting = new Lighting();
		this.weather = new Weather();
		pp.add(lighting);
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

		scripts.execute(this, "script/init/start.js", new String[0], new Object[0]);

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

		if(DEBUG)
			Log.i("Benchmark", "Game initialization took " + Benchmark.stop("init") + "ms.");
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
		return tracer.trace(map, e, 4f);
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

	public ArrayList<IGameObject> getObjects(){
		return objects;
	}

	public Sprite getSprite(String name){
		for(IGameObject obj : objects)
			if(obj instanceof Sprite && ((Sprite)obj).getName().equals(name))
				return (Sprite)obj;
		return null;
	}

	public void addObject(IGameObject object){
		if(object == null){
			new Exception().printStackTrace();
			return;
		}
		if(object instanceof EntityNPC){
			Log.i("NPC", "NPC ADDED");
		}
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

	public void removeObject(IGameObject object){
		if(object == null)
			return;
		synchronized(objRemove){
			objRemove.add(object);
			objectModCount++;
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

	public ScriptEngine getScripts(){
		return scripts;
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

	public void setBattle(Battle battle){
		this.battle = battle;
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

	public Lighting getLighting(){
		return lighting;
	}

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

	public void switchMap(String mapName){
		RPGMap loadMap = (RPGMap)resources.getObject(mapName);
		if(loadMap == null){
			Log.e("Game", "Failed to load map: " + mapName + ". Resource was probably not found.");
			return;
		}
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

	public void showMessage(String message, Gui returnGui){
		GuiMessage msgGui = (GuiMessage)guis.get(GuiMessage.class);
		msgGui.get("txtMessage").setText(message);
		setGlobal("gui_message_return", returnGui);
		guis.hide(returnGui);
		guis.show(GuiMessage.class);
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
	
	public void flushObjects(){
		synchronized(objects){
			for(IGameObject obj : objRemove){
				objects.remove(obj);
				if(obj != null)
					obj.dispose(this);
			}
			synchronized(objRemove){
				objRemove.clear();
			}
		}
	}

	public void update(){
		if(DEBUG)
			Benchmark.start("update");

		long now = System.currentTimeMillis();
		delta = now - time;
		time = now;

		scheduler.update(this);
		pp.update(this);
		
		synchronized(objects){
			flushObjects();
			ArrayList<IGameObject> copy = new ArrayList<IGameObject>(objects);
			for(IGameObject obj : copy){
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
			updateTime = Benchmark.stop("update");
	}

	public void pushTranslate(Canvas canvas){
		canvas.translate(scrollX, scrollY);
	}

	public void popTranslate(Canvas canvas){
		canvas.translate(-scrollX, -scrollY);
	}

	long lastFps = 0;
	private int lastFpsI = 0;
	private int frameCounter = 0;
	private Paint fpsPaint;
	public void draw(Canvas canvas){
		if(DEBUG)
			Benchmark.start("draw");

		if(screenScaleX > 1f || screenScaleY > 1f)
			canvas.scale(screenScaleX, screenScaleY);

		canvas.drawColor(Color.BLACK);
		if(skipFrames > 0){
			skipFrames--;
			return;
		}
		
		pp.preDraw(this, canvas);

		scrollX = (player == null ? 0 : player.getX()) * SCALE_FACTOR - 1280f / 2f;
		scrollY = (player == null ? 0 : player.getY()) * SCALE_FACTOR - 720f / 2f;

		if(battle != null)
			battle.draw(this, canvas);

		popTranslate(canvas);

		canvas.scale(SCALE_FACTOR, SCALE_FACTOR);

		if(DEBUG)
			Benchmark.start("draw_objects");
		synchronized(objects){
			if(objectModCount > 0){
				Collections.sort(objects, IGameObject.RENDER_COMPARATOR);
				objectModCount = 0;
			}

			ArrayList<IGameObject> copy = new ArrayList<IGameObject>(objects);
			for(IGameObject obj : copy){
				if(battle != null)
					if(!(obj instanceof Dialog))
						continue;
				if(!obj.shouldScale())
					canvas.scale(SCALE_FACTOR_1, SCALE_FACTOR_1);
				obj.draw(this, canvas);
				if(!obj.shouldScale())
					canvas.scale(SCALE_FACTOR, SCALE_FACTOR);

				if(DEBUG_DRAW){
					if(obj instanceof Sprite){
						Sprite sprite = (Sprite)obj;
						Style style = obj.getPaint().getStyle();
						int color = obj.getPaint().getColor();
						float size = sprite.getPaint().getTextSize();
						obj.getPaint().setStyle(Style.STROKE);
						obj.getPaint().setColor(Color.RED);
						canvas.drawRect(sprite.getBounds(), obj.getPaint());
						sprite.getPaint().setColor(Color.BLUE);
						sprite.getPaint().setTextSize(8f);
						canvas.drawText(sprite.getName(), sprite.getX(), sprite.getY() - 8f, sprite.getPaint());
						sprite.getPaint().setTextSize(size);
						obj.getPaint().setStyle(style);
						obj.getPaint().setColor(color);
					}
				}
			}
		}
		long objDrawTime = 0L;
		if(DEBUG)
			objDrawTime = Benchmark.stop("draw_objects");
		pp.draw(this, canvas);

		canvas.scale(SCALE_FACTOR_1, SCALE_FACTOR_1);

		pushTranslate(canvas);
		if(DEBUG)
			Benchmark.start("draw_gui");
		windows.draw(this, canvas);
		guis.draw(canvas, this);
		pp.postDraw(this, canvas);
		long guiDrawTime = 0l;
		if(DEBUG){
			guiDrawTime = Benchmark.stop("draw_gui");
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
			long memUsed = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
			canvas.drawText("Memory: " + (memUsed / 1024l) + "/" + Runtime.getRuntime().maxMemory() + "KB", 1280f, 40f, fpsPaint);
			canvas.drawText("Objects: " + objects.size(), 1280f, 56f, fpsPaint);
			BenchmarkClass ppDraw = Benchmark.getBenchmark("draw_pp");
			drawTimes(canvas, fpsPaint, drawTime, new long[] { objDrawTime, guiDrawTime, ppDraw != null ? ppDraw.getTime() : 0L });
		}

		if(delta > 17L && delta < 33L){
			long diff = 33 - delta;
			try{
				Thread.sleep(diff);
			}catch(InterruptedException e){
				e.printStackTrace();
			}
		}

		frameCounter++;

		if(screenScaleX > 1f || screenScaleY > 1f)
			canvas.scale(1f / screenScaleX, 1f / screenScaleY);

		if(DEBUG)
			drawTime = Benchmark.stop("draw");
	}

	private void drawTimes(Canvas canvas, Paint paint, long drawTime, long[] times){
		long highest = 0l;
		int highestIndex = 0;
		for(int i = 0; i < times.length; i++){
			if(times[i] > highest){
				highest = times[i];
				highestIndex = i;
			}
		}
		String toDraw = "Time: " + drawTime + "ms, ";
		for(int i = 0; i < times.length; i++)
			toDraw += times[i] + "ms, ";
		toDraw += " H:" + highest + "ms, " + highestIndex + "i";
		canvas.drawText(toDraw, 1280f, 72f, paint);
	}

	public void input(MotionEvent event){
		PointF scaled = screen.scaleInput(event.getX(), event.getY());
		event.setLocation(scaled.x, scaled.y);

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
