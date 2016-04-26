package net.site40.rodit.tinyrpg.game.item.armour;

import net.site40.rodit.tinyrpg.game.Game;
import net.site40.rodit.tinyrpg.game.entity.EntityLiving;
import android.graphics.Canvas;
import android.graphics.Paint;




public class Shield extends Armour{
	
	public Shield(){
		this("", "", "", "", "", Rarity.UNKNOWN, 0L, 0f);
	}

	public Shield(String name, String showName, String description, String script, String resource, Rarity rarity, long value, float armourValue){
		super(name, showName, description, script, resource, rarity, value, SLOT_HAND_1, armourValue);
	}
	
	@Override
	public boolean drawSpriteOverlay(Canvas canvas, Game game, EntityLiving equipper, Paint paint){
		if(!super.drawSpriteOverlay(canvas, game, equipper, paint))
			return drawSpriteOverlay(canvas, game, equipper, paint, "shield_generic");
		return true;
	}
}
