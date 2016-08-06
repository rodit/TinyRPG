package net.site40.rodit.tinyrpg.game;



public class Input_Old {
	
	public static final int KEY_UP = 0;
	public static final int KEY_DOWN = 1;
	public static final int KEY_LEFT = 2;
	public static final int KEY_RIGHT = 3;
	public static final int KEY_ACTION = 4;
	public static final int KEY_MENU = 5;
	
	private boolean[] keyStates;
	private boolean[] keyUpStates;
	private long[] keyUpTimes;
	private long[] keys;
	
	private boolean allowMovement;
	private boolean disabled = false;
	
	public Input_Old(){
		this.keyStates = new boolean[8];
		this.keyUpStates = new boolean[8];
		this.keyUpTimes = new long[8];
		this.keys = new long[8];
	}
	
	public void enable(){
		disabled = false;
	}
	
	public void disable(){
		disabled = true;		
	}
	
	public boolean isUp(int key){
		return keyUpStates[key];
	}
	
	public boolean isDown(int key){
		return keyStates[key];
	}
	
	public long getDownTime(int key){
		return keys[key];
	}
	
	public long getUpTime(int key){
		return keyUpTimes[key];
	}
	
	public void setKeyState(int key, boolean state){
		keys[key] = state ? keys[key] : 0L;
		keyStates[key] = state;
	}
	
	public void setKeyUpState(int key, boolean state){
		setKeyUpState(key, state, 0L);
	}
	
	public void setKeyUpState(int key, boolean state, long time){
		keyUpStates[key] = state;
		if(true)
			keyUpTimes[key] = time;
	}
	
	public void update(Game game){
		for(int i = 0; i < keys.length; i++){
			if(keyStates[i])
				keys[i] += game.getDelta();
		}
		if(disabled){
			for(int i = 0; i < keys.length; i++){
				keyStates[i] = false;
				keys[i] = 0L;
				keyUpStates[i] = false;
				keyUpTimes[i] = 0L;
			}
		}
	}
	
	public boolean allowMovement(){
		return allowMovement;
	}
	
	public void allowMovement(boolean allowMovement){
		this.allowMovement = allowMovement;
	}
}
