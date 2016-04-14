package net.site40.rodit.tinyrpg.game.gui2;

import net.site40.rodit.tinyrpg.game.Game;
import net.site40.rodit.tinyrpg.game.gui2.IComponentListener.ComponentListener;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.inputmethod.InputMethodManager;

public class GuiSetName extends LinkedGui{

	public static final int MAX_NAME_LENGTH = 24;
	
	private InputMethodManager im;
	private boolean inputShown;

	public GuiSetName(){
		
	}
	
	@Override
	public void postInit(){
		get("editName").attach(new ComponentListener(){
			public void draw(Game game, Canvas canvas, Component component){
				editNameDraw(game, canvas, component);
			}
		});
	}
	
	public void editNameTouchUp(Game game, MotionEvent event, Component component){
		if(im == null)
			im = (InputMethodManager)game.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
		if(inputShown)
			im.hideSoftInputFromWindow(game.getView().getWindowToken(), 0);
		else
			im.showSoftInput(game.getView(), InputMethodManager.SHOW_FORCED);
		inputShown = !inputShown;
	}
	
	public void editNameKeyUp(Game game, KeyEvent event, Component component){
		Log.d("GuiSetName", "Key event " + event.getUnicodeChar() + ".");
		if(event.getKeyCode() == KeyEvent.KEYCODE_DEL){
			if(component.getText().length() > 0)
				component.setText(component.getText().substring(0, component.getText().length() - 1));
			return;
		}
		if(component.getText().length() > MAX_NAME_LENGTH)
			return;
		component.setText(component.getText() + (char)event.getUnicodeChar());
	}

	public void editNameDraw(Game game, Canvas canvas, Component component){
		int original = component.getPaint().getColor();
		component.getPaint().setColor(Color.WHITE);
		canvas.drawRect(component.getBoundsF(), component.getPaint());
		component.getPaint().setColor(original);
		
		component.flag = Component.FLAG_DONT_NOTIFY_LISTENERS;
		component.draw(canvas, game);
		component.flag = Component.FLAG_DONT_DRAW;
	}
	
	public void btnConfirmUp(Game game, MotionEvent event, Component component){
		if(im != null)
			im.hideSoftInputFromWindow(game.getView().getWindowToken(), 0);
		Component editName = get("editName");
		if(TextUtils.isEmpty(editName.getText()))
			editName.setText("Invalid name.");
		else{
			game.getPlayer().setUsername(editName.getText());
			game.getGuis().hide(this);
			game.getGuis().show("ingame");
			game.getScripts().execute(game, "script/nameset.js", new String[0], new Object[0]);
		}
	}
}
