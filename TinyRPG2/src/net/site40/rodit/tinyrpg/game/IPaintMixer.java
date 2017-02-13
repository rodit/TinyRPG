package net.site40.rodit.tinyrpg.game;

import net.site40.rodit.tinyrpg.game.object.GameObject;
import android.graphics.Canvas;

public interface IPaintMixer {

	public void preRender(Game game, Canvas canvas, GameObject obj);
	public void postRender(Game game, Canvas canvas, GameObject obj);
}
