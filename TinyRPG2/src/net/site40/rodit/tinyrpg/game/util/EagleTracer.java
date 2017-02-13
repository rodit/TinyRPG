package net.site40.rodit.tinyrpg.game.util;

import net.site40.rodit.tinyrpg.game.entity.Entity;
import net.site40.rodit.tinyrpg.game.map.MapState;

public class EagleTracer {

	public EagleTracer(){}
	
	public Object trace(MapState map, Entity ent, float distance){
		if(map == null)
			return null;
		float nx = ent.getBounds().getCenterX();
		float ny = ent.getBounds().getCenterY();
		if(ent.getDirection() == Direction.D_UP)
			ny -= distance;
		else if(ent.getDirection() == Direction.D_DOWN)
			ny += distance;
		else if(ent.getDirection() == Direction.D_LEFT)
			nx -= distance;
		else if(ent.getDirection() == Direction.D_RIGHT)
			nx += distance;
		return map.getCollisionObjectD(nx, ny, true, ent);
	}
}
