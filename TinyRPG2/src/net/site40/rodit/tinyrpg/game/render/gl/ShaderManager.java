package net.site40.rodit.tinyrpg.game.render.gl;

import java.util.HashMap;

import android.content.res.AssetManager;
import android.opengl.GLES20;

public class ShaderManager extends GenericManager{

	private HashMap<String, SimpleShader> shaders;
	
	public ShaderManager(AssetManager assets){
		super(assets);
		this.shaders = new HashMap<String, SimpleShader>();
	}
	
	public SimpleShader get(String vertexFile, String fragmentFile){
		String key = vertexFile + "," + fragmentFile;
		SimpleShader shader = shaders.get(key);
		if(shader == null)
			shaders.put(key, shader = load(vertexFile, fragmentFile));
		return shader;
	}
	
	private SimpleShader load(String vertexFile, String fragmentFile){
		String vertexCode = new String(read(vertexFile));
		int vId = GLES20.glCreateShader(GLES20.GL_VERTEX_SHADER);
		GLES20.glShaderSource(vId, vertexCode);
		GLES20.glCompileShader(vId);
		
		String fragmentCode = new String(read(fragmentFile));
		int fId = GLES20.glCreateShader(GLES20.GL_VERTEX_SHADER);
		GLES20.glShaderSource(fId, fragmentCode);
		GLES20.glCompileShader(fId);
		
		return new SimpleShader(vId, fId);
	}
}
