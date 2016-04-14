package net.site40.rodit.tinyrpg.game.gui;

import net.site40.rodit.tinyrpg.game.Game;
import net.site40.rodit.util.RenderUtil;
import android.graphics.Canvas;

public class WindowComponent extends Component{

	public WindowComponent(){
		super();
	}
	
	@Override
	public void draw(Game game, Canvas canvas){
		if(flag == INACTIVE)
			return;
		
		RenderUtil.drawBitmapBox(canvas, game, getBoundsF(), paint);
	}
}
