package net.site40.rodit.tinyrpg.game.script;

import net.site40.rodit.tinyrpg.game.Game;
import net.site40.rodit.tinyrpg.game.entity.EntityLiving;
import net.site40.rodit.tinyrpg.game.entity.MovementProvider;
import net.site40.rodit.tinyrpg.game.script.ScriptManager.KVP;
import net.site40.rodit.tinyrpg.game.util.Direction;
import net.site40.rodit.util.Util;

import org.mozilla.javascript.Function;

public class CutseneHelper {

	public static void moveEntity(final Game game, final EntityLiving e, final String dir, final float amount, final Function onComplete, final Object[] args){
		new Thread(){
			@Override
			public void run(){
				Direction direction = Util.tryGetDirection(dir);
				e.setDirection(direction);
				float moved = 0;
				while(moved < amount){
					float toMove = e.getStats().getMoveSpeed() * MovementProvider.SPEED_MULTI;
					switch(direction){
					case D_UP:
						e.setVelocityY(-toMove);
						break;
					case D_DOWN:
						e.setVelocityY(toMove);
						break;
					case D_LEFT:
						e.setVelocityX(-toMove);
						break;
					case D_RIGHT:
						e.setVelocityX(toMove);
						break;
					}
					moved += toMove;
					try{
						Thread.sleep(16l);
					}catch(InterruptedException e){
						e.printStackTrace();
					}
				}
				game.getScript().runFunction(game, onComplete, null, KVP.EMPTY, args);
			}
		}.start();
	}
}
