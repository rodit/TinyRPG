package net.site40.rodit.tinyrpg.game.gui;

import net.site40.rodit.tinyrpg.game.Game;
import net.site40.rodit.tinyrpg.game.Values;
import net.site40.rodit.tinyrpg.game.gui.ComponentListener.ComponentListenerImpl;
import net.site40.rodit.util.RenderUtil;
import android.graphics.Canvas;

public class GuiMessage extends Gui{

	public GuiMessage(){
		super("");
	}
	
	@Override
	public void init(){
		Component txtMessage = new Component("txtMessage"){
			@Override
			public void draw(Game game, Canvas canvas){
				int state = canvas.save();
				canvas.translate(bounds.getX(), bounds.getY());
				RenderUtil.drawWrappedText(game, this.getText(), 1000, paint, canvas);
				canvas.restoreToCount(state);
			}
		};
		txtMessage.setX(140f);
		txtMessage.setY(64f);
		txtMessage.getPaint().setTextSize(Values.FONT_SIZE_BIG);
		add(txtMessage);
		
		Component btnOk = new Component("btnOk");
		btnOk.setBackground("gui/button.png");
		btnOk.setBackgroundSelected("gui/button_selected.png");
		btnOk.getPaint().setTextSize(Values.FONT_SIZE_MEDIUM);
		btnOk.setWidth(320f);
		btnOk.setHeight(128f);
		btnOk.setX(640f - btnOk.getBounds().getWidth() / 2f);
		btnOk.setY(1280f - btnOk.getBounds().getHeight() - 32f);
		btnOk.setText("OK");
		btnOk.addListener(new ComponentListenerImpl(){
			public void touchUp(Component component, Game game){
				Object guiO = game.getGlobal("gui_message_return");
				Gui returnGui = guiO == null ? null : (Gui)guiO;
				game.getGuis().hide(GuiMessage.class);
				if(returnGui != null)
					game.getGuis().show(returnGui);
			}
		});
		add(btnOk);
	}
}
