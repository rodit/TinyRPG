package net.site40.rodit.tinyrpg.game.gui;

import net.site40.rodit.tinyrpg.game.Game;
import net.site40.rodit.tinyrpg.game.GameObject;
import android.graphics.Canvas;

public class RendererComponent extends Component{

	private GameObject renderer;
	
	private boolean first = true;
	
	public RendererComponent(GameObject renderer){
		this.renderer = renderer;
	}
	
	@Override
	public void update(Game game){
		if(first && getGui() != null && getGui().isActive()){
			game.addObject(renderer);
			first = false;
		}
	}
	
	@Override
	public void draw(Game game, Canvas canvas){}
}
