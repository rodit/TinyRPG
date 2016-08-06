package net.site40.rodit.tinyrpg.game.render.effects;

import java.util.ArrayList;

import net.site40.rodit.tinyrpg.game.Game;
import android.graphics.Canvas;

public class ChainEffect extends PPEffect implements IChainableEffect{

	private ArrayList<IChainableEffect> effects;
	private int cEffectIndex = 0;

	public ChainEffect(IChainableEffect... effects){
		this.effects = new ArrayList<IChainableEffect>();
		if(effects != null)
			for(int i = 0; i < effects.length; i++)
				this.effects.add(effects[i]);
	}
	
	public boolean isComplete(Game game){
		return cEffectIndex >= effects.size();
	}
	
	public PPEffect getEffect(){
		return this;
	}
	
	public void update(Game game){
		if(isComplete(game)){
			game.getPostProcessor().remove(this);
			return;
		}
		
		IChainableEffect effect = effects.get(cEffectIndex);
		effect.getEffect().update(game);

		if(effect.isComplete(game)){
			cEffectIndex++;
			//game.skipFrames(1);
		}
	}

	public void preDraw(Game game, Canvas canvas){
		if(!isComplete(game))
			effects.get(cEffectIndex).getEffect().preDraw(game, canvas);
	}

	public void draw(Game game, Canvas canvas){
		if(!isComplete(game))
			effects.get(cEffectIndex).getEffect().draw(game, canvas);
	}
	
	public void postDraw(Game game, Canvas canvas){
		if(!isComplete(game))
			effects.get(cEffectIndex).getEffect().postDraw(game, canvas);
	}
}
