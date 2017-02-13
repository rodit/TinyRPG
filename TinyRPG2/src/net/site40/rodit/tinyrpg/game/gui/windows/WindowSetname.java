package net.site40.rodit.tinyrpg.game.gui.windows;

import static net.site40.rodit.tinyrpg.game.render.Strings.getString;
import net.site40.rodit.tinyrpg.game.Dialog.DialogCallback;
import net.site40.rodit.tinyrpg.game.Game;
import net.site40.rodit.tinyrpg.game.Values;
import net.site40.rodit.tinyrpg.game.render.Strings;
import android.graphics.Color;

public class WindowSetname extends Window{

	private WindowComponent txtTitle;
	private WindowTextBoxComponent txtName;
	private WindowComponent btnConfirm;

	public WindowSetname(Game game){
		super(game, false);
	}

	@Override
	public void initialize(Game game){
		this.setBounds(0f, 0f, 1280f, 720f);

		this.txtTitle = new WindowComponent("txtTitle");
		txtTitle.setText("What's Your Name?");
		txtTitle.getPaint().setTextSize(32f);
		txtTitle.getPaint().setColor(Color.WHITE);
		txtTitle.setX(360);
		txtTitle.setY(100);
		add(txtTitle);

		this.txtName = new WindowTextBoxComponent(WindowTextBoxComponent.INPUT_TYPE_ALPHA_NUMERIC, Color.BLACK, Color.WHITE, 20);
		txtName.setName("txtName");
		txtName.getPaint().setTextSize(Values.FONT_SIZE_MEDIUM);
		txtName.setBounds(128, 300, 768, 100);
		add(txtName);

		this.btnConfirm = new WindowComponent("btnConfirm");
		btnConfirm.setBackground(WindowComponent.STATE_IDLE, "gui/button.png");
		btnConfirm.setBackground(WindowComponent.STATE_DOWN, "gui/button_selected.png");
		btnConfirm.setText("Confirm");
		btnConfirm.getPaint().setTextSize(Values.FONT_SIZE_MEDIUM);
		btnConfirm.setBounds(txtName.getBounds().getX(), txtName.getBounds().getY() + txtName.getBounds().getHeight() + 16f, txtName.getBounds().getWidth(), 100);
		btnConfirm.addListener(new WindowListener(){
			public void touchUp(final Game game, WindowComponent component){
				txtName.closeKeyboard(game);
				if(txtName.getText().length() == 0)
					game.getHelper().dialog(Strings.Dialog.ENTER_NAME);
				else{
					game.getHelper().dialog(getString(Strings.Dialog.CONFIRM_NAME, txtName.getText()), Strings.Dialog.YES_NO, new DialogCallback(){
						@Override
						public void onSelected(int option){
							if(option == 0){
								game.getPlayer().setUsername(txtName.getText());
								WindowSetname.this.close();
								game.getScript().runScript(game, "script/init/nameset.js");
							}
						}
					});
				}
			}
		});
		add(btnConfirm);
	}
}
