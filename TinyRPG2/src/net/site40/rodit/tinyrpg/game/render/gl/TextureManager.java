package net.site40.rodit.tinyrpg.game.render.gl;

import java.util.HashMap;

import net.site40.rodit.tinyrpg.game.render.ResourceManager;
import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.GLUtils;

public class TextureManager {

	public static final int DEFAULT_FILTER = GLES20.GL_NEAREST;

	private ResourceManager resources;
	private HashMap<String, Texture> textures;

	public TextureManager(ResourceManager resources){
		this.resources = resources;
		this.textures = new HashMap<String, Texture>();
	}

	public Texture get(String resource){
		if(resource.endsWith(".png") && !resource.startsWith("bitmap/"))
			resource = "bitmap/" + resource;
		Texture tex = textures.get(resource);
		if(tex == null)
			textures.put(resource, tex = loadTexture(resource));
		return tex;
	}

	public Texture loadTexture(String resource){
		Bitmap bmp = resources.readBitmap(resource);
		if(bmp == null)
			return null;
		int texId = GLUtil.genTexture();
		GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texId);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, DEFAULT_FILTER);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, DEFAULT_FILTER);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_REPEAT);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_REPEAT);
		GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bmp, 0);
		bmp.recycle();
		return new Texture(texId);
	}

	public static class Texture{

		protected int id;

		public Texture(int id){
			this.id = id;
		}

		public int getId(){
			return id;
		}
	}
}
