package net.site40.rodit.tinyrpg.game;

import android.graphics.Canvas;

public interface IPaintMixer {

	public void preRender(Game game, Canvas canvas, IGameObject obj);
	public void postRender(Game game, Canvas canvas, IGameObject obj);
}
