package net.site40.rodit.tinyrpg.game.gui;

import net.site40.rodit.tinyrpg.game.Game;
import android.graphics.Color;
import android.text.TextUtils;

public class GuiSetname extends Gui{
	
	TextboxComponent editName;

	public GuiSetname(){
		super("");
	}

	@Override
	public void init(){
		Component txtTitle = new Component("txtTitle", "What's Your Name?");
		txtTitle.getPaint().setTextSize(32f);
		txtTitle.getPaint().setColor(Color.WHITE);
		txtTitle.setX(360);
		txtTitle.setY(100);
		add(txtTitle);

		editName = new TextboxComponent();
		editName.getPaint().setTextSize(24f);
		editName.setName("editName");
		editName.setText("New Player");
		editName.setWidth(768);
		editName.setHeight(100);
		editName.setX(128);
		editName.setY(300);
		add(editName);

		Component btnConfirm = new Component("btnConfirm", "Confirm");
		btnConfirm.getPaint().setColor(Color.WHITE);
		btnConfirm.getPaint().setTextSize(18f);
		btnConfirm.setX(editName.getX() + editName.getWidth() + 20);
		btnConfirm.setY(editName.getY());
		btnConfirm.setWidth(236);
		btnConfirm.setHeight(100);
		btnConfirm.addListener(new ComponentListener(){
			public void update(Component component, Game game){}
			public void touchDown(Component component, Game game){}
			public void touchUp(Component component, Game game){
				if(TextUtils.isEmpty(editName.getText()))
					editName.setText("Invalid name.");
				else{
					editName.closeKeyboard(game);
					game.getPlayer().setUsername(editName.getText());
					game.getGuis().hide(GuiSetname.class);
					game.getGuis().show(GuiIngame.class);
					game.getScripts().execute(game, "script/nameset.js", new String[0], new Object[0]);
				}
			}
		});
		add(btnConfirm);
	}
}
