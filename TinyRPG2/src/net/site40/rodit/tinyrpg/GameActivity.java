package net.site40.rodit.tinyrpg;

import net.site40.rodit.tinyrpg.game.Game;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Canvas;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

public class GameActivity extends Activity {

	private Game game;

	private GLSurfaceView glSurfaceView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

//		this.glSurfaceView = new GLSurfaceView(this);
//		glSurfaceView.setEGLContextClientVersion(2);
//		glSurfaceView.setRenderer(new RGLRenderer(game = new Game(getApplicationContext(), glSurfaceView)));
//		setContentView(glSurfaceView);
		GameView view = new GameView(getApplicationContext(), this);
		setContentView(view);
	}

	@Override
	protected void onPause() {
		super.onPause();
		if(glSurfaceView != null)
			glSurfaceView.onPause();
		game.pause();
	}

	@Override
	protected void onResume() {
		super.onResume();
		if(glSurfaceView != null)
			glSurfaceView.onPause();
	}

	public class GameView extends View{

		public GameView(Context context, Activity activity){
			super(activity);
			setFocusable(true);
			setFocusableInTouchMode(true);
			setLayerType(View.LAYER_TYPE_HARDWARE, null);
			game = new Game(context, this);
		}

		@Override
		public void onDraw(Canvas canvas){
			game.update();
			game.draw(canvas);
			invalidate();
		}

		@SuppressLint("ClickableViewAccessibility")
		@Override
		public boolean onTouchEvent(MotionEvent event){
			game.input(event);
			return true;
		}

		@Override
		public boolean onKeyDown(int keyCode, KeyEvent event){
			if(keyCode == KeyEvent.KEYCODE_VOLUME_UP || keyCode == KeyEvent.KEYCODE_VOLUME_DOWN || keyCode == KeyEvent.KEYCODE_VOLUME_MUTE)
				return false;
			game.keyInput(event);
			return true;
		}
	}
}
