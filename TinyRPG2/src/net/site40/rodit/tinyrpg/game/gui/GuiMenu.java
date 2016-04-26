package net.site40.rodit.tinyrpg.game.gui;

import net.site40.rodit.tinyrpg.game.Game;
import net.site40.rodit.tinyrpg.game.Values;
import net.site40.rodit.tinyrpg.game.gui.ComponentListener.ComponentListenerImpl;
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
		btnPlay.setBackground("gui/button.png");
		btnPlay.setBackgroundSelected("gui/button_selected.png");
		btnPlay.setX(540);
		btnPlay.setY(335);
		btnPlay.setWidth(256);
		btnPlay.setHeight(128);
		btnPlay.addListener(new ComponentListenerImpl(){
			public void touchUp(Component component, Game game){
				game.getScripts().execute(game, "script/play.js", new String[0], new Object[0]);
			}
		});
		add(btnPlay);
		
		Component btnSaves = new Component("btnSaves", "Saves");
		btnSaves.getPaint().setTextSize(Values.FONT_SIZE_BIG);
		btnSaves.setBackground("gui/button.png");
		btnSaves.setBackgroundSelected("gui/button_selected.png");
		btnSaves.setX(540);
		btnSaves.setY(600);
		btnSaves.setWidth(256f);
		btnSaves.setHeight(92f);
		btnSaves.addListener(new ComponentListenerImpl(){
			public void touchUp(Component component, Game game){
				game.getGuis().hide(GuiMenu.class);
				game.getGuis().show(GuiSaves.class);
			}
		});
		add(btnSaves);
	}
}
