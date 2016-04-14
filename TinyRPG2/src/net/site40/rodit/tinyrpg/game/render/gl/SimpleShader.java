package net.site40.rodit.tinyrpg.game.render.gl;

import android.opengl.GLES20;

public class SimpleShader {

	private int programId;
	private int vId;
	private int fId;

	public SimpleShader(int vId, int fId){
		this.programId = -1;
		this.vId = vId;
		this.fId = fId;
	}

	public void bind(){
		if(programId == -1){
			programId = GLES20.glCreateProgram();
			GLES20.glAttachShader(programId, vId);
			GLES20.glAttachShader(programId, fId);
			GLES20.glLinkProgram(programId);
		}
		GLES20.glUseProgram(programId);
	}

	public void unbind(){
		unbindStatic();
	}

	public static void unbindStatic(){
		GLES20.glUseProgram(0);
	}
}
