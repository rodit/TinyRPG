package net.site40.rodit.tinyrpg.game.render.gl;

import java.io.InputStream;
import java.util.HashMap;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;

public class TextureManager extends GenericManager{
	
	public static final int DEFAULT_FILTER = GLES20.GL_NEAREST;

	private HashMap<String, Texture> textures;
	
	public TextureManager(AssetManager assets){
		super(assets);
		this.textures = new HashMap<String, Texture>();
	}
	
	public Texture get(String file){
		Texture texture = textures.get(file);
		if(texture == null)
			textures.put(file, texture = load(file));
		return texture;
	}
	
	public Texture load(String file){
		int[] handle = new int[1];
		GLES20.glGenTextures(1, handle, 0);
		if(handle[0] != 0){
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inScaled = false;
			
			InputStream in = open(file);
			Bitmap bitmap = BitmapFactory.decodeStream(in);			
			close(in);
			
			GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, handle[0]);
			GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, DEFAULT_FILTER);
			GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, DEFAULT_FILTER);
			
			GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
			bitmap.recycle();
		}
		return null;
	}
}
