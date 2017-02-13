package net.site40.rodit.tinyrpg.game.render;

import java.util.ArrayList;

import net.site40.rodit.tinyrpg.game.Game;
import net.site40.rodit.tinyrpg.game.object.GameObject;
import android.graphics.Bitmap;
import android.graphics.Canvas;

public class TransitionalRenderer extends GameObject{

	public static final float DRAW_WIDTH = 1580f / 1.8f;
	public static final float DRAW_HEIGHT = 1020f / 1.8f;

	private ArrayList<String> resources;
	private long startTime = 0L;
	private long transitionTime;
	private float transitionPercentage = 0.3f;

	private float currentTranslationX = -640f - 150f;
	private float currentTranslationY = -360f - 150f;
	private float currentScaleX = 1f;
	private float currentScaleY = 1f;

	private float cAlphaMulti = 1f;

	private int currentIndex = 0;

	private boolean lastDirection = false;
	private boolean direction = false;
	private Bitmap current;
	private Bitmap next;

	public TransitionalRenderer(ArrayList<String> resources, long transitionTime){
		this.resources = resources;
		this.transitionTime = transitionTime;
	}

	private int uNextIndex;
	public int getNextIndex(){
		uNextIndex = currentIndex + 1;
		if(uNextIndex >= resources.size())
			uNextIndex = 0;
		return uNextIndex;
	}

	private void resetCache(Game game){
		current = game.getResources().getBitmap(resources.get(currentIndex));
		next = game.getResources().getBitmap(resources.get(getNextIndex()));
	}

	private long uAlphaChangeDiffMin;
	private long uAlphaChangeTime;
	private long uDiffTime;
	@Override
	public void update(Game game){
		if(startTime == 0L){
			startTime = game.getTime();
			resetCache(game);
		}

		uAlphaChangeDiffMin = (long)((1f - transitionPercentage) * (float)transitionTime);
		uAlphaChangeTime = (long)(transitionPercentage * (float)transitionTime);

		uDiffTime = game.getTime() - startTime;
		if(uDiffTime > uAlphaChangeDiffMin)
			cAlphaMulti = 1f - ((float)(uDiffTime - uAlphaChangeDiffMin) / (float)uAlphaChangeTime);
		if(uDiffTime >= transitionTime){
			currentIndex = getNextIndex();
			resetCache(game);
			cAlphaMulti = 1f;
			startTime = game.getTime();
			lastDirection = direction;
			return;
		}

		if(direction == lastDirection && uDiffTime > (long)(transitionPercentage / 2f * (float)transitionTime))
			direction = !direction;

		currentTranslationX += (direction ? 0.25f : -0.25f);
		currentTranslationY += (direction ? 0.25f : -0.25f);
		currentScaleX += (direction ? 0.0004f : -0.0004f);
		currentScaleY += (direction ? 0.0004f : -0.0004f);
	}

	private float uWidth, uHeight, ux, uy;
	@Override
	public void draw(Game game, Canvas canvas){
		if(current != null && next != null){			
			uWidth = DRAW_WIDTH * currentScaleX;
			uHeight = DRAW_HEIGHT * currentScaleY;
			ux = currentTranslationX - (uWidth - DRAW_WIDTH) / 2;
			uy = currentTranslationY - (uHeight - DRAW_HEIGHT) / 2;
			paint.setAlpha((int)(cAlphaMulti * 255f));
			bounds.getPooled0().set(ux, uy, uWidth, uHeight);
			canvas.drawBitmap(current, null, bounds.getPooled0(), paint);//new RectF(currentTranslationX, currentTranslationY, currentTranslationX + DRAW_WIDTH, currentTranslationY + DRAW_HEIGHT), paint);
			paint.setAlpha((int)((1 - cAlphaMulti) * 255f));
			canvas.drawBitmap(next, null, bounds.getPooled0(), paint);//new RectF(currentTranslationX, currentTranslationY, currentTranslationX + DRAW_WIDTH, currentTranslationY + DRAW_HEIGHT), paint);
		}
	}

	@Override
	public int getRenderLayer(){
		return RenderLayer.BOTTOM;
	}

	@Override
	public boolean shouldScale(){
		return false;
	}
}
