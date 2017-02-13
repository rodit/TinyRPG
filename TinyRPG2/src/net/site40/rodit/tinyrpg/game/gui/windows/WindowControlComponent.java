package net.site40.rodit.tinyrpg.game.gui.windows;

import net.site40.rodit.tinyrpg.game.Game;
import android.view.MotionEvent;

public class WindowControlComponent extends WindowComponent{

	protected int inputKey;
	protected boolean joystick;
	
	public WindowControlComponent(int inputKey, boolean joystick, String background){
		super("control_" + inputKey);
		this.inputKey = inputKey;
		this.joystick = joystick;
		this.setBackgroundDefault(background);
	}
	
	public int getInputKey(){
		return inputKey;
	}
	
	public void setInputKey(int inputKey){
		this.inputKey = inputKey;
	}
	
	public boolean isJoystick(){
		return joystick;
	}
	
	public void setJoystick(boolean joystick){
		this.joystick = joystick;
	}
	
	private boolean first = true;
	
	@Override
	public void update(Game game){
		if(first){
			paint.setAlpha(game.getGlobali("controls_alpha"));
			first = false;
		}
		
		super.update(game);
		//if(this.getState() != STATE_DOWN && game.getInput().isUp(inputKey) && game.getInput().getUpTime(inputKey) != game.getTime())
			//game.getInput().setKeyUpState(inputKey, false, game.getTime());
	}
	
	@Override
	public WindowEventStatus touchInput(Game game, MotionEvent event){
		if(super.touchInput(game, event) == WindowEventStatus.UNHANDLED)
			return WindowEventStatus.UNHANDLED;
		
		int action = event.getAction();
		switch(action){
		case MotionEvent.ACTION_DOWN:
			game.getInput().setDown(game, inputKey);
			break;
		case MotionEvent.ACTION_UP:
			game.getInput().setUp(game, inputKey);
			break;
		case MotionEvent.ACTION_MOVE:
			boolean contains = bounds.get().contains(event.getX(), event.getY());
			if(!contains && joystick && game.getInput().allowMovement())
				game.getInput().setUp(game, inputKey);
			if(contains && joystick && game.getInput().allowMovement())
				game.getInput().setDown(game, inputKey);
			if(contains && getState() != STATE_DOWN){
				game.getInput().setDown(game, inputKey);
				setState(STATE_DOWN);
			}
			break;
		}
		return WindowEventStatus.HANDLED;
	}
}
