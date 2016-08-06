package net.site40.rodit.tinyrpg.game.render.effects;

import net.site40.rodit.tinyrpg.game.Game;

public interface IChainableEffect {

	public boolean isComplete(Game game);
	public PPEffect getEffect();
}
