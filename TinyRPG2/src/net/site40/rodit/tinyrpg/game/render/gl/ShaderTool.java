package net.site40.rodit.tinyrpg.game.render.gl;

import android.opengl.GLES20;
import android.opengl.GLU;
import android.util.Log;

public class ShaderTool {
	
	public static int DEFAULT = -1;
 
    public static int loadShader(int type, String shaderCode){
        int shader = GLES20.glCreateShader(type);
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);
        int error = GLES20.glGetError();
        if(error != 0)
        	Log.e("ShaderTool", "OpenGL error while compiling shader: " + GLU.gluErrorString(error));
        return shader;
    }
    
    public static int linkProgram(int vertexShader, int fragmentShader){
    	int program = GLES20.glCreateProgram();
    	GLES20.glAttachShader(program, vertexShader);
    	GLES20.glAttachShader(program, fragmentShader);
    	GLES20.glLinkProgram(program);
        int error = GLES20.glGetError();
        if(error != 0)
        	Log.e("ShaderTool", "OpenGL error while attaching/linking shader: " + GLU.gluErrorString(error));
    	return program;
    }
}
