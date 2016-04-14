package net.site40.rodit.tinyrpg.game.gui;

import net.site40.rodit.tinyrpg.game.Dialog;
import net.site40.rodit.tinyrpg.game.Game;
import net.site40.rodit.tinyrpg.game.Input;
import net.site40.rodit.util.RenderUtil;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.graphics.RectF;
import android.view.View;

public class GuiOptions extends Gui{

	private int selected = 0;

	public GuiOptions(){
		super("");
	}
	
	@Override
	public void onShown(){
		selected = 0;
	}
	
	@Override
	public void init(){
		Component bg = new Component(){
			private final RectF BOUNDS_MENU = new RectF(480 - 256, 360 - 256, 480 + 256, 360 + 256);
			private final String[] OPTIONS = new String[] { "Render Mode: Software", "Back" };
			
			private long keyUpDown = 0L;
			private long keyDownDown = 0L;
			
			private void select(Game game){
				switch(selected){
				case 0:
					if(game.getGlobal("render_mode").equals("software")){
						game.getView().setLayerType(View.LAYER_TYPE_HARDWARE, null);
						game.setGlobal("render_mode", "hardware");
						OPTIONS[0] = "Render Mode: Hardware";
					}
					if(game.getGlobal("render_mode").equals("hardware")){
						game.getView().setLayerType(View.LAYER_TYPE_SOFTWARE, null);
						game.setGlobal("render_mode", "software");
						OPTIONS[0] = "Render Mode: Software";
					}
					break;
				case 1:
					game.getGuis().hide(GuiOptions.class);
					game.getGuis().show(GuiIngameMenu.class);
					break;
				}
			}
			
			public void moveUp(){
				if(selected == 0)
					selected = OPTIONS.length - 1;
				else
					selected--;
			}

			public void moveDown(){
				if(selected == OPTIONS.length - 1)
					selected = 0;
				else
					selected++;
			}
			
			@Override
			public void update(Game game){
				super.update(game);
				Input input = game.getInput();
				input.allowMovement(false);
				if(input.isDown(Input.KEY_UP))
					keyUpDown += game.getDelta();
				else
					keyUpDown = 0L;
				if(input.isDown(Input.KEY_DOWN))
					keyDownDown += game.getDelta();
				else
					keyDownDown = 0L;
				if(keyUpDown >= Dialog.INPUT_DELAY_CURSOR || input.isUp(Input.KEY_UP)){
					moveUp();
					keyUpDown = 0L;
				}
				if(keyDownDown >= Dialog.INPUT_DELAY_CURSOR || input.isUp(Input.KEY_DOWN)){
					moveDown();
					keyDownDown = 0L;
				}
				if(input.isUp(Input.KEY_ACTION))
					select(game);
			}
			
			@Override
			public void draw(Game game, Canvas canvas){
				RenderUtil.drawBitmapBox(canvas, game, BOUNDS_MENU, getPaint());
				
				getPaint().setColor(Color.BLACK);
				getPaint().setTextAlign(Align.LEFT);
				getPaint().setTextSize(26f);
				float offX = 92f;
				float offY = 72f;
				for(int i = 0; i < OPTIONS.length; i++){
					canvas.drawText(OPTIONS[i], offX, offY, getPaint());
					if(i == selected)
						canvas.drawText(">", offX - 24, offY, getPaint());
					offY += getPaint().getTextSize() + 15f;
				}
			}
		};
		add(bg);
	}
}
