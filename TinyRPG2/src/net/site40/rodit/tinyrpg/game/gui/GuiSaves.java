package net.site40.rodit.tinyrpg.game.gui;

import java.io.IOException;
import java.util.ArrayList;

import net.site40.rodit.tinyrpg.R;
import net.site40.rodit.tinyrpg.game.Game;
import net.site40.rodit.tinyrpg.game.gui.ComponentListener.ComponentListenerImpl;
import net.site40.rodit.tinyrpg.game.render.Strings.Values;
import net.site40.rodit.tinyrpg.game.saves.SaveSlot;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;

public class GuiSaves extends Gui{

	public static final int SAVES_PER_PAGE = 5;
	public static final float Y_OFFSET = 256;

	private int selectedIndex = 0;

	private ArrayList<SaveSlot> slots;

	public GuiSaves(){
		super("");
	}

	@Override
	public void onShown(){
		selectedIndex = 0;
	}

	@Override
	public void onHidden(){
		readSaves = false;
		if(slots != null)
			slots.clear();
		slots = null;
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
				canvas.drawRect(getBounds().get(), paint);

				for(int i = selectedIndex; i < slots.size() && i < selectedIndex + SAVES_PER_PAGE; i++){
					SaveSlot save = slots.get(i);
					String saveText = save.getRoot().getName() + " " + save.getHumanReadablePlayTime();
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
		btnBack.setY(720f - btnBack.getBounds().getHeight());
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
		btnLoad.setX(1280f - btnLoad.getBounds().getWidth() - 16f);
		btnLoad.setY(btnBack.getBounds().getY());
		btnLoad.addListener(new ComponentListenerImpl(){
			public void touchUp(Component component, Game game){
				try{
					game.getSaves().loadSlot(game.getSaves().getLastSlot());
					game.getSaves().load(game);
					game.removeObject(((GuiMenu)game.getGuis().get(GuiMenu.class)).r);
					game.getGuis().hide(GuiSaves.class);
					game.getGuis().show(GuiIngame.class);
					game.getAudio().get(R.raw.menu_music).stop();
				}catch(IOException e){
					e.printStackTrace();
					game.showMessage("There was an error while loading your save.\n" + e.getClass().getName() + ": " + e.getMessage() + "\n" + e.getLocalizedMessage(), GuiSaves.this);
				}
			}

			public void update(Component component, Game game){
				if(selectedIndex >= slots.size()){
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
		btnDelete.setX(1280f - btnDelete.getBounds().getWidth() - 16f);
		btnDelete.setY(btnLoad.getBounds().getY() - btnLoad.getBounds().getHeight() - 16f);
		btnDelete.addListener(new ComponentListenerImpl(){
			public void touchUp(Component component, final Game game){
				DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						switch (which){
						case DialogInterface.BUTTON_POSITIVE:
							SaveSlot save = slots.get(selectedIndex);
							if(!save.destroy())
								game.getHelper().dialog("There was an error while deleting this save file.");
							else
								slots = game.getSaves().getSlots();
							break;
						default:
						case DialogInterface.BUTTON_NEGATIVE:
							break;
						}
					}
				};

				AlertDialog.Builder builder = new AlertDialog.Builder(game.getContext());
				builder.setMessage("Are you sure you would like to delete this save? This operation cannot be undone.").setPositiveButton("Yes", dialogClickListener).setNegativeButton("No", dialogClickListener).show();
			}
		});
		add(btnDelete);

		Component btnUp = new Component("btnUp");
		btnUp.setBackground("gui/scroll_up.png");
		btnUp.setBackgroundSelected("gui/scroll_up_selected.png");
		btnUp.setX(savesRender.getBounds().getX() + savesRender.getBounds().getWidth() + 16f);
		btnUp.setY(savesRender.getBounds().getY() + 64f);
		btnUp.setWidth(64f);
		btnUp.setHeight(64f);
		btnUp.addListener(new ComponentListenerImpl(){
			public void touchUp(Component component, Game game){
				if(selectedIndex > 0)
					selectedIndex--;
				else
					selectedIndex = slots.size() - 1;
			}
		});
		add(btnUp);

		Component btnDown = new Component("btnDown");
		btnDown.setBackground("gui/scroll_down.png");
		btnDown.setBackgroundSelected("gui/scroll_down_selected.png");
		btnDown.setX(savesRender.getBounds().getX() + savesRender.getBounds().getWidth() + 16f);
		btnDown.setY(savesRender.getBounds().getY() + 192f);
		btnDown.setWidth(64f);
		btnDown.setHeight(64f);
		btnDown.addListener(new ComponentListenerImpl(){
			public void touchUp(Component component, Game game){
				if(selectedIndex < slots.size() - 1)
					selectedIndex++;
				else
					selectedIndex = 0;
			}
		});
		add(btnDown);
	}

	private boolean readSaves;

	@Override
	public void update(Game game){
		super.update(game);
		if(!readSaves){
			readSaves = true;
			slots = game.getSaves().getSlots();
		}
	}
}
