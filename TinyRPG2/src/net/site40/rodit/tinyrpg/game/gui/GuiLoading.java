package net.site40.rodit.tinyrpg.game.gui;

import net.site40.rodit.tinyrpg.game.Values;
import android.graphics.Color;

public class GuiLoading extends Gui{

	public GuiLoading(){
		super("gui/generic_black.png");
	}
	
	@Override
	public void init(){
		Component txtLoading = new Component("txtLoading", "Loading...");
		txtLoading.setX(640);
		txtLoading.setY(360);
		txtLoading.getPaint().setColor(Color.WHITE);
		txtLoading.getPaint().setTextSize(Values.FONT_SIZE_HUGE);
		add(txtLoading);
	}
}
