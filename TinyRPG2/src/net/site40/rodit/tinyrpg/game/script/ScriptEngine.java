package net.site40.rodit.tinyrpg.game.script;

import java.util.ArrayList;
import java.util.HashMap;

import net.site40.rodit.tinyrpg.game.Game;

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
		long time = System.currentTimeMillis();
		
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
			Log.d("ScriptEngine", "Script execution took " + (System.currentTimeMillis() - time) + "ms.");
		}
		return null;
	}
	
	public Object execute(Game game, String scriptPath, String[] varNames, Object[] varVals){
		long time = System.currentTimeMillis();
		Context cx2 = ContextFactory.getGlobal().enterContext();
		cx2.setOptimizationLevel(-1);
		
		if(!scriptCache.containsKey(scriptPath)){
			String script = game.getResources().getString(scriptPath);
			Script scr = cx2.compileString(script, "", 1, null);
			scriptCache.put(scriptPath, scr);
		}
		
		Script s = scriptCache.get(scriptPath);
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
			Log.d("ScriptEngine", "Script execution took " + (System.currentTimeMillis() - time) + "ms.");
		}
		return null;
	}

	public void executeAsync(final Game game, final String script, final String[] varNames, final Object[] varVals, final int delay){
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
				execute(game, script, varNames, varVals);
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
