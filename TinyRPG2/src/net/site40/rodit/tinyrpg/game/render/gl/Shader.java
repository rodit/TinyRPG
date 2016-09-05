package net.site40.rodit.tinyrpg.game.render.gl;

import android.opengl.GLES20;


public class Shader {

	protected int type;
	protected int id;
	protected String source;
	
	public Shader(String source, int type){
		this.type = type;
		this.id = type == GLES20.GL_VERTEX_SHADER ? GLUtil.genVertexShader() : GLUtil.genFragmentShader();
		this.source = source;
		GLES20.glShaderSource(id, source);
		GLES20.glCompileShader(id);
	}

	public static final String DEFAULT_VERTEX_SHADER_SOURCE = "uniform mat4 uMVPMatrix;" +
		    "attribute vec4 vPosition;" +
		    "attribute vec2 a_texCoord;" +
		    "varying vec2 v_texCoord;" +
		    "void main() {" +
		    "  gl_Position = uMVPMatrix * vPosition;" +
		    "  v_texCoord = a_texCoord;" +
		    "}";
	public static final String DEFAULT_FRAGMENT_SHADER_SOURCE = "precision mediump float;" +
		    "varying vec2 v_texCoord;" +
		    "uniform sampler2D s_texture;" +
		    "void main() {" +
		    "  gl_FragColor = texture2D( s_texture, v_texCoord );" +
		    "}";
	
	public static Shader DEFAULT_VERTEX_SHADER;
	public static Shader DEFAULT_FRAGMENT_SHADER;
	
	public static void initDefaults(){
		DEFAULT_VERTEX_SHADER = new Shader(DEFAULT_VERTEX_SHADER_SOURCE, GLES20.GL_VERTEX_SHADER);
		DEFAULT_FRAGMENT_SHADER = new Shader(DEFAULT_FRAGMENT_SHADER_SOURCE, GLES20.GL_FRAGMENT_SHADER);
	}
}
