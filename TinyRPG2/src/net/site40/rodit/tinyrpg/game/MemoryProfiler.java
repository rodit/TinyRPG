package net.site40.rodit.tinyrpg.game;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;

public class MemoryProfiler {

	private static Runtime runtime = Runtime.getRuntime();
	
	private static HashMap<String, MemoryProfile> profiles = new HashMap<String, MemoryProfile>();
	
	public static MemoryProfile start(String name){
		MemoryProfile profile = profiles.get(name);
		if(profile == null)
			profiles.put(name, profile = new MemoryProfile(name, runtime));
		profile.start();
		return profile;
	}
	
	public static MemoryProfile end(String name){
		MemoryProfile profile = profiles.get(name);
		profile.end();
		return profile;
	}
	
	public static MemoryProfile get(String name){
		return profiles.get(name);
	}
	
	public static class MemoryProfile{
		
		protected Runtime runtime;
		
		protected String name;
		protected long memStart;
		protected long memEnd;
		
		public MemoryProfile(String name, Runtime runtime){
			this.name = name;
			this.runtime = runtime;
		}
		
		public void start(){
			memStart = runtime.maxMemory() - runtime.freeMemory();
		}
		
		public void end(){
			memEnd = runtime.maxMemory() - runtime.freeMemory();
		}
		
		public void dump(OutputStream out)throws IOException{
			out.write(("\n" + name + " START: " + memStart + "B END: " + memEnd + "B DIFF:" + (memEnd - memStart) + "B").getBytes());
		}
	}
}
