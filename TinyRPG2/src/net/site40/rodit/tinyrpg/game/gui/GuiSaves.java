package net.site40.rodit.tinyrpg.game.gui;

import java.io.IOException;
import java.util.ArrayList;

import net.site40.rodit.tinyrpg.game.Dialog.DialogCallback;
import net.site40.rodit.tinyrpg.game.Game;
import net.site40.rodit.tinyrpg.game.Values;
import net.site40.rodit.tinyrpg.game.gui.ComponentListener.ComponentListenerImpl;
import net.site40.rodit.tinyrpg.game.saves.SaveGame;
import android.graphics.Canvas;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;

public class GuiSaves extends Gui{

	public static final int SAVES_PER_PAGE = 5;
	public static final float Y_OFFSET = 256;

	private int selectedIndex = 0;

	public GuiSaves(){
		super("");
	}
	
	@Override
	public void onShown(){
		selectedIndex = 0;
	}

	@Override
	public void init(){
		Component txtTitle = new Component("txtTitle");
		txtTitle.setText("Saves");
		txtTitle.setX(640);
		txtTitle.setY(72f);
		txtTitle.getPaint().setTextSize(Values.FONT_SIZE_LARGE);
		add(txtTitle);
		
		Component savesRender = new Component("savesRender"){			
			@Override
			public void draw(Game game, Canvas canvas){
				canvas.drawRect(getBoundsF(), paint);
				
				ArrayList<SaveGame> all = game.getSaves().all();
				for(int i = selectedIndex; i < all.size() && i < selectedIndex + SAVES_PER_PAGE; i++){
					SaveGame save = all.get(i);
					String saveText = save.getName() + " " + save.getHumanReadableTime();
					if(i == selectedIndex)
						paint.setTextSize(Values.FONT_SIZE_MEDIUM + 4f);
					else
						paint.setTextSize(Values.FONT_SIZE_MEDIUM);
					canvas.drawText(saveText, 640, Y_OFFSET + (paint.getTextSize() + 2f) * (float)(i - selectedIndex), paint);
				}
			}
		};
		savesRender.getPaint().setStyle(Style.STROKE);
		savesRender.getPaint().setTextAlign(Align.CENTER);
		savesRender.getPaint().setTextSize(Values.FONT_SIZE_MEDIUM);
		savesRender.setX(288f);
		savesRender.setY(192f);
		savesRender.setWidth(720f);
		savesRender.setHeight(592f);
		add(savesRender);

		final Component btnBack = new Component("btnBack", "Back");
		btnBack.setBackground("gui/button.png");
		btnBack.setBackgroundSelected("gui/button_selected.png");
		btnBack.getPaint().setTextSize(Values.FONT_SIZE_MEDIUM);
		btnBack.setWidth(256f);
		btnBack.setHeight(92f);
		btnBack.setX(16f);
		btnBack.setY(720f - btnBack.getHeight());
		btnBack.addListener(new ComponentListenerImpl(){
			public void touchUp(Component component, Game game){
				game.getGuis().hide(GuiSaves.class);
				game.getGuis().show(GuiMenu.class);
			}
		});
		add(btnBack);

		final Component btnDelete = new Component("btnDelete", "Delete");
		final Component btnLoad = new Component("btnLoad", "Load");
		btnDelete.getPaint().setTextSize(Values.FONT_SIZE_MEDIUM);
		btnLoad.getPaint().setTextSize(Values.FONT_SIZE_MEDIUM);
		btnLoad.setBackground("gui/button.png");
		btnLoad.setBackgroundSelected("gui/button_selected.png");
		btnLoad.setWidth(256f);
		btnLoad.setHeight(92f);
		btnLoad.setX(1280f - btnLoad.getWidth() - 16f);
		btnLoad.setY(btnBack.getY());
		btnLoad.addListener(new ComponentListenerImpl(){
			public void touchUp(Component component, Game game){
				SaveGame save = game.getSaves().all().get(selectedIndex);
				try{
					save.load(game);
				}catch(IOException e){
					e.printStackTrace();
					game.getHelper().dialog("There was an error while loading this save file.");
				}
			}

			public void update(Component component, Game game){
				if(selectedIndex >= game.getSaves().all().size()){
					btnLoad.setFlag(Component.INACTIVE);
					btnDelete.setFlag(Component.INACTIVE);
				}else{
					btnLoad.setFlag(0);
					btnDelete.setFlag(0);
				}
			}
		});
		add(btnLoad);

		btnDelete.setBackground("gui/button.png");
		btnDelete.setBackgroundSelected("gui/button_selected.png");
		btnDelete.setWidth(256f);
		btnDelete.setHeight(92f);
		btnDelete.setX(1280f - btnDelete.getWidth() - 16f);
		btnDelete.setY(btnLoad.getY() - btnLoad.getHeight() - 16f);
		btnDelete.addListener(new ComponentListenerImpl(){
			private Game game;
			public void touchUp(Component component, Game game){
				this.game = game;
				game.getHelper().dialog("Are you sure you would like to delete this save? This operation is not reversable.", new String[] { "Yes", "No" }, deleteCallback);
			}
			
			public DialogCallback deleteCallback = new DialogCallback(){
				@Override
				public void onSelected(int option){
					if(option == 0){
						SaveGame save = game.getSaves().all().get(selectedIndex);
						if(!save.getFile().delete())
							game.getHelper().dialog("There was an error while deleting this save file.");
					}
				}
			};
		});
		add(btnDelete);
		
		Component btnUp = new Component("btnUp");
		btnUp.setBackground("gui/scroll_up.png");
		btnUp.setBackgroundSelected("gui/scroll_up_selected.png");
		btnUp.setX(savesRender.getX() + savesRender.getWidth() + 16f);
		btnUp.setY(savesRender.getY() + 64f);
		btnUp.setWidth(64f);
		btnUp.setHeight(64f);
		btnUp.addListener(new ComponentListenerImpl(){
			public void touchUp(Component component, Game game){
				if(selectedIndex > 0)
					selectedIndex--;
				else
					selectedIndex = game.getSaves().all().size() - 1;
			}
		});
		add(btnUp);
		
		Component btnDown = new Component("btnDown");
		btnDown.setBackground("gui/scroll_down.png");
		btnDown.setBackgroundSelected("gui/scroll_down_selected.png");
		btnDown.setX(savesRender.getX() + savesRender.getWidth() + 16f);
		btnDown.setY(savesRender.getY() + 192f);
		btnDown.setWidth(64f);
		btnDown.setHeight(64f);
		btnDown.addListener(new ComponentListenerImpl(){
			public void touchUp(Component component, Game game){
				if(selectedIndex < game.getSaves().all().size() - 1)
					selectedIndex++;
				else
					selectedIndex = 0;
			}
		});
		add(btnDown);
	}
}
