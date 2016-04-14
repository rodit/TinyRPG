package net.site40.rodit.tinyrpg.game.render;

import net.site40.rodit.tinyrpg.game.Game;
import net.site40.rodit.tinyrpg.game.IGameObject;
import net.site40.rodit.tinyrpg.game.IPaintMixer;
import android.graphics.Canvas;

public class FlashMixer implements IPaintMixer{

	private long delay;
	private long life;
	
	private long start = -1L;
	private long lastChange = 0L;
	
	public FlashMixer(long delay, long life){
		this.delay = delay;
		this.life = life;
	}
	
	@Override
	public void preRender(Game game, Canvas canvas, IGameObject object){
		if(start == -1L)
			start = game.getTime();
		if(game.getTime() - start >= life){
			object.getPaint().setAlpha(255);
			object.detachPaintMixer(this);
			return;
		}
		long delta = game.getTime() - lastChange;
		int alpha = object.getPaint().getAlpha();
		if(delta >= delay){
			object.getPaint().setAlpha(alpha == 0 ? 255 : 0);
			lastChange = game.getTime();
		}
	}
	
	@Override
	public void postRender(Game game, Canvas canvas, IGameObject object){}
}
