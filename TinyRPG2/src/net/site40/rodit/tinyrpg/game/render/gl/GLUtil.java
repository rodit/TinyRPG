package net.site40.rodit.tinyrpg.game.render.gl;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import android.opengl.GLES20;

public class GLUtil {
	
	public static void prepareFrame(){
		GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
	}
	
	public static int genShaderProgram(){
		return GLES20.glCreateProgram();
	}
	
	public static int genVertexShader(){
		return GLES20.glCreateShader(GLES20.GL_VERTEX_SHADER);
	}
	
	public static int genFragmentShader(){
		return GLES20.glCreateShader(GLES20.GL_FRAGMENT_SHADER);
	}
	
	public static int genTexture(){
		int[] texIds = new int[1];
		GLES20.glGenTextures(1, texIds, 0);
		return texIds[0];
	}
	
	public static void enableTexture(){
		GLES20.glEnable(GLES20.GL_TEXTURE_2D);
	}
	
	public static void disableTexture(){
		GLES20.glDisable(GLES20.GL_TEXTURE_2D);
	}
	
	public static void bindTexture(int id){
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, id);
	}
	
	public static void unbindTexture(){
		bindTexture(0);
	}
	
	public static ShortBuffer allocateShortBuffer(int capacity){
		ByteBuffer tempBuffer = ByteBuffer.allocateDirect(capacity * 2);
		tempBuffer.order(ByteOrder.nativeOrder());
		return tempBuffer.asShortBuffer();
	}
	
	public static FloatBuffer allocateFloatBuffer(int capacity){
		ByteBuffer tempBuffer = ByteBuffer.allocateDirect(capacity * 4);
		tempBuffer.order(ByteOrder.nativeOrder());
		return tempBuffer.asFloatBuffer();
	}
	
	public static ShortBuffer allocateAndPut(short[] data){
		ShortBuffer sBuffer = allocateShortBuffer(data.length);
		sBuffer.put(data);
		sBuffer.position(0);
		return sBuffer;
	}
	
	public static FloatBuffer allocateAndPut(float[] data){
		FloatBuffer fBuffer = allocateFloatBuffer(data.length);
		fBuffer.put(data);
		fBuffer.position(0);
		return fBuffer;
	}
}
