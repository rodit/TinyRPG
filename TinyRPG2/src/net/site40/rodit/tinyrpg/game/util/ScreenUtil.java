package net.site40.rodit.tinyrpg.game.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.PointF;
import android.os.Build;
import android.view.Display;
import android.view.WindowManager;

public class ScreenUtil {

	private float screenWidth;
	private float screenHeight;
	private float ratioRenderX;
	private float ratioRenderY;
	private float ratioInputX;
	private float ratioInputY;

	@SuppressLint("NewApi")
	public ScreenUtil(Context context){
		Display window = ((WindowManager)context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
		Point size = new Point();
		if(Build.VERSION.SDK_INT >= 13){
			window.getSize(size);
			screenWidth = size.x;
			screenHeight = size.y;
		}else{
			screenWidth = window.getWidth();
			screenHeight = window.getHeight();
		}
		ratioRenderX = screenWidth / 1280f;
		ratioRenderY = screenHeight / 720f;
		ratioInputX = 1280f / screenWidth;
		ratioInputY = 720f / screenHeight;
	}
	
	public void apply(Canvas canvas){
		canvas.scale(ratioRenderX, ratioRenderY);
	}
	
	public float getWidth(){
		return screenWidth;
	}
	
	public float getHeight(){
		return screenHeight;
	}
	
	public float getRatioRenderX(){
		return ratioRenderX;
	}
	
	public float getRatioRenderY(){
		return ratioRenderY;
	}
	
	public float getRatioInputX(){
		return ratioInputX;
	}
	
	public float getRatioInputY(){
		return ratioInputY;
	}
	
	public PointF scaleRender(float x, float y){
		return new PointF(scaleRenderX(x), scaleRenderY(y));
	}
	
	public float scaleRenderX(float x){
		return x * ratioRenderX;
	}
	
	public float scaleRenderY(float y){
		return y * ratioRenderY;
	}
	
	public PointF scaleInput(float x, float y){
		return new PointF(scaleInputX(x), scaleInputY(y));
	}
	
	public float scaleInputX(float x){
		return x * ratioInputX;
	}
	
	public float scaleInputY(float y){
		return y * ratioInputY;
	}
}
