package net.site40.rodit.tinyrpg.game.render;

import java.util.ArrayList;

import net.site40.rodit.tinyrpg.game.Game;
import net.site40.rodit.tinyrpg.game.GameObject;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.RectF;

public class TransitionalRenderer extends GameObject{
	
	public static final float DRAW_WIDTH = 1580f;
	public static final float DRAW_HEIGHT = 1020f;

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

	public TransitionalRenderer(ArrayList<String> resources, long transitionTime){
		this.resources = resources;
		this.transitionTime = transitionTime;
	}
	
	public int getNextIndex(){
		int nextIndex = currentIndex + 1;
		if(nextIndex >= resources.size())
			nextIndex = 0;
		return nextIndex;
	}
	
	@Override
	public void update(Game game){
		if(startTime == 0L)
			startTime = game.getTime();

		long alphaChangeDiffMin = (long)((1f - transitionPercentage) * (float)transitionTime);
		long alphaChangeTime = (long)(transitionPercentage * (float)transitionTime);
		
		long diffTime = game.getTime() - startTime;
		if(diffTime > alphaChangeDiffMin)
			cAlphaMulti = 1f - ((float)(diffTime - alphaChangeDiffMin) / (float)alphaChangeTime);
		if(diffTime >= transitionTime){
			currentIndex = getNextIndex();
			//currentTranslationX = -790f;
			//currentTranslationY = -510;
			cAlphaMulti = 1f;
			startTime = game.getTime();
			lastDirection = direction;
			return;
		}
		
		if(direction == lastDirection && diffTime > (long)(transitionPercentage / 2f * (float)transitionTime))
			direction = !direction;
		
		currentTranslationX += (direction ? 0.25f : -0.25f);
		currentTranslationY += (direction ? 0.25f : -0.25f);
		currentScaleX += (direction ? 0.0004f : -0.0004f);
		currentScaleY += (direction ? 0.0004f : -0.0004f);
	}

	@Override
	public void draw(Game game, Canvas canvas){
		String currentRes = resources.get(currentIndex);
		String nextRes = resources.get(getNextIndex());

		Bitmap current = game.getResources().getBitmap(currentRes);
		Bitmap next = game.getResources().getBitmap(nextRes);
		
		if(current != null && next != null){
			float width = DRAW_WIDTH * currentScaleX;
			float height = DRAW_HEIGHT * currentScaleY;
			float x = currentTranslationX - (width - DRAW_WIDTH) / 2;
			float y = currentTranslationY - (height - DRAW_HEIGHT) / 2;
			paint.setAlpha((int)(cAlphaMulti * 255f));
			canvas.drawBitmap(current, null, new RectF(x, y, x + width, y + height), paint);//new RectF(currentTranslationX, currentTranslationY, currentTranslationX + DRAW_WIDTH, currentTranslationY + DRAW_HEIGHT), paint);
			paint.setAlpha((int)((1 - cAlphaMulti) * 255f));
			canvas.drawBitmap(next, null, new RectF(x, y, x + width, y + height), paint);//new RectF(currentTranslationX, currentTranslationY, currentTranslationX + DRAW_WIDTH, currentTranslationY + DRAW_HEIGHT), paint);
		}
	}

	@Override
	public RenderLayer getRenderLayer(){
		return RenderLayer.BOTTOM;
	}

	@Override
	public boolean shouldScale(){
		return false;
	}
}
