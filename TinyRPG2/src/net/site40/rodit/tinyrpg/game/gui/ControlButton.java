package net.site40.rodit.tinyrpg.game.gui;

import net.site40.rodit.tinyrpg.game.Game;
import net.site40.rodit.tinyrpg.game.event.EventReceiver.EventType;
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

	@Override
	public void update(Game game){
		super.update(game);
		if(!isSelected() && game.getInput().isUp(key) && game.getInput().getUpTime(key) != game.getTime())
			game.getInput().setKeyUpState(key, false, game.getTime());
	}
	
	@Override
	public void input(MotionEvent event, Game game){
		super.input(event, game);
		int action = event.getAction();
		switch(action){
		case MotionEvent.ACTION_DOWN:
			game.getInput().setKeyState(key, true);
			game.getEvents().onEvent(game, EventType.KEY_DOWN, key);
			break;
		case MotionEvent.ACTION_UP:
			game.getInput().setKeyState(key, false);
			game.getInput().setKeyUpState(key, true, game.getTime());
			game.getEvents().onEvent(game, EventType.KEY_UP, key);
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
