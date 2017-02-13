package net.site40.rodit.tinyrpg.game;

import net.site40.rodit.tinyrpg.game.event.EventReceiver.EventType;
import android.util.SparseArray;

public class Input {

	public static final int KEY_UP = 0;
	public static final int KEY_DOWN = 1;
	public static final int KEY_LEFT = 2;
	public static final int KEY_RIGHT = 3;
	public static final int KEY_ACTION = 4;
	public static final int KEY_MENU = 5;

	private SparseArray<KeyState> keys;
	private boolean allowMovement;
	
	public Input(){
		this.keys = new SparseArray<KeyState>();
		keys.put(KEY_UP, new KeyState());
		keys.put(KEY_DOWN, new KeyState());
		keys.put(KEY_LEFT, new KeyState());
		keys.put(KEY_RIGHT, new KeyState());
		keys.put(KEY_ACTION, new KeyState());
		keys.put(KEY_MENU, new KeyState());
	}

	public void setDown(Game game, int key){
		KeyState state = keys.get(key);
		state.state = KeyState.STATE_DOWN;
		state.stateChangeTime = game.getTime();
		game.getEvents().onEvent(game, EventType.KEY_DOWN, key);
	}

	public void setUp(Game game, int key){
		KeyState state = keys.get(key);
		state.state = KeyState.STATE_UP;
		state.stateChangeTime = game.getTime();
		game.getEvents().onEvent(game, EventType.KEY_UP, key);
	}

	public void setIdle(Game game, int key){
		KeyState state = keys.get(key);
		state.state = KeyState.STATE_IDLE;
		state.stateChangeTime = game.getTime();
		keys.put(key, state);
	}
	
	public boolean isDown(int key){
		return keys.get(key).state == KeyState.STATE_DOWN;
	}

	public boolean isUp(int key){
		return keys.get(key).state == KeyState.STATE_UP;
	}

	public void update(Game game){
		for(int i = 0; i < keys.size(); i++){
			KeyState state = keys.get(i);
			if(state.state == KeyState.STATE_UP && state.stateChangeTime != game.getTime()){
				state.state = KeyState.STATE_IDLE;
				state.stateChangeTime = game.getTime();
			}
		}
	}

	public boolean allowMovement(){
		return allowMovement;
	}

	public void allowMovement(boolean allowMovement){
		this.allowMovement = allowMovement;
	}

	public static class KeyState{

		public static final int STATE_IDLE = 0;
		public static final int STATE_DOWN = 1;
		public static final int STATE_UP = 2;

		protected int state;
		protected long stateChangeTime;

		public KeyState(){
			this(STATE_IDLE);
		}

		public KeyState(int state){
			this.state = state;
		}
	}
}
