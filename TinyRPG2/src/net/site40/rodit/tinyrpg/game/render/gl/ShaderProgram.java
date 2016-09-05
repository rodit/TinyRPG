package net.site40.rodit.tinyrpg.game.render.gl;

import android.opengl.GLES20;

public class ShaderProgram {

	protected int id;
	protected Shader vertexShader;
	protected Shader fragmentShader;
	
	public ShaderProgram(Shader vertexShader, Shader fragmentShader){
		this.id = GLUtil.genShaderProgram();
		this.vertexShader = vertexShader;
		this.fragmentShader = fragmentShader;
		
		GLES20.glAttachShader(id, vertexShader.id);
		GLES20.glAttachShader(id, fragmentShader.id);
		GLES20.glLinkProgram(id);
	}
	
	public void bind(){
		GLES20.glUseProgram(id);
	}
	
	public void unbind(){
		GLES20.glUseProgram(0);
	}
	
	public static class DefaultShaderProgram extends ShaderProgram{
		
		public DefaultShaderProgram(){
			super(Shader.DEFAULT_VERTEX_SHADER, Shader.DEFAULT_FRAGMENT_SHADER);
		}
	}
}
