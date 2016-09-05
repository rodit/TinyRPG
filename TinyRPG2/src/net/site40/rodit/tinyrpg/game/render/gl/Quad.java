package net.site40.rodit.tinyrpg.game.render.gl;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import net.site40.rodit.tinyrpg.game.render.gl.TextureManager.Texture;
import android.opengl.GLES20;

public class Quad {
	
	private boolean updated = false;
	private float x, y, width, height;

	short[] quadIndices = new short[]{
			0, 1, 2, 0, 2, 3
	};
	
	private ShortBuffer indexBuffer;
	private FloatBuffer vertexBuffer;
	private FloatBuffer uvBuffer;
	
	private Texture texture;
	
	public Quad(){
		this(0f, 0f, 0f, 0f);
	}
	
	public Quad(float x, float y, float width, float height){
		this(x, y, width, height, new Texture(0));
	}
	
	public Quad(float x, float y, float width, float height, Texture texture){
		setX(x);
		setY(y);
		setWidth(width);
		setHeight(height);
		setTexture(texture);
	}
	
	public void setX(float x){
		this.x = x;
		updated = true;
	}
	
	public void setY(float y){
		this.y = y;
		updated = true;
	}
	
	public void setWidth(float width){
		this.width = width;
		updated = true;
	}
	
	public void setHeight(float height){
		this.height = height;
		updated = true;
	}
	
	public void setTexture(Texture texture){
		this.texture = texture;
	}
	
	public void render(ShaderProgram currentShader, float[] matrix){
		if(vertexBuffer == null || updated)
			bufferQuad();
		
		int vPositionHandle = GLES20.glGetAttribLocation(currentShader.id, "vPosition");
		GLES20.glEnableVertexAttribArray(vPositionHandle);
		GLES20.glVertexAttribPointer(vPositionHandle, 3, GLES20.GL_FLOAT, false, 0, vertexBuffer);
		
		int texCoordsHandle = GLES20.glGetAttribLocation(currentShader.id, "a_TexCoords");
		GLES20.glEnableVertexAttribArray(texCoordsHandle);
		GLES20.glVertexAttribPointer(texCoordsHandle, 2, GLES20.GL_FLOAT, false, 0, uvBuffer);
		
		int mtrxHandle = GLES20.glGetUniformLocation(currentShader.id, "uMVPMatrix");
		GLES20.glUniformMatrix4fv(mtrxHandle, 1, false, matrix, 0);
		
		int texSamplerHandle = GLES20.glGetUniformLocation(currentShader.id, "s_texture");
		GLES20.glUniform1i(texSamplerHandle, texture.getId());
		
		GLES20.glDrawElements(GLES20.GL_TRIANGLES, quadIndices.length, GLES20.GL_UNSIGNED_SHORT, indexBuffer);
		
		GLES20.glDisableVertexAttribArray(vPositionHandle);
		GLES20.glDisableVertexAttribArray(texCoordsHandle);
	}
	
	protected void bufferQuad(){
		float[] quadVerts = new float[]{
				x, y + height, 0f,
				x, y, 0f,
				x + width, y, 0f,
				x + width, y + height, 0f
		};
		float[] quadUVs = new float[]{
				0f, 0f,
				0f, 1f,
				1f, 1f,
				1f, 0f
		};
		indexBuffer = GLUtil.allocateAndPut(quadIndices);
		vertexBuffer = GLUtil.allocateAndPut(quadVerts);
		uvBuffer = GLUtil.allocateAndPut(quadUVs);
	}
}
