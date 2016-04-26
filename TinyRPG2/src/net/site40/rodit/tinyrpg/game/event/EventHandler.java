package net.site40.rodit.tinyrpg.game.event;

import java.util.ArrayList;

import net.site40.rodit.tinyrpg.game.Game;
import net.site40.rodit.tinyrpg.game.event.EventReceiver.EventType;

public class EventHandler {

	private ArrayList<EventReceiver> addQueue;
	private ArrayList<EventReceiver> removeQueue;
	private ArrayList<EventReceiver> receivers;
	
	public EventHandler(){
		this.addQueue = new ArrayList<EventReceiver>();
		this.removeQueue = new ArrayList<EventReceiver>();
		this.receivers = new ArrayList<EventReceiver>();
	}
	
	public ArrayList<EventReceiver> getReceivers(){
		return receivers;
	}
	
	public void add(EventReceiver receiver){
		synchronized(addQueue){
			addQueue.add(receiver);
		}
	}
	
	public void remove(EventReceiver receiver){
		synchronized(removeQueue){
			removeQueue.add(receiver);
		}
	}
	
	public void onEvent(Game game, EventType type, Object... args){
		synchronized(addQueue){
			for(EventReceiver receiver : addQueue)
				if(!receivers.contains(receiver))
					receivers.add(receiver);
		}
		
		synchronized(removeQueue){
			for(EventReceiver receiver : removeQueue)
				receivers.remove(receiver);
		}
		
		for(EventReceiver receiver : receivers)
			if(receiver.getType() == type)
				receiver.onEvent(game, args);
	}
}
