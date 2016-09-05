package net.site40.rodit.tinyrpg.game.render.gl;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import net.site40.rodit.tinyrpg.game.Game;
import net.site40.rodit.tinyrpg.game.render.gl.ShaderProgram.DefaultShaderProgram;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView.Renderer;
import android.opengl.Matrix;

public class RGLRenderer implements Renderer{
	
	private int width;
	private int height;
	
	private Game game;
	private TextureManager textures;
	private GLHolder holder;
	
	public RGLRenderer(Game game){
		this.textures = new TextureManager(game.getResources());
	}
	
	private Quad testQuad = new Quad(0, 0, 32, 32);
	private ShaderProgram sp;

	@Override
	public void onDrawFrame(GL10 unused0){
		GLUtil.prepareFrame();
		
		testQuad.setTexture(textures.get("item/assassin_hood.png"));
		sp.bind();
		testQuad.render(sp, holder.mProjectionView);
		sp.unbind();
	}

	@Override
	public void onSurfaceChanged(GL10 unused0, int width, int height){
		this.width = width;
		this.height = height;
		
        GLES20.glViewport(0, 0, width, height);
        
        holder.clearMatrices();
        
        Matrix.orthoM(holder.mProjection, 0, 0f, width, 0.0f, height, 0, 50);
        Matrix.setLookAtM(holder.mView, 0, 0f, 0f, 1f, 0f, 0f, 0f, 0f, 1.0f, 0.0f);
        Matrix.multiplyMM(holder.mProjectionView, 0, holder.mProjection, 0, holder.mView, 0);
	}

	@Override
	public void onSurfaceCreated(GL10 unused0, EGLConfig config){
		GLES20.glClearColor(0f, 0f, 0f, 0f);
		
		this.holder = new GLHolder();
		
		Shader.initDefaults();
		this.sp = new DefaultShaderProgram();
	}
}
