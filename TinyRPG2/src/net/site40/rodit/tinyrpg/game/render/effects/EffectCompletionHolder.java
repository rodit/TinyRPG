package net.site40.rodit.tinyrpg.game.render.effects;

import net.site40.rodit.tinyrpg.game.Game;
import android.graphics.Canvas;

public class EffectCompletionHolder extends PPEffect{
	
	private IChainableEffect effect;
	private Runnable onComplete;
	
	public EffectCompletionHolder(IChainableEffect effect, Runnable onComplete){
		this.effect = effect;
		this.onComplete = onComplete;
	}

	@Override
	public void update(Game game){
		if(effect.isComplete(game)){
			if(onComplete != null){
				onComplete.run();
				game.getPostProcessor().remove(this);
			}
		}
	}
	
	public void preDraw(Game game, Canvas canvas){}
	public void draw(Game game, Canvas canvas){}
	public void postDraw(Game game, Canvas canvas){}
}
