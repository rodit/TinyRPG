package net.site40.rodit.tinyrpg.game.gui.windows;

import net.site40.rodit.tinyrpg.game.Game;
import android.view.KeyEvent;

public class WindowListener {

	public void onFocus(Game game, WindowComponent component){}
	public void onUnfocus(Game game, WindowComponent component){}
	public void update(Game game, WindowComponent component){}
	public void touchDown(Game game, WindowComponent component){}
	public void touchUp(Game game, WindowComponent component){}
	public void keyInput(Game game, KeyEvent event){}
}
