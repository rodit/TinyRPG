package net.site40.rodit.tinyrpg.game.gui2;

import net.site40.rodit.tinyrpg.game.Game;
import android.graphics.Canvas;
import android.view.KeyEvent;
import android.view.MotionEvent;

public interface IComponentListener {

	public void touchDown(Game game, MotionEvent event, Component component);
	public void touchUp(Game game, MotionEvent event, Component component);
	public void keyDown(Game game, KeyEvent event, Component component);
	public void keyUp(Game game, KeyEvent event, Component component);
	public void update(Game game, Component component);
	public void draw(Game game, Canvas canvas, Component component);
	
	public static class ComponentListener implements IComponentListener{
		
		public void touchDown(Game game, MotionEvent event, Component component){}
		public void touchUp(Game game, MotionEvent event, Component component){}
		public void keyDown(Game game, KeyEvent event, Component component){}
		public void keyUp(Game game, KeyEvent event, Component component){}
		public void update(Game game, Component component){}
		public void draw(Game game, Canvas canvas, Component component){}
	}
}
