package net.site40.rodit.tinyrpg.game.gui;

import java.util.ArrayList;

import net.site40.rodit.tinyrpg.game.Game;
import net.site40.rodit.tinyrpg.game.object.Bounds;
import net.site40.rodit.tinyrpg.game.object.GameObject;
import net.site40.rodit.tinyrpg.game.render.Animation;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.os.SystemClock;
import android.text.TextUtils;
import android.view.MotionEvent;

public class Component extends GameObject{

	public static final int IGNORE_CUSTOM_CENTERING = 1;
	public static final int INACTIVE = 2;
	
	private Gui parent;
	
	private String name;
	private String text;
	private String background;
	private String backgroundSelected;
	private Gui gui;
	private ArrayList<ComponentListener> listeners;
	private boolean selected;
	protected int flag;

	public int tag;

	public Component(){
		this("", "", "", "", null);
	}
	
	public Component(String name){
		this(name, "");
	}
	
	public Component(String name, String text){
		this(name, text, "", "", null);
	}

	public Component(String name, String text, String background, String backgroundSelected, Gui gui){
		this.name = name;
		this.text = text;
		this.background = background;
		this.backgroundSelected = backgroundSelected;
		this.gui = gui;
		this.listeners = new ArrayList<ComponentListener>();
		this.paint = Game.getDefaultPaint();
		paint.setTextAlign(Align.CENTER);
		paint.setTextSize(12f);
	}
	
	public Gui getParent(){
		return parent;
	}
	
	public void setParent(Gui parent){
		this.parent = parent;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getBackground() {
		return background;
	}

	public void setBackground(String background) {
		this.background = background;
	}
	
	public void setBackgroundDefault(String background){
		setBackground(background);
		setBackgroundSelected(background);
	}

	public String getBackgroundSelected(){
		return this.backgroundSelected;
	}

	public void setBackgroundSelected(String backgroundSelected){
		this.backgroundSelected = backgroundSelected;
	}

	public Gui getGui() {
		return gui;
	}

	public void setGui(Gui gui) {
		this.gui = gui;
	}

	public ArrayList<ComponentListener> getListeners(){
		return listeners;
	}

	public void addListener(ComponentListener listener){
		listeners.add(listener);
	}

	public void removeListener(ComponentListener listener){
		listeners.remove(listener);
	}

	public Paint getPaint(){
		return paint;
	}

	public void setPaint(Paint paint){
		this.paint = paint;
	}

	public boolean isSelected(){
		return selected;
	}
	
	public void setSelected(boolean selected){
		this.selected = selected;
	}

	public void setFlag(int flag){
		this.flag = flag;
	}
	
	public void input(MotionEvent event, Game game){
		if(flag == INACTIVE)
			return;
		
		int action = event.getAction();
		switch(action){
		case MotionEvent.ACTION_DOWN:
			selected = true;
			for(ComponentListener listener : listeners)
				listener.touchDown(this, game);
			break;
		case MotionEvent.ACTION_UP:
			selected = false;
			for(ComponentListener listener : listeners)
				listener.touchUp(this, game);
			break;
		case MotionEvent.ACTION_MOVE:
			if(!bounds.get().contains(event.getX(), event.getY()))
				selected = false;
			else if(!selected){
				selected = true;
				for(ComponentListener listener : listeners)
					listener.touchDown(this, game);
			}
			break;
		}
	}

	public void update(Game game){
		if(flag == INACTIVE)
			return;
		for(ComponentListener listener : listeners)
			listener.update(this, game);
	}
	
	public void simulateInput(Game game, int type){
		long downTime = SystemClock.uptimeMillis();
		long eventTime = SystemClock.uptimeMillis() + 100;
		int metaState = 0;
		MotionEvent motionEvent = MotionEvent.obtain(downTime, eventTime, type, bounds.getX(), bounds.getY(), metaState);
		input(motionEvent, game);
		motionEvent.recycle();
	}
	
	@Override
	public void draw(Game game, Canvas canvas){
		if(flag == INACTIVE)
			return;
		
		super.preRender(game, canvas);
		
		String toUse = (selected && !TextUtils.isEmpty(backgroundSelected)) ? backgroundSelected : background;
		if(toUse != null && !TextUtils.isEmpty(toUse)){
			Object o = game.getResources().getObject(toUse);
			if(o instanceof Bitmap)
				canvas.drawBitmap((Bitmap)o, null, bounds.get(), paint);
			else if(o instanceof Animation){
				canvas.drawBitmap(((Animation)o).getFrame(game.getTime()), null, bounds.get(), paint);
			}
		}
		if(!TextUtils.isEmpty(text)){
			String[] lines = text.split("\n");
			for(int i = 0; i < lines.length; i++){
				if(paint.getTextAlign() == Align.CENTER && flag != IGNORE_CUSTOM_CENTERING)
					drawCenteredText(canvas, lines[i], bounds);
				else
					canvas.drawText(lines[i], bounds.getX(), bounds.getY() + (float)i * paint.getTextSize(), paint);
			}
		}
		
		super.postRender(game, canvas);
	}
	
	@Override
	public int getRenderLayer(){
		return RenderLayer.TOP_OVER_ALL;
	}
	
	@Override
	public boolean shouldScale(){
		return false;
	}
	
	private int dWidth, dNumChars, dStart;
	protected void drawCenteredText(Canvas canvas, String text, Bounds bounds){
		dWidth = (int)bounds.getWidth();
		dNumChars = paint.breakText(text, true, dWidth, null);
		dStart = (text.length() - dNumChars) / 2;
		canvas.drawText(text, dStart, dStart + dNumChars, bounds.getCenterX(), bounds.getCenterY() + paint.getTextSize() / 4, paint);
	}
}
