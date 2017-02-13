package net.site40.rodit.tinyrpg.game.gui.windows;

import java.util.ArrayList;

import net.site40.rodit.tinyrpg.game.Game;
import net.site40.rodit.tinyrpg.game.object.GameObject;
import net.site40.rodit.util.RenderUtil;
import android.graphics.Canvas;
import android.graphics.Paint.Align;
import android.graphics.RectF;
import android.os.SystemClock;
import android.text.StaticLayout;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.KeyEvent;
import android.view.MotionEvent;

public class WindowComponent extends GameObject{

	public static final int FLAG_NONE = 0;
	public static final int FLAG_DISABLED = 1;
	public static final int FLAG_INVISIBLE = 2;
	public static final int FLAG_MULTILINE_TEXT = 3;
	public static final int FLAG_WRAPPED_TEXT = 4;

	public static final int STATE_IDLE = 0;
	public static final int STATE_DOWN = 1;
	public static final int STATE_DISABLED = 2;

	private Window parent;
	private String name;
	private boolean[] flags;
	private int state;
	private SparseArray<String> backgrounds;
	private boolean focus;
	private String text;
	private ArrayList<WindowListener> listeners;
	
	private String[] lineCache;
	private StaticLayout multiLineDrawCache;

	public WindowComponent(){
		this("");
	}

	public WindowComponent(String name){
		super();
		paint.setTextAlign(Align.CENTER);
		paint.setTextSize(12f);
		
		this.name = name;
		this.flags = new boolean[8];
		this.state = STATE_IDLE;
		this.backgrounds = new SparseArray<String>();
		this.focus = false;
		this.text = "";
		this.listeners = new ArrayList<WindowListener>();
	}

	public float getScreenX(){
		return (parent == null ? 0 : parent.getBounds().getX()) + bounds.getX();
	}

	public float getScreenY(){
		return (parent == null ? 0 : parent.getBounds().getY()) + bounds.getY();
	}
	
	public RectF getScreenBoundsF(){
		bounds.getPooled0().set(getScreenX(), getScreenY(), getScreenX() + bounds.getWidth(), getScreenY() + bounds.getHeight());
		return bounds.getPooled0();
	}

	public Window getParent(){
		return parent;
	}

	public void setParent(Window parent){
		this.parent = parent;
	}

	public String getName(){
		return name;
	}

	public void setName(String name){
		this.name = name;
	}

	public boolean getFlag(int flag){
		return flags[flag];
	}

	public void setFlag(int flag, boolean value){
		flags[flag] = value;
	}

	public int getState(){
		return state;
	}

	public void setState(int state){
		this.state = state;
	}

	public String getBackground(){
		return getBackground(state);
	}

	public String getBackground(int state){
		return backgrounds.get(state);
	}

	public void setBackgroundDefault(String background){
		for(int i = 0; i < 8; i++)
			backgrounds.put(i, background);
	}

	public void setBackground(int state, String background){
		backgrounds.put(state, background);
	}

	public boolean hasFocus(){
		return focus;
	}

	public void setFocus(boolean focus, Game game){
		boolean oldFocus = this.focus;
		this.focus = focus;
		if(game != null && focus != oldFocus){
			if(focus)
				onFocus(game);
			else
				onUnfocus(game);
		}
	}

	public void onFocus(Game game){
		for(WindowListener listener : listeners)
			listener.onFocus(game, this);
	}

	public void onUnfocus(Game game){
		for(WindowListener listener : listeners)
			listener.onUnfocus(game, this);
	}

	public String getText(){
		return text;
	}
	
	public void setText(String text){
		if(lineCache == null || multiLineDrawCache == null || !this.text.equals(text)){
			lineCache = text.split("\n");
			multiLineDrawCache = RenderUtil.getStaticLayout(text, (int)bounds.getWidth(), paint);
		}
		this.text = text;
	}

	public void addListener(WindowListener listener){
		listeners.add(listener);
	}

	public void removeListener(WindowListener listener){
		listeners.remove(listener);
	}

	public ArrayList<WindowListener> getListeners(){
		return listeners;
	}

	public void simulateInput(Game game, int type){
		long downTime = SystemClock.uptimeMillis();
		long eventTime = SystemClock.uptimeMillis() + 100;
		int metaState = 0;
		MotionEvent motionEvent = MotionEvent.obtain(downTime, eventTime, type, bounds.getX(), bounds.getY(), metaState);
		touchInput(game, motionEvent);
		motionEvent.recycle();
	}

	public void onTouchDown(Game game){
		for(WindowListener listener : listeners)
			listener.touchDown(game, this);
	}

	public void onTouchUp(Game game){
		for(WindowListener listener : listeners)
			listener.touchUp(game, this);
	}

	public void onUpdate(Game game){
		for(WindowListener listener : listeners)
			listener.update(game, this);
	}

	public WindowEventStatus touchInput(Game game, MotionEvent event){
		if(state == STATE_DISABLED || getFlag(FLAG_INVISIBLE))
			return WindowEventStatus.UNHANDLED;

		if(!this.getScreenBoundsF().contains(event.getX(), event.getY())){
			this.setState(STATE_IDLE);
			this.setFocus(false, game);
			return WindowEventStatus.UNHANDLED;
		}

		switch(event.getAction()){
		case MotionEvent.ACTION_DOWN:
			parent.unfocusAll(game);
			state = STATE_DOWN;
			focus = true;

			onTouchDown(game);
			break;
		case MotionEvent.ACTION_UP:
			if(state == STATE_DOWN){
				state = STATE_IDLE;
				onTouchUp(game);
			}
			break;
		case MotionEvent.ACTION_MOVE:
			if(state == STATE_IDLE)
				parent.unfocusAll(game);
			break;
		}

		return WindowEventStatus.HANDLED;
	}

	public void onKeyInput(Game game, KeyEvent event){
		for(WindowListener listener : listeners)
			listener.keyInput(game, event);
	}

	public void keyInput(Game game, KeyEvent event){
		onKeyInput(game, event);
	}

	@Override
	public void update(Game game){
		if(state == STATE_DISABLED || getFlag(FLAG_INVISIBLE))
			return;

		onUpdate(game);
	}

	@Override
	public void draw(Game game, Canvas canvas){
		if(getFlag(FLAG_INVISIBLE))
			return;

		if(Game.USE_OPENGL){
			float drawX = getScreenX();
			float drawY = getScreenY();
			
			
		}else{
			super.preRender(game, canvas);

			float drawX = getScreenX();
			float drawY = getScreenY();
			bounds.getPooled0().set(drawX, drawY, drawX + bounds.getWidth(), drawY + bounds.getHeight());
			
			String resource = getBackground(state);
			resourceCache.setResource(resource);
			resourceCache.draw(game, canvas, this, bounds.getPooled0());
			
			if(!TextUtils.isEmpty(text)){
				if(paint.getTextAlign() == Align.CENTER){
					drawX += bounds.getWidth() / 2f;
					drawY += bounds.getHeight() / 2f + 10f;
				}
				if(getFlag(FLAG_MULTILINE_TEXT))
					RenderUtil.drawMultilineText(game, canvas, lineCache, drawX, drawY, paint);
				else if(getFlag(FLAG_WRAPPED_TEXT)){
					canvas.translate(bounds.getX(), bounds.getY());
					RenderUtil.drawWrappedTextMemorySafe(game, multiLineDrawCache, canvas);
					canvas.translate(-bounds.getX(), -bounds.getY());
				}else
					canvas.drawText(text, drawX, drawY, paint);
			}

			super.postRender(game, canvas);
		}
	}

	@Override
	public int getRenderLayer(){ return RenderLayer.TOP_OVER_ALL; }

	@Override
	public boolean shouldScale(){ return false; }
}
