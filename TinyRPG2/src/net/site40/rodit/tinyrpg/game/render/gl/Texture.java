package net.site40.rodit.tinyrpg.game.render.gl;

import android.opengl.GLES20;


public class Texture {

	private int id;
	private int width;
	private int height;
	
	public Texture(int id, int width, int height){
		this.id = id;
		this.width = width;
		this.height = height;
	}
	
	public int getId(){
		return id;
	}
	
	public int getWidth(){
		return width;
	}
	
	public int getHeight(){
		return height;
	}
	
	public void bind(){
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, id);
	}
	
	public void unbind(){
		unbindStatic();
	}
	
	public static void unbindStatic(){
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);	
	}
}
