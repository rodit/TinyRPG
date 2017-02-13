package net.site40.rodit.tinyrpg.game.gui.windows;

import java.io.IOException;

import net.site40.rodit.tinyrpg.game.Game;
import net.site40.rodit.tinyrpg.game.Values;

public class WindowOptions extends Window{

	private WindowComponent txtTitle;
	private WindowCheckboxComponent chkRenderHardware;
	private WindowCheckboxComponent chkBgMusic;
	private WindowCheckboxComponent chkSoundFx;
	private WindowComponent btnBack;

	public WindowOptions(Game game){
		super(game);
	}
	
	@Override
	public void initialize(Game game){
		this.setBounds(64, 32, 1152, 640);
		
		this.txtTitle = new WindowComponent("txtTitle");
		txtTitle.setText("Game Options");
		txtTitle.getPaint().setTextSize(Values.FONT_SIZE_MEDIUM);
		txtTitle.setX(bounds.getWidth() / 2f);
		txtTitle.setY(78f);
		add(txtTitle);
		
		this.chkRenderHardware = new WindowCheckboxComponent("chkRenderHardware");
		chkRenderHardware.setText("Hardware Rendering");
		chkRenderHardware.getPaint().setTextSize(Values.FONT_SIZE_SMALL);
		chkRenderHardware.setBackgroundDefault("gui/checkbox.png");
		chkRenderHardware.setBackgroundChecked("gui/checkbox_checked.png");
		chkRenderHardware.setX(32f);
		chkRenderHardware.setY(128f);
		chkRenderHardware.setWidth(64f);
		chkRenderHardware.setHeight(64f);
		attachToOption(game, chkRenderHardware, "hardware_render");
		add(chkRenderHardware);
		
		this.chkBgMusic = new WindowCheckboxComponent("chkBgMusic");
		chkBgMusic.setText("Background Music");
		chkBgMusic.getPaint().setTextSize(Values.FONT_SIZE_SMALL);
		chkBgMusic.setBackgroundDefault("gui/checkbox.png");
		chkBgMusic.setBackgroundChecked("gui/checkbox_checked.png");
		chkBgMusic.setX(chkRenderHardware.getBounds().getX());
		chkBgMusic.setY(chkRenderHardware.getBounds().getY() + chkRenderHardware.getBounds().getHeight() + 4f);
		chkBgMusic.setWidth(64f);
		chkBgMusic.setHeight(64f);
		attachToOption(game, chkBgMusic, "music_bg");
		add(chkBgMusic);
		
		this.btnBack = new WindowComponent("btnBack");
		btnBack.setBackground(WindowComponent.STATE_IDLE, "gui/button.png");
		btnBack.setBackground(WindowComponent.STATE_DOWN, "gui/button_selected.png");
		btnBack.setText("Back");
		btnBack.setX(32f);
		btnBack.setY(bounds.getHeight() - 92f);
		btnBack.setWidth(164f);	
		btnBack.setHeight(72f);
		btnBack.addListener(new WindowListener(){
			public void touchUp(Game game, WindowComponent component){
				WindowOptions.this.close();
			}
		});
		add(btnBack);
	}
	
	protected void attachToOption(final Game game, final WindowCheckboxComponent chk, final String optionName){
		chk.setState(game.getOptions().getBool(optionName) ? WindowCheckboxComponent.STATE_DOWN : WindowCheckboxComponent.STATE_IDLE);
		chk.addListener(new WindowListener(){
			public void touchUp(Game game, WindowComponent component){
				game.getOptions().put(optionName, chk.getState() == WindowCheckboxComponent.STATE_DOWN);
				try{
					game.getSaves().saveOptions(game);
				}catch(IOException e){ e.printStackTrace(); }
			}
		});
	}
	
	protected void attachToOption(final Game game, final WindowTextBoxComponent txt, final String optionName){
		txt.setText(game.getOptions().get(optionName));
		txt.addListener(new WindowListener(){
			public void touchUp(Game game, WindowComponent component){
				game.getOptions().put(optionName, txt.getText());
				try{
					game.getSaves().saveOptions(game);
				}catch(IOException e){ e.printStackTrace(); }
			}
		});
	}
	
	protected void attachToOption(final Game game, final WindowSliderComponent slider, final String optionName){
		slider.setValue(game.getOptions().getFloat(optionName));
		slider.addListener(new WindowListener(){
			public void touchUp(Game game, WindowComponent component){
				game.getOptions().put(optionName, slider.getValue());
				try{
					game.getSaves().saveOptions(game);
				}catch(IOException e){ e.printStackTrace(); }
			}
		});
	}
}
