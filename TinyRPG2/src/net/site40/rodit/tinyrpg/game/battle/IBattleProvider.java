package net.site40.rodit.tinyrpg.game.battle;

import net.site40.rodit.tinyrpg.game.Game;
import net.site40.rodit.tinyrpg.game.entity.EntityLiving;

public interface IBattleProvider {

	public void makeDecision(Game game, Battle battle);
	public EntityLiving getEntity();
}
