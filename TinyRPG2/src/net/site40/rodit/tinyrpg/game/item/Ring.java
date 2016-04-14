package net.site40.rodit.tinyrpg.game.item;

import net.site40.rodit.tinyrpg.game.Game;
import net.site40.rodit.tinyrpg.game.entity.EntityLiving;
import android.graphics.Canvas;
import android.graphics.Paint;


public class Ring extends ItemEquippable{
	
	public Ring(){
		this("", "", "", "", "", Rarity.UNKNOWN, 0L);
	}
	
	public Ring(String name, String showName, String description, String script, String resource, Rarity rarity, long value){
		super(name, showName, description, script, resource, rarity, value, SLOT_FINGER_0, SLOT_FINGER_1);
	}

	@Override
	public boolean drawSpriteOverlay(Canvas canvas, Game game, EntityLiving equipper, Paint paint){
		return true;
	}
}
