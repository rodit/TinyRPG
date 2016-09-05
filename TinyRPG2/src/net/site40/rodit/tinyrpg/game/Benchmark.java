package net.site40.rodit.tinyrpg.game;

import java.util.HashMap;

import android.util.Log;

public class Benchmark {
	
	private static HashMap<String, BenchmarkClass> benchmarks = new HashMap<String, BenchmarkClass>();
	
	public static BenchmarkClass getBenchmark(String name){
		return benchmarks.get(name);
	}
	
	public static void start(String name){
		BenchmarkClass cls = getBenchmark(name);
		if(cls == null)
			benchmarks.put(name, cls = new BenchmarkClass(name));
		cls.setStart(System.currentTimeMillis());
	}
	
	public static long stop(String name){
		BenchmarkClass cls = getBenchmark(name);
		if(cls == null)
			Log.e("Benchmark", "Failed to stop benchmark " + name + ".");
		cls.setEnd(System.currentTimeMillis());
		return cls.getTime();
	}
	
	public static class BenchmarkClass{
		
		protected String name;
		protected long start;
		protected long end;
		
		public BenchmarkClass(String name){
			this.name = name;
		}
		
		public void setStart(long start){
			this.start = start;
		}
		
		public void setEnd(long end){
			this.end = end;
		}
		
		public long getTime(){
			return end - start;
		}
	}
}
