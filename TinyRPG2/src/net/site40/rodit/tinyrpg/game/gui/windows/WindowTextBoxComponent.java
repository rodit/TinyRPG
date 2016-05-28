package net.site40.rodit.tinyrpg.game.gui.windows;

import net.site40.rodit.tinyrpg.game.Game;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.inputmethod.InputMethodManager;

public class WindowTextBoxComponent extends WindowComponent{

	public static final String CHARS_NUMERIC = "1234567890";

	public static final int INPUT_TYPE_ALPHA_NUMERIC = 0;
	public static final int INPUT_TYPE_NUMERIC = 1;

	private InputMethodManager im;
	private boolean showingInput;

	private int inputType;
	private int textColor;
	private int bgColor;
	private int maxLength;

	public WindowTextBoxComponent(){
		this(INPUT_TYPE_ALPHA_NUMERIC, Color.BLACK, Color.WHITE);
	}

	public WindowTextBoxComponent(int inputType){
		this(inputType, Color.BLACK, Color.WHITE);
	}

	public WindowTextBoxComponent(int inputType, int textColor, int bgColor){
		this(inputType, textColor, bgColor, 128);
	}

	public WindowTextBoxComponent(int inputType, int textColor, int bgColor, int maxLength){
		super();
		this.inputType = inputType;
		this.textColor = textColor;
		this.bgColor = bgColor;
		this.maxLength = maxLength;
		this.setText("");
	}

	public int getInputType(){
		return inputType;
	}

	public int getTextColor(){
		return textColor;
	}

	public int getBgColor(){
		return bgColor;
	}

	public int getMaxLength(){
		return maxLength;
	}

	@Override
	public void onTouchUp(Game game){
		super.onTouchUp(game);

		if(im == null)
			im = (InputMethodManager)game.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
		if(!showingInput){
			showingInput = false;
			im.showSoftInput(game.getView(), InputMethodManager.SHOW_FORCED);
			this.setFocus(true, game);
			for(WindowComponent c : getParent().getComponents())
				if(c instanceof WindowTextBoxComponent && c != this)
					c.setFocus(false, game);
			if(inputType == INPUT_TYPE_NUMERIC)
				//TODO: work out how to show number pad instead of keyboard
				;
		}else{
			im.hideSoftInputFromWindow(game.getView().getWindowToken(), 0);
			showingInput = false;
			this.setFocus(false, game);
		}
	}

	@Override
	public void onKeyInput(Game game, KeyEvent event){
		super.onKeyInput(game, event);

		if(!this.hasFocus())
			return;
		if(event.getKeyCode() == KeyEvent.KEYCODE_DEL){
			if(getText().length() > 0)
				setText(getText().substring(0, getText().length() - 1));
			return;
		}

		if(inputType == INPUT_TYPE_NUMERIC)
			if(!CHARS_NUMERIC.contains(String.valueOf((char)event.getUnicodeChar())))
				return;

		if(getText().length() >= maxLength)
			return;
		setText(getText() + (char)event.getUnicodeChar());
	}

	@Override
	public void draw(Game game, Canvas canvas){
		super.preRender(game, canvas);

		int paintColor = paint.getColor();
		Align align = paint.getTextAlign();
		paint.setColor(bgColor);
		canvas.drawRect(getScreenBoundsF(), paint);
		paint.setColor(textColor);
		if(!TextUtils.isEmpty(getText()))
			canvas.drawText(this.getText(), getScreenX() + this.getWidth() / 2f, getScreenY() + this.getHeight() / 2f + 16f, paint);
		paint.setColor(paintColor);
		paint.setTextAlign(align);

		super.postRender(game, canvas);
	}
}