package net.site40.rodit.tinyrpg.game.gui2;

import net.site40.rodit.tinyrpg.game.Game;
import android.view.MotionEvent;

public class GuiMenu extends LinkedGui{

	public GuiMenu(){}
	
	public void btnPlayClick(Game game, MotionEvent event, Component component){
		game.getScripts().execute(game, "script/play.js", new String[0], new Object[0]);
	}
}
