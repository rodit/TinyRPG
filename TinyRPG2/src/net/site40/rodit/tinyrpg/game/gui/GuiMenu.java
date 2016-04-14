package net.site40.rodit.tinyrpg.game.gui;

import net.site40.rodit.tinyrpg.game.Game;
import net.site40.rodit.tinyrpg.game.Values;
import android.graphics.Color;

public class GuiMenu extends Gui{

	public GuiMenu(){
		super("");
	}
	
	@Override
	public void init(){
		Component txtTinyRpg = new Component("txtTinyRpg", "TinyRPG");
		txtTinyRpg.getPaint().setTextSize(Values.FONT_SIZE_HUGE);
		txtTinyRpg.getPaint().setColor(Color.WHITE);
		txtTinyRpg.setX(640);
		txtTinyRpg.setY(92);
		add(txtTinyRpg);
		
		Component btnPlay = new Component("btnPlay", "Play");
		btnPlay.getPaint().setColor(Color.WHITE);
		btnPlay.getPaint().setTextSize(Values.FONT_SIZE_BIG);
		btnPlay.setX(540);
		btnPlay.setY(335);
		btnPlay.setWidth(200);
		btnPlay.setHeight(360);
		btnPlay.addListener(new ComponentListener(){
			public void update(Component component, Game game){}
			public void touchDown(Component component, Game game){}
			public void touchUp(Component component, Game game){
				game.getScripts().execute(game, "script/play.js", new String[0], new Object[0]);
			}
		});
		add(btnPlay);
	}
}
