package net.site40.rodit.tinyrpg.game.entity;

import net.site40.rodit.tinyrpg.game.Game;
import net.site40.rodit.tinyrpg.game.util.Direction;
import net.site40.rodit.util.Util;

public class DirectionalCollision extends Entity{
	
	private Direction direction;
	
	public DirectionalCollision(){
		super();
	}
	
	@Override
	public void onCollide(Game game, Entity actor){
		if(direction == null)
			direction = Util.tryGetDirection(getRuntimeProperty("direction"));
		if(actor.isPlayer() && actor.getDirection() == direction){
			EntityPlayer player = (EntityPlayer)actor;
			player.getTileMovementProvider().setMovement(game, player, direction, 1);
		}
	}
}
