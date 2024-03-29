package net.site40.rodit.tinyrpg.game.gui2;

import net.site40.rodit.tinyrpg.game.Game;
import android.view.MotionEvent;

public class ControlButton extends Component{

	public static final int DEFAULT_ALPHA = 50;

	private int key;
	private boolean joystick;

	public ControlButton(int key){
		this(key, "", false);
	}

	public ControlButton(int key, String name, boolean joystick){
		super();
		this.key = key;
		this.setName(name);
		this.joystick = joystick;
		getPaint().setAlpha(DEFAULT_ALPHA);
	}

	public int getKey(){
		return key;
	}

	public void setKey(int key){
		this.key = key;
	}
	
	public boolean isSelected(){
		return getState() == STATE_TOUCH;
	}
	
	public void setSelected(boolean selected){
		setState(selected ? STATE_TOUCH : STATE_IDLE);
	}

	@Override
	public void update(Game game){
		super.update(game);
		if(isSelected() && game.getInput().isUp(key) && game.getInput().getUpTime(key) != game.getTime())
			game.getInput().setKeyUpState(key, false, game.getTime());
	}
	
	@Override
	public void handleTouchInput(Game game, MotionEvent event){
		super.handleTouchInput(game, event);
		int action = event.getAction();
		switch(action){
		case MotionEvent.ACTION_DOWN:
			game.getInput().setKeyState(key, true);
			break;
		case MotionEvent.ACTION_UP:
			game.getInput().setKeyState(key, false);
			game.getInput().setKeyUpState(key, true, game.getTime());
			break;
		case MotionEvent.ACTION_MOVE:
			boolean contains = getBoundsF().contains(event.getX(), event.getY());
			if(!contains && joystick && game.getInput().allowMovement()){
				game.getInput().setKeyState(key, false);
				game.getInput().setKeyUpState(key, true, game.getTime());
			}
			if(contains && joystick && game.getInput().allowMovement())
				game.getInput().setKeyState(key, true);
			if(contains && !isSelected()){
				game.getInput().setKeyState(key, true);
				setSelected(true);
			}
			break;
		}
	}
}
