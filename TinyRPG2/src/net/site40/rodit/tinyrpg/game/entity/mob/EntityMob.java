package net.site40.rodit.tinyrpg.game.entity.mob;

import net.site40.rodit.tinyrpg.game.battle.AIBattleProvider;
import net.site40.rodit.tinyrpg.game.entity.EntityLiving;

public class EntityMob extends EntityLiving{

	public EntityMob(){
		super();
		
		this.battleProvider = new AIBattleProvider(this);
	}
}
