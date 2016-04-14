package net.site40.rodit.tinyrpg.game;

import java.util.ArrayList;
import java.util.Iterator;

public class Scheduler {

	private ArrayList<ScheduledEvent> events;

	public Scheduler(){
		this.events = new ArrayList<ScheduledEvent>();
	}

	public ArrayList<ScheduledEvent> getEvents(){
		return events;
	}

	public void schedule(Runnable runnable, long time, long delay){
		ScheduledEvent event = new ScheduledEvent(runnable, time, delay);
		schedule(event);
	}

	public void schedule(ScheduledEvent event){
		events.add(event);
	}

	public void unschedule(ScheduledEvent event){
		events.remove(event);
	}

	public void update(final Game game){
		ArrayList<ScheduledEvent> remove = new ArrayList<ScheduledEvent>();
		synchronized(events){
			Iterator<ScheduledEvent> eventIt = events.iterator();
			while(eventIt.hasNext()){
				ScheduledEvent event = eventIt.next();
				if(event.shouldRun(game.getTime())){
					remove.add(event);
					event.getRunnable().run();
				}
			}
			events.removeAll(remove);
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
}
