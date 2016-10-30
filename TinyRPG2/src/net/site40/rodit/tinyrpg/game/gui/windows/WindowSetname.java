package net.site40.rodit.tinyrpg.game.gui.windows;

import static net.site40.rodit.tinyrpg.game.render.MM.EMPTY_OBJECT_ARRAY;
import static net.site40.rodit.tinyrpg.game.render.MM.EMPTY_STRING_ARRAY;
import static net.site40.rodit.tinyrpg.game.render.Strings.DIALOG_CONFIRM_NAME;
import static net.site40.rodit.tinyrpg.game.render.Strings.DIALOG_ENTER_NAME;
import static net.site40.rodit.tinyrpg.game.render.Strings.DIALOG_YES_NO;
import static net.site40.rodit.tinyrpg.game.render.Strings.getString;

import android.graphics.Color;
import net.site40.rodit.tinyrpg.game.Dialog.DialogCallback;
import net.site40.rodit.tinyrpg.game.Game;
import net.site40.rodit.tinyrpg.game.Values;

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
		btnConfirm.setBounds(txtName.getX(), txtName.getY() + txtName.getHeight() + 16f, txtName.getWidth(), 100);
		btnConfirm.addListener(new WindowListener(){
			public void touchUp(final Game game, WindowComponent component){
				txtName.closeKeyboard(game);
				if(txtName.getText().length() == 0)
					game.getHelper().dialog(DIALOG_ENTER_NAME);
				else{
					game.getHelper().dialog(getString(DIALOG_CONFIRM_NAME, txtName.getText()), DIALOG_YES_NO, new DialogCallback(){
						@Override
						public void onSelected(int option){
							if(option == 0){
								game.getPlayer().setUsername(txtName.getText());
								WindowSetname.this.hide();
								game.getScripts().execute(game, "script/init/nameset.js", EMPTY_STRING_ARRAY, EMPTY_OBJECT_ARRAY);
							}
						}
					});
				}
			}
		});
		add(btnConfirm);
	}
}
