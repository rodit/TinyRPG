package net.site40.rodit.tinyrpg.game.script;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import net.site40.rodit.tinyrpg.game.Benchmark;
import net.site40.rodit.tinyrpg.game.Game;
import net.site40.rodit.tinyrpg.game.event.EventReceiver.EventType;
import net.site40.rodit.tinyrpg.game.render.Strings.Benchmarks;
import net.site40.rodit.tinyrpg.game.render.Strings.GameData;
import net.site40.rodit.util.Util;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextFactory;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Script;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

import android.text.TextUtils;
import android.util.Log;

import com.faendir.rhino_android.RhinoAndroidHelper;

public class ScriptManager {

	public static boolean USE_RHINO_MOD = true;
	private static final String LOCAL_SCRIPT_PATH = "script_cache";

	private HashMap<String, Script> scriptCache = new HashMap<String, Script>();

	private RhinoAndroidHelper helper;
	private Context scriptContext;
	private Scriptable scriptScope;

	public boolean insideFunction = false;

	public ScriptManager(Game game){
		this.scriptCache = new HashMap<String, Script>();
		this.helper = new RhinoAndroidHelper(game.getContext());
		this.scriptContext = USE_RHINO_MOD ? helper.enterContext() : ContextFactory.getGlobal().enterContext();
		scriptContext.setOptimizationLevel(USE_RHINO_MOD ? 1 : -1);
		this.scriptScope = scriptContext.initStandardObjects();
	}

	public void initializeGlobals(Game game){
		ScriptableObject.putProperty(scriptScope, "game", game);
		ScriptableObject.putProperty(scriptScope, "helper", game.getHelper());
		ScriptableObject.putProperty(scriptScope, "cutsene", new CutseneHelper());
		ScriptableObject.putProperty(scriptScope, "quests", game.getQuests());
		ScriptableObject.putProperty(scriptScope, "util", new Util());
		ScriptableObject.putProperty(scriptScope, "audio", game.getAudio());
		ScriptableObject.putProperty(scriptScope, "sfx", game.getSfx());
	}

	private Scriptable copyScope(){
		Scriptable scope = scriptContext.newObject(scriptScope);
		scope.setPrototype(scriptScope);
		scope.setParentScope(null);
		return scope;
	}

	private File getCachedLocal(Game game, String res){
		return new File(game.getContext().getCacheDir(), LOCAL_SCRIPT_PATH + "/" + res);
	}

	private Script compileScript(Game game, String res, boolean local){
		String scriptSource = "";
		if(local){
			File localFile = getCachedLocal(game, res);
			if(localFile.exists()){
				try{
					InputStream in = Util.openFile(localFile);
					scriptSource = new String(Util.readAll(in));
					in.close();
				}catch(IOException e){
					Log.e("ScriptManager", "Error while compiling local script - " + e.getMessage());
					if(Game.DEBUG)
						e.printStackTrace();
				}
			}
		}else{
			byte[] assetDat = game.getResources().readAsset(res);
			if(assetDat != null)
				scriptSource = new String(assetDat);
			else{
				Log.e("ScriptManager", "Failed to read script from assets " + res + ".");
				return null;
			}
		}
		if(!TextUtils.isEmpty(scriptSource)){
			String localRes = (local ? "local:" : "") + res;
			Script compiled = scriptContext.compileString(scriptSource, localRes, 1, null);
			scriptCache.put(localRes, compiled);
			return compiled;
		}else
			Log.w("ScriptManager", "No script to compile at " + res + " local=" + local);
		return null;
	}

	private Script getCachedScript(Game game, String res, boolean local){
		Script script = scriptCache.get((local ? "local:" : "") + res);
		if(script == null)
			script = compileScript(game, res, local);
		return script;
	}

	public void cacheScript(Game game, String res, boolean local){
		getCachedScript(game, res, local);
	}

	public Object runScript(Game game, String res, KVP<?>... vars){
		return runScript(game, res, false, vars);
	}

	public Object runScript(Game game, String res, boolean local, KVP<?>... vars){
		if(Game.DEBUG)
			Benchmark.start(Benchmarks.SCRIPT_EXEC + "_" + res);

		Object ret = null;

		Scriptable scope = copyScope();

		for(int i = 0; vars != null && i < vars.length; i++){
			ScriptableObject.putProperty(scope, vars[i].getName(), vars[i].getValue());
		}

		try{
			if(USE_RHINO_MOD){
				ret = getCachedScript(game, res, local).exec(scriptContext, scope);
			}else{
				Script run = getCachedScript(game, res, local);
				if(run == null){
					Log.e("ScriptManager", "Script not found " + res + " local=" + local + ".");
					return null;
				}
				ret = run.exec(scriptContext, scriptScope);
			}
		}catch(Exception e){
			Log.e("ScriptManager", "Error while running script - " + e.getMessage());
			if(Game.DEBUG)
				e.printStackTrace();
		}finally{
			if(Game.DEBUG)
				Log.d("ScriptManager", "Script execution took " + Benchmark.stop(Benchmarks.SCRIPT_EXEC + "_" + res) + "ms.");
		}
		return ret;
	}

	private ConcurrentLinkedQueue<FunctionRunData> funcQueue = new ConcurrentLinkedQueue<FunctionRunData>();
	public Object runFunction(Game game, Function function, Object self, KVP<?>[] vars, Object... args){
		if(insideFunction){
			funcQueue.offer(new FunctionRunData(function, self, vars, args));
			return null;
		}
		if(Game.DEBUG)
			Benchmark.start(Benchmarks.SCRIPT_FUNCTION);

		insideFunction = true;
		Object ret = null;

		Scriptable scope = copyScope();

		for(int i = 0; vars != null && i < vars.length; i++){
			ScriptableObject.putProperty(scope, vars[i].getName(), vars[i].getValue());
		}

		if(args == null)
			args = GameData.EMPTY_ARRAY;

		if(USE_RHINO_MOD)
			ScriptableObject.putProperty(scope, "self", self);

		try{
			ret = function.call(scriptContext, scope, (Scriptable)Context.javaToJS(self, scope), args);
		}catch(Exception e){
			Log.e("ScriptManager", "Error while running function - " + e.getMessage());
			if(Game.DEBUG)
				e.printStackTrace();
		}finally{
			Log.d("ScriptManager", "Script function execution took " + Benchmark.stop(Benchmarks.SCRIPT_FUNCTION) + "ms.");
		}
		insideFunction = false;
		if(funcQueue.size() > 0){
			FunctionRunData func = funcQueue.poll();
			runFunction(game, func.function, func.self, func.vars, func.args);
		}
		game.getEvents().onEvent(game, EventType.SCRIPT_COMPLETE, function);
		return ret;
	}

	public static class FunctionRunData{

		public Function function;
		public Object self;
		public KVP<?>[] vars;
		public Object[] args;

		public FunctionRunData(Function function, Object self, KVP<?>[] vars, Object[] args){
			this.function = function;
			this.self = self;
			this.vars = vars;
			this.args = args;
		}
	}

	public static class KVP<T>{

		public static final KVP<?>[] EMPTY = new KVP<?>[0];

		private String name;
		private T value;

		public KVP(String name, T value){
			this.name = name;
			this.value = value;
		}

		public String getName(){
			return name;
		}

		public void setName(String name){
			this.name = name;
		}

		public T getValue(){
			return value;
		}

		public void setValue(T value){
			this.value = value;
		}

		public static KVP<?>[] get(String[] keys, Object[] vals){
			KVP<?>[] kvps = new KVP<?>[keys.length];
			for(int i = 0; i < keys.length && i < vals.length; i++)
				kvps[i] = new KVP<Object>(keys[i], vals[i]);
			return kvps;
		}
	}
}
