package net.site40.rodit.tinyrpg.game.gui;

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
	
	@Override
	public void input(MotionEvent event, Game game){
		super.input(event, game);
		int action = event.getAction();
		switch(action){
		case MotionEvent.ACTION_DOWN:
			game.getInput().setDown(game, key);
			break;
		case MotionEvent.ACTION_UP:
			game.getInput().setUp(game, key);
			break;
		case MotionEvent.ACTION_MOVE:
			if(!game.isShowingDialog() && game.getInput().allowMovement()){
				boolean contains = getBoundsF().contains(event.getX(), event.getY());
				if(joystick && !contains)
					game.getInput().setUp(game, key);
				if(joystick && contains)
					game.getInput().setDown(game, key);
			}
			break;
		}
	}
}
