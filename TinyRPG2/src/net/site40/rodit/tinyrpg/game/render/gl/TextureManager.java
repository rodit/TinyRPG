package net.site40.rodit.tinyrpg.game.render.gl;

import java.util.HashMap;

import net.site40.rodit.tinyrpg.game.render.ResourceManager;
import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.GLUtils;

public class TextureManager {

	private ResourceManager resources;
	private HashMap<String, Texture> textures;

	public TextureManager(ResourceManager resources){
		this.resources = resources;
		this.textures = new HashMap<String, Texture>();
	}
	
	public Texture getTexture(String name){
		if(name.endsWith(".png") && !name.startsWith("bitmap/"))
			name = "bitmap/" + name;
		Texture ret = textures.get(name);
		if(ret == null){
			Bitmap loaded = resources.getBitmap(name);
			textures.put(name, ret = new Texture(bitmapToTexture(loaded), loaded.getWidth(), loaded.getHeight()));
			loaded.recycle();
		}
		return ret;
	}

	public static int bitmapToTexture(Bitmap bitmap){
		int texId = GLUtil.genTexture();
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texId);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
		GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
		return texId;
	}
}
