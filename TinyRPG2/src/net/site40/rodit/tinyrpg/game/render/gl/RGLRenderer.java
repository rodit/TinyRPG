package net.site40.rodit.tinyrpg.game.render.gl;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import net.site40.rodit.tinyrpg.game.Game;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView.Renderer;

public class RGLRenderer implements Renderer{

	private int screenWidth;
	private int screenHeight;
	private long lastDraw;
	private int fps;
	private boolean firstDraw;
	private boolean surfaceCreated;
	
	private Game game;

	public RGLRenderer(Game game){
		this.screenWidth = -1;
		this.screenHeight = -1;
		this.lastDraw = System.currentTimeMillis();
		this.fps = 0;
		this.firstDraw = true;
		
		this.game = game;
	}

	@Override
	public void onSurfaceCreated(GL10 notUsed, EGLConfig config){
		surfaceCreated = true;
		screenWidth = -1;
		screenHeight = -1;
	}

	@Override
	public void onSurfaceChanged(GL10 notUsed, int width, int height) {
		if(!surfaceCreated && width == screenWidth && height == screenHeight)
			return;

		screenWidth = width;
		screenHeight = height;

		onCreate(screenWidth, screenHeight, surfaceCreated);
		surfaceCreated = false;
	}

	@Override
	public void onDrawFrame(GL10 notUsed) {
		onDrawFrame(firstDraw);

		fps++;
		long currentTime = System.currentTimeMillis();
		if(currentTime - lastDraw >= 1000){
			fps = 0;
			lastDraw = currentTime;
		}

		if(firstDraw)
			firstDraw = false;
	}
	
	public void onCreate(int width, int height, boolean contextLost){
		GLES20.glClearColor(0f, 0f, 0f, 1f);
	}
	public void onDrawFrame(boolean firstDraw) {
		GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
		
		game.draw(null);
	}

	public int getFps(){
		return fps;
	}
}
