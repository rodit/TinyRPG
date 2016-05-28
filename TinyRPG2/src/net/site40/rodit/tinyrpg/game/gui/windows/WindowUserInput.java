package net.site40.rodit.tinyrpg.game.gui.windows;

import net.site40.rodit.tinyrpg.game.Game;
import net.site40.rodit.tinyrpg.game.Values;

public class WindowUserInput extends Window{
	
	public static enum InputResult{
		CANCELLED;
	}
	
	public static interface InputCallback{
		
		public boolean onResult(WindowUserInput window, Object result);
	}
	
	private String msg;
	private String initTxt;

	private WindowTextBoxComponent inputBox;
	private WindowComponent txtMsg;
	private WindowComponent btnCancel;
	private WindowComponent btnOk;
	
	private InputCallback callback;
	
	public WindowUserInput(Game game, String msg, String initTxt){
		this(game, msg, initTxt, null);
	}
	
	public WindowUserInput(Game game, String msg, String initTxt, InputCallback callback){
		super(game);
		this.msg = msg;
		this.initTxt = initTxt;
		this.callback = callback;
		initialize(game);
	}
	
	@Override
	public void initialize(Game game){
		if(initTxt == null)
			return;
		
		this.setBounds(360, 180, 640, 280);
		
		this.inputBox = new WindowTextBoxComponent(WindowTextBoxComponent.INPUT_TYPE_NUMERIC);
		inputBox.setName("input");
		inputBox.setText(initTxt == null ? "" : initTxt);
		inputBox.getPaint().setTextSize(Values.FONT_SIZE_MEDIUM);
		inputBox.setBounds(32f, 100f, getWidth() - 64f, 64f);
		add(inputBox);
		
		this.txtMsg = new WindowComponent("txtMsg");
		txtMsg.setText(msg);
		txtMsg.getPaint().setTextSize(Values.FONT_SIZE_MEDIUM);
		txtMsg.setX(getWidth() / 2f);
		txtMsg.setY(56f);
		add(txtMsg);
		
		this.btnCancel = new WindowComponent("btnCancel");
		btnCancel.setText("Back");
		btnCancel.getPaint().setTextSize(Values.FONT_SIZE_MEDIUM - 8f);
		btnCancel.setBackground(WindowComponent.STATE_IDLE, "gui/button.png");
		btnCancel.setBackground(WindowComponent.STATE_DOWN, "gui/button_selected.png");
		btnCancel.setBounds(inputBox.getX(), inputBox.getY() + inputBox.getHeight() + 24f, 192f, 64f);
		btnCancel.addListener(new WindowListener(){
			public void touchUp(Game game, WindowComponent component){
				if(callback != null && callback.onResult(WindowUserInput.this, InputResult.CANCELLED))
					hide();
			}
		});
		add(btnCancel);
		
		this.btnOk = new WindowComponent("btnOk");
		btnOk.setText("OK");
		btnOk.getPaint().setTextSize(Values.FONT_SIZE_MEDIUM - 8f);
		btnOk.setBackground(WindowComponent.STATE_IDLE, "gui/button.png");
		btnOk.setBackground(WindowComponent.STATE_DOWN, "gui/button_selected.png");
		btnOk.setBounds(getWidth() - 192f - 32f, btnCancel.getY(), 192f, 64f);
		btnOk.addListener(new WindowListener(){
			public void touchUp(Game game, WindowComponent component){
				if(callback != null && callback.onResult(WindowUserInput.this, inputBox.getText()))
					hide();
			}
		});
		add(btnOk);
	}
}
