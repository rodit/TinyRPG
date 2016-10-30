package net.site40.rodit.tinyrpg.game.gui.windows;

import java.util.ArrayList;

import android.graphics.Canvas;
import android.graphics.Paint.Align;
import android.graphics.RectF;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.KeyEvent;
import android.view.MotionEvent;
import net.site40.rodit.tinyrpg.game.Game;
import net.site40.rodit.tinyrpg.game.GameObject;
import net.site40.rodit.tinyrpg.game.render.ResourceWrapper;
import net.site40.rodit.util.RenderUtil;

public class WindowComponent extends GameObject{

	public static final int FLAG_NONE = 0;
	public static final int FLAG_DISABLED = 1;
	public static final int FLAG_INVISIBLE = 2;
	public static final int FLAG_MULTILINE_TEXT = 3;

	public static final int STATE_IDLE = 0;
	public static final int STATE_DOWN = 1;
	public static final int STATE_DISABLED = 2;

	private float x;
	private float y;
	private float width;
	private float height;

	private Window parent;
	private String name;
	private boolean[] flags;
	private int state;
	private SparseArray<String> backgrounds;
	private boolean focus;
	private String text;
	private ArrayList<WindowListener> listeners;

	public WindowComponent(){
		this("");
	}

	public WindowComponent(String name){
		super();
		paint.setTextAlign(Align.CENTER);
		paint.setTextSize(12f);

		this.x = y = width = height = 0;

		this.name = name;
		this.flags = new boolean[8];
		this.state = STATE_IDLE;
		this.backgrounds = new SparseArray<String>();
		this.focus = false;
		this.text = "";
		this.listeners = new ArrayList<WindowListener>();
	}

	public float getX(){
		return x;
	}

	public float getScreenX(){
		return (parent == null ? 0 : parent.getX()) + x;
	}

	public void setX(float x){
		this.x = x;
	}

	public float getY(){
		return y;
	}

	public float getScreenY(){
		return (parent == null ? 0 : parent.getY()) + y;
	}

	public void setY(float y){
		this.y = y;
	}

	public float getWidth(){
		return width;
	}

	public void setWidth(float width){
		this.width = width;
	}

	public float getHeight(){
		return height;
	}

	public void setHeight(float height){
		this.height = height;
	}

	public RectF getBoundsF(){
		return new RectF(getX(), getY(), getX() + getWidth(), getY() + getHeight());
	}

	public RectF getScreenBoundsF(){
		return new RectF(getScreenX(), getScreenY(), getScreenX() + getWidth(), getScreenY() + getHeight());
	}

	public void setBounds(float x, float y, float width, float height){
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
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
		MotionEvent motionEvent = MotionEvent.obtain(downTime, eventTime, type, x, y, metaState);
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

		super.preRender(game, canvas);

		float drawX = getScreenX();
		float drawY = getScreenY();

		String resource = getBackground(state);

		ResourceWrapper wrapper = new ResourceWrapper(game.getResources().getObject(resource));
		wrapper.draw(game, canvas, drawX, drawY, getWidth(), getHeight(), paint);
		wrapper.dispose();

		if(!TextUtils.isEmpty(text)){
			if(paint.getTextAlign() == Align.CENTER){
				drawX += getWidth() / 2f;
				drawY += getHeight() / 2f + 10f;
			}
			if(getFlag(FLAG_MULTILINE_TEXT))
				RenderUtil.drawMultilineText(game, canvas, text, drawX, drawY, paint);
			else
				canvas.drawText(text, drawX, drawY, paint);
		}

		super.postRender(game, canvas);
	}

	@Override
	public RenderLayer getRenderLayer(){ return RenderLayer.TOP_ALL; }

	@Override
	public boolean shouldScale(){ return false; }
}
