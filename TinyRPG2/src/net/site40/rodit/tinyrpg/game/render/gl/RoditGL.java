package net.site40.rodit.tinyrpg.game.render.gl;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import net.site40.rodit.tinyrpg.game.Game;
import android.graphics.RectF;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView.Renderer;
import android.opengl.Matrix;

public class RoditGL implements Renderer{

	private Game game;

	private final float[] mtrxProjection = new float[16];
	private final float[] mtrxView = new float[16];
	private final float[] mtrxProjectionAndView = new float[16];

	private TextureManager textures;
	private int screenWidth;
	private int screenHeight;
	
	public RoditGL(Game game){
		this.game = game;
		game.setGl(this);
	}

	public TextureManager getTextures(){
		return textures;
	}
	
	public float[] getPVMatrix(){
		return mtrxProjectionAndView;
	}
	
	public void initialize(){
		String defaultVertShader = game.getResources().getString("zgl/shader/default.vert");
		String defaultFragShader = game.getResources().getString("zgl/shader/default.frag");
		ShaderTool.DEFAULT = ShaderTool.linkProgram(ShaderTool.loadShader(GLES20.GL_VERTEX_SHADER, defaultVertShader), ShaderTool.loadShader(GLES20.GL_FRAGMENT_SHADER, defaultFragShader));
		
		textures = new TextureManager(game.getResources());
		
		GLES20.glUseProgram(ShaderTool.DEFAULT);
	}
	
	@Override
	public void onSurfaceCreated(GL10 paramGL10, EGLConfig config){
		initialize();
	}

	@Override
	public void onSurfaceChanged(GL10 paramGL10, int width, int height){
		this.screenWidth = width;
		this.screenHeight = height;

		GLES20.glViewport(0, 0, 1280, 720);//(int)screenWidth, (int)screenHeight);
		
		for(int i = 0 ;i < 16; i++){
			mtrxProjection[i] = 0.0f;
			mtrxView[i] = 0.0f;
			mtrxProjectionAndView[i] = 0.0f;
		}

		Matrix.orthoM(mtrxProjection, 0, 0f, 1280f, 720f, 0f, 0, 50);
		Matrix.setLookAtM(mtrxView, 0, 0f, 0f, 1f, 0f, 0f, 0f, 0f, 1.0f, 0.0f);
		Matrix.multiplyMM(mtrxProjectionAndView, 0, mtrxProjection, 0, mtrxView, 0);
	}

	@Override
	public void onDrawFrame(GL10 paramGL10){
		game.update();
		game.draw();
	}
	
	public void drawBitmap(String resource, RectF bounds){
		drawBitmap(resource, bounds.left, bounds.top, bounds.width(), bounds.height());
	}
	
	public void drawBitmap(String resource, float x, float y, float width, float height){
		TexturedQuad quad = new TexturedQuad(ShaderTool.DEFAULT, x, y, width, height, textures.getTexture(resource).getId());
		quad.draw(mtrxProjectionAndView);
	}
}
