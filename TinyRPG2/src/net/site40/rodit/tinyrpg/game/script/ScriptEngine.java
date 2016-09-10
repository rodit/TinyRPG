package net.site40.rodit.tinyrpg.game.script;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import net.site40.rodit.tinyrpg.game.Benchmark;
import net.site40.rodit.tinyrpg.game.Game;
import net.site40.rodit.util.GenericCallback.ObjectCallback;
import net.site40.rodit.util.Util;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextFactory;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Script;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

import android.util.Log;

public class ScriptEngine {

	private ArrayList<Thread> threads = new ArrayList<Thread>();
	private HashMap<String, Script> scriptCache = new HashMap<String, Script>();

	public Object executeFunction(Game game, Function function, Object thisObj, String[] varNames, Object[] varVals, Object[] vars){
		Benchmark.start("sf_" + function);

		Context cx = ContextFactory.getGlobal().enterContext();
		try{
			Scriptable scope = cx.initStandardObjects();

			for(int i = 0; i < varNames.length; i++)
				ScriptableObject.putProperty(scope, varNames[i], Context.javaToJS(varVals[i], scope));

			for(String key : game.getGlobals().keySet())
				ScriptableObject.putProperty(scope, key, game.getGlobal(key));

			return function.call(cx, scope, (Scriptable)Context.javaToJS(thisObj, scope), vars);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			Context.exit();
			Log.d("ScriptEngine", "Script function execution took " + Benchmark.stop("sf_" + function) + "ms.");
		}
		return null;
	}

	public void executeAsyncFunction(final Game game, final Function function, final Object thisObj, final String[] varNames, final Object[] varVals, final Object[] vars, final ObjectCallback<Object> callback){
		new Thread(){
			@Override
			public void run(){
				Object result = executeFunction(game, function, thisObj, varNames, varVals, vars);
				if(callback != null)
					callback.callback(result);
			}
		}.start();
	}

	public Object execute(Game game, String scriptPath, String[] varNames, Object[] varVals){
		return execute(game, scriptPath, varNames, varVals, false);
	}

	public Object execute(Game game, String scriptPath, String[] varNames, Object[] varVals, boolean localFile){
		if(Game.DEBUG)
			Benchmark.start("se_" + scriptPath);
		Context cx2 = ContextFactory.getGlobal().enterContext();
		cx2.setOptimizationLevel(-1);

		if(!localFile){
			if(!scriptCache.containsKey(scriptPath)){
				String script = game.getResources().getString(scriptPath);
				Script scr = cx2.compileString(script, "", 1, null);
				scriptCache.put(scriptPath, scr);
			}
		}else{
			if(!scriptCache.containsKey("local_" + scriptPath)){
				String script = new String(Util.readAll(Util.openFile(new File(game.getContext().getCacheDir(), "script_cache/" + scriptPath))));
				Script scr = cx2.compileString(script, "", 1, null);
				scriptCache.put("local_" + scriptPath, scr);
			}
		}

		Script s = localFile ? scriptCache.get("local_" + scriptPath) : scriptCache.get(scriptPath);
		Context cx = ContextFactory.getGlobal().enterContext();
		try{
			Scriptable scope = cx.initStandardObjects();

			for(int i = 0; i < varNames.length; i++)
				ScriptableObject.putProperty(scope, varNames[i], Context.javaToJS(varVals[i], scope));

			for(String key : game.getGlobals().keySet())
				ScriptableObject.putProperty(scope, key, game.getGlobal(key));

			return s.exec(cx, scope);
			//return cx.evaluateString(scope, script, func, 1, null);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			Context.exit();
			if(Game.DEBUG)
				Log.d("ScriptEngine", "Script execution took " + Benchmark.stop("se_" + scriptPath) + "ms.");
		}
		return null;
	}

	public void executeAsync(final Game game, final String script, final String[] varNames, final Object[] varVals, final int delay, final ObjectCallback<Object> callback){
		Thread t = new Thread(){
			@Override
			public void run(){
				if(delay != 0){
					try{
						Thread.sleep(delay);
					}catch(InterruptedException e){
						e.printStackTrace();
					}
				}
				Object result = execute(game, script, varNames, varVals);
				if(callback != null)
					callback.callback(result);
				threads.remove(this);
			}
		};
		threads.add(t);
		t.start();
	}

	@SuppressWarnings("deprecation")
	public void clean(){
		for(Thread t : threads){
			if(t.isAlive())
				t.stop();
			threads.remove(t);
		}
	}
}
