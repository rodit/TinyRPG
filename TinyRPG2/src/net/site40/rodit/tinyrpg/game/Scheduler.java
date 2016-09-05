package net.site40.rodit.tinyrpg.game;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import net.site40.rodit.util.TinyInputStream;
import net.site40.rodit.util.TinyOutputStream;

public class Scheduler {
	
	private ArrayList<ScheduledEvent> addQueue;
	private ArrayList<ScheduledEvent> removeQueue;

	private ArrayList<ScheduledEvent> events;

	public Scheduler(){
		this.addQueue = new ArrayList<ScheduledEvent>();
		this.removeQueue = new ArrayList<ScheduledEvent>();

		this.events = new ArrayList<ScheduledEvent>();
	}

	public ArrayList<ScheduledEvent> getEvents(){
		return events;
	}

	public ScheduledEvent schedule(Runnable runnable, long time, long delay){
		ScheduledEvent event = new ScheduledEvent(runnable, time, delay);
		schedule(event);
		return event;
	}

	public void schedule(ScheduledEvent event){
		addQueue.add(event);
	}

	public void unschedule(ScheduledEvent event){
		removeQueue.add(event);
	}
	
	public void update(final Game game){
		synchronized(addQueue){
			for(ScheduledEvent event : addQueue)
				if(!events.contains(event))
					events.add(event);
			addQueue.clear();
		}
		
		Iterator<ScheduledEvent> eventIt = events.iterator();
		while(eventIt.hasNext()){
			ScheduledEvent event = eventIt.next();
			if(event.shouldRun(game.getTime())){
				removeQueue.add(event);
				event.getRunnable().run();
			}
		}
		
		synchronized(removeQueue){
			events.removeAll(removeQueue);
			removeQueue.clear();
		}
	}

	public static class ScheduledEvent {
		
		private long startTime;
		private long delay;
		private Runnable runnable;

		public ScheduledEvent(Runnable runnable, long startTime, long delay){
			this.runnable = runnable;
			this.startTime = startTime;
			this.delay = delay;
		}

		public boolean shouldRun(long time){
			return time - startTime >= delay;
		}

		public Runnable getRunnable(){
			return runnable;
		}
	}
	
	public void load(TinyInputStream in)throws IOException{
		int count = in.readInt();
		//TODO: make this work...
	}
	
	public void save(TinyOutputStream out)throws IOException{
		out.write(events.size());
		//TODO: make this work...
	}
}
