package net.site40.rodit.tinyrpg.game.gui;

import net.site40.rodit.tinyrpg.game.Game;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.inputmethod.InputMethodManager;

public class TextboxComponent extends Component{

	private boolean password;
	private boolean hasFocus;
	private int textColor;
	private int backgroundColor;
	private InputMethodManager im;
	private int maxLength = 32;

	public TextboxComponent(){
		this(Color.BLACK, Color.WHITE);
	}

	public TextboxComponent(int textColor, int backgroundColor){
		super();
		this.textColor = textColor;
		this.backgroundColor = backgroundColor;
	}

	public boolean isPassword(){
		return password;
	}

	public void setPassword(boolean password){
		this.password = password;
	}

	public boolean hasFocus(){
		return hasFocus;
	}
	
	public void setFocus(boolean hasFocus){
		this.hasFocus = hasFocus;
	}

	public int getMaxLength(){
		return maxLength;
	}

	public void setMaxLength(int maxLength){
		this.maxLength = maxLength;
	}
	
	public void closeKeyboard(Game game){
		if(im != null)
			im.hideSoftInputFromWindow(game.getView().getWindowToken(), 0);
	}

	public void keyEvent(KeyEvent event, Game game){
		if(!hasFocus)
			return;
		if(event.getKeyCode() == KeyEvent.KEYCODE_DEL){
			if(getText().length() > 0)
				setText(getText().substring(0, getText().length() - 1));
			return;
		}
		if(getText().length() >= maxLength)
			return;
		setText(getText() + (char)event.getUnicodeChar());
	}

	public String makePassword(String text){
		String pass = "";
		for(int i = 0; i < text.length(); i++)
			pass += "*";
		return pass;
	}

	@Override
	public void input(MotionEvent event, Game game){
		if(event.getAction() != MotionEvent.ACTION_UP)
			return;
		if(im == null)
			im = (InputMethodManager)game.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
		if(!hasFocus){
			im.showSoftInput(game.getView(), InputMethodManager.SHOW_FORCED);
			hasFocus = true;
			for(Component c : getGui().getComponents())
				if(c instanceof TextboxComponent && c != this)
					((TextboxComponent)c).setFocus(false);
		}else{
			im.hideSoftInputFromWindow(game.getView().getWindowToken(), 0);
			hasFocus = false;
		}
	}

	@Override
	public void draw(Game game, Canvas canvas){
		getPaint().setColor(backgroundColor);
		canvas.drawRect(bounds.get(), getPaint());
		getPaint().setColor(textColor);
		drawCenteredText(canvas, getText(), getBounds());
	}
}
