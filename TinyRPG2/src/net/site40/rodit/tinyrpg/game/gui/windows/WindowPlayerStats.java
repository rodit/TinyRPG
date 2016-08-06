package net.site40.rodit.tinyrpg.game.gui.windows;

import net.site40.rodit.tinyrpg.game.Game;
import net.site40.rodit.tinyrpg.game.Values;
import net.site40.rodit.tinyrpg.game.entity.EntityPlayer;
import android.graphics.Paint.Align;

public class WindowPlayerStats extends Window{
	
	private EntityPlayer player;
	
	private WindowComponent txtTitle;
	private WindowComponent imgPlayer;

	public WindowPlayerStats(Game game, EntityPlayer player){
		super(game);
		this.player = player;
		initialize(game);
	}
	
	@Override
	public void initialize(Game game){
		if(player == null)
			return;
		
		this.setBounds(64, 32, 1152, 640);
		
		this.txtTitle = new WindowComponent("txtTitle");
		txtTitle.setText("Player Stats");
		txtTitle.setX(640);
		txtTitle.setY(32);
		txtTitle.getPaint().setTextSize(Values.FONT_SIZE_BIG);
		txtTitle.getPaint().setTextAlign(Align.CENTER);
		add(txtTitle);
	}
}
