package net.site40.rodit.tinyrpg.game.render.gl;

public class GLHolder {

	protected final float[] mProjection = new float[16];
	protected final float[] mView = new float[16];
	protected final float[] mProjectionView = new float[16];
	
	public void clearMatrices(){
		for(int i = 0; i < 16; i++){
			mProjection[i] = 0f;
			mView[i] = 0f;
			mProjectionView[i] = 0f;
		}
	}
}
