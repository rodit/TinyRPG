package net.site40.rodit.tinyrpg.game.render.gl;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import android.opengl.GLES20;

public class TexturedQuad {
	
	private static final short[] indices = new short[] { 0, 1, 2, 0, 2, 3 };
	private static final float[] uvs_old = new float[] { 0f, 0f, 0f, 1f, 1f, 1f, 1f, 0f };
	private static final float[] uvs = new float[] { 0f, 1f, 0f, 0f, 1f, 0f, 1f, 1f };
	
	private float x;
	private float y;
	private float width;
	private float height;
	private int texture;

	private FloatBuffer vertexGen;
	private ShortBuffer indexGen;
	private FloatBuffer uvGen;

	private int shader;
	private boolean initHandles = false;
	private int positionHandle;
	private int texCoordHandle;
	private int matrixHandle;
	private int samplerHandle;

	public TexturedQuad(int shader){
		this(shader, 0f, 0f, 1f, 1f, 0);
	}

	public TexturedQuad(int shader, float x, float y, float width, float height, int texture){
		this.shader = shader;
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.texture = texture;
		
		vertexGen = GLUtil.allocateAndPut(new float[] {
				x + width, y + height, 0f,
				x + width, y, 0f,
				x, y, 0f,
				x, y + height, 0f,
		});
		indexGen = GLUtil.allocateAndPut(indices);
		uvGen = GLUtil.allocateAndPut(uvs);
	}

	public void draw(float[] matrix){
		if(!initHandles){
			positionHandle = GLES20.glGetAttribLocation(shader, "vPosition");
			texCoordHandle = GLES20.glGetAttribLocation(shader, "a_texCoord");
			matrixHandle = GLES20.glGetUniformLocation(shader, "uMVPMatrix");
			samplerHandle = GLES20.glGetUniformLocation(shader, "s_texture");
			initHandles = true;
		}
		GLES20.glEnableVertexAttribArray(positionHandle);
		GLES20.glVertexAttribPointer(positionHandle, 3, GLES20.GL_FLOAT, false, 0, vertexGen);
		GLES20.glEnableVertexAttribArray(texCoordHandle);
		GLES20.glVertexAttribPointer(texCoordHandle, 2, GLES20.GL_FLOAT, false, 0, uvGen);
		GLES20.glUniformMatrix4fv(matrixHandle, 1, false, matrix, 0);
		GLES20.glUniform1i(samplerHandle, 0);
		GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture);
		GLES20.glDrawElements(GLES20.GL_TRIANGLES, indices.length, GLES20.GL_UNSIGNED_SHORT, indexGen);
		GLES20.glDisableVertexAttribArray(positionHandle);
		GLES20.glDisableVertexAttribArray(texCoordHandle);
	}
}
