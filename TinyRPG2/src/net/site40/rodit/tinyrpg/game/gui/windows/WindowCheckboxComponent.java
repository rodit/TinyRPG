package net.site40.rodit.tinyrpg.game.gui.windows;

import net.site40.rodit.tinyrpg.game.Game;
import android.graphics.Canvas;
import android.graphics.Paint.Align;

public class WindowCheckboxComponent extends WindowComponent{
	
	public static final int FLAG_CHECKED = 3;
	private String backgroundIdle;
	private String backgroundChecked;

	public WindowCheckboxComponent(){
		this("");
	}
	
	public WindowCheckboxComponent(String name){
		super(name);
	}
	
	public String getBackgroundChecked(){
		return backgroundChecked;
	}
	
	public void setBackgroundChecked(String backgroundChecked){
		this.backgroundChecked = backgroundChecked;
	}
	
	public boolean isChecked(){
		return getFlag(FLAG_CHECKED);
	}
	
	public void setChecked(boolean checked){
		setFlag(FLAG_CHECKED, checked);
	}
	
	@Override
	public void onTouchUp(Game game){
		setChecked(!isChecked());
		if(isChecked()){
			this.backgroundIdle = getBackground(STATE_IDLE);
			setBackground(STATE_IDLE, backgroundChecked);
			setBackground(STATE_DOWN, backgroundChecked);
		}else{
			setBackground(STATE_IDLE, backgroundIdle);
			setBackground(STATE_DOWN, backgroundIdle);
		}
		super.onTouchUp(game);
	}
	
	@Override
	public void draw(Game game, Canvas canvas){
		String oText = getText();
		setText("");
		super.draw(game, canvas);
		paint.setTextAlign(Align.LEFT);
		canvas.drawText(oText, bounds.getX() + bounds.getWidth() + 8, bounds.getY() + 32, paint);
		setText(oText);
	}
}
