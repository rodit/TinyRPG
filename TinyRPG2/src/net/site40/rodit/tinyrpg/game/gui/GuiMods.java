package net.site40.rodit.tinyrpg.game.gui;

import java.io.File;
import java.util.ArrayList;

import net.site40.rodit.tinyrpg.game.Game;
import net.site40.rodit.tinyrpg.game.Values;
import net.site40.rodit.tinyrpg.game.gui.ComponentListener.ComponentListenerImpl;
import net.site40.rodit.tinyrpg.game.mod.TinyMod;
import net.site40.rodit.util.RenderUtil;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.util.Log;

public class GuiMods extends Gui{

	public static final int MODS_PER_PAGE = 4;

	private int scrollIndex;
	private int selectedIndex;

	private Component btnLoad;
	private Component btnDelete;

	private ArrayList<TinyMod> mods = new ArrayList<TinyMod>();

	public GuiMods(){
		super("");
	}

	@Override
	public void onShown(){
		selectedIndex = 0;
	}

	@Override
	public void init(){
		Component txtTitle = new Component("txtTitle", "Mods");
		txtTitle.setX(640);
		txtTitle.setY(72f);
		txtTitle.getPaint().setTextSize(Values.FONT_SIZE_LARGE);
		add(txtTitle);

		Component modsRenderer = new Component("modsRenderer"){
			@Override
			public void draw(Game game, Canvas canvas){
				if(mods == null || mods.size() == 0 || game.getMods().modListUpdated()){
					mods = game.getMods().listMods(game);
					for(TinyMod mod : mods)
						try{
							mod.loadInfo(game);
						}catch(Exception e){
							Log.e("GuiMods", "Exception loading mod info.");
							e.printStackTrace();
						}
					game.getMods().handledUpdate();
				}
				paint.setAlpha(255);
				canvas.drawRect(getBoundsF(), paint);
				for(int i = scrollIndex; i < mods.size(); i++){
					TinyMod mod = mods.get(i);
					float x = getX();
					float y = getY() + 4f + (i - scrollIndex) * 156f;
					if(i == selectedIndex){
						paint.setColor(0xF2F2F2);
						paint.setAlpha(170);
						paint.setStyle(Style.FILL);
						canvas.drawRect(new RectF(x, y, x + getWidth(), y + (getHeight() / MODS_PER_PAGE)), paint);
						paint.setStyle(Style.STROKE);
						paint.setColor(Color.WHITE);
					}
					if(game.getMods().isModEnabled(game, mod))
						paint.setAlpha(255);
					else
						paint.setAlpha(160);
					canvas.drawBitmap(mod.getIcon(), null, new RectF(x, y, x + 146f, y + 146f), paint);
					paint.setTextSize(Values.FONT_SIZE_SMALL);
					canvas.drawText(mod.getInfo().getShowName(), x + 164f, y + 32f, paint);
					int save = canvas.save();
					canvas.translate(getX() + 164f, y + paint.getTextSize());
					paint.setTextSize(Values.FONT_SIZE_TINY);
					RenderUtil.drawWrappedText(game, mod.getInfo().getDescription(), (int)(getWidth() - 164f - 32f), paint, canvas);
					canvas.restoreToCount(save);
				}
			}
		};
		modsRenderer.getPaint().setStyle(Style.STROKE);
		modsRenderer.getPaint().setTextAlign(Align.LEFT);
		modsRenderer.getPaint().setTextSize(Values.FONT_SIZE_SMALL);
		modsRenderer.setX(288f);
		modsRenderer.setY(192f);
		modsRenderer.setWidth(720f);
		modsRenderer.setHeight(592f);
		add(modsRenderer);

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
				game.getGuis().hide(GuiMods.class);
				game.getGuis().show(GuiMenu.class);
			}
		});
		add(btnBack);

		btnDelete = new Component("btnDelete", "Delete");
		btnLoad = new Component("btnLoad", "Enable");
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
				TinyMod mod = mods.get(selectedIndex);
				try{
					if(game.getMods().isModEnabled(game, mod)){
						game.getMods().disableMod(game, mod);
						onCurrentModChange(game);
					}else{
						game.getMods().enableMod(game, mod);
						mod.load(game);
						onCurrentModChange(game);
					}
				}catch(Exception e){
					e.printStackTrace();
					game.getHelper().dialog("There was an error while enabling the mod.");
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
			public void touchUp(Component component, final Game game){
				DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						switch (which){
						case DialogInterface.BUTTON_POSITIVE:
							TinyMod mod = mods.get(selectedIndex);
							File modFile = new File(game.getContext().getFilesDir(), TinyMod.MOD_DIR + "/" + mod.getFile());
							if(!modFile.delete())
								game.getHelper().dialog("There was an error while deleting this save file.");
							else
								game.getMods().updated();
							break;
						default:
						case DialogInterface.BUTTON_NEGATIVE:
							break;
						}
					}
				};

				AlertDialog.Builder builder = new AlertDialog.Builder(game.getContext());
				builder.setMessage("Are you sure you would like to delete this mod? This operation cannot be undone.").setPositiveButton("Yes", dialogClickListener).setNegativeButton("No", dialogClickListener).show();
			}
		});
		add(btnDelete);

		Component btnUp = new Component("btnUp");
		btnUp.setBackground("gui/scroll_up.png");
		btnUp.setBackgroundSelected("gui/scroll_up_selected.png");
		btnUp.setX(modsRenderer.getX() + modsRenderer.getWidth() + 16f);
		btnUp.setY(modsRenderer.getY() + 64f);
		btnUp.setWidth(64f);
		btnUp.setHeight(64f);
		btnUp.addListener(new ComponentListenerImpl(){
			public void touchUp(Component component, Game game){
				if(selectedIndex > 0){
					selectedIndex--;
					onCurrentModChange(game);
				}
				if(selectedIndex < scrollIndex)
					scrollIndex = selectedIndex;
			}
		});
		add(btnUp);

		Component btnDown = new Component("btnDown");
		btnDown.setBackground("gui/scroll_down.png");
		btnDown.setBackgroundSelected("gui/scroll_down_selected.png");
		btnDown.setX(modsRenderer.getX() + modsRenderer.getWidth() + 16f);
		btnDown.setY(modsRenderer.getY() + 192f);
		btnDown.setWidth(64f);
		btnDown.setHeight(64f);
		btnDown.addListener(new ComponentListenerImpl(){
			public void touchUp(Component component, Game game){
				if(selectedIndex < mods.size() - 1){
					selectedIndex++;
					onCurrentModChange(game);
				}
				if(selectedIndex > scrollIndex + MODS_PER_PAGE)
					scrollIndex = selectedIndex;
			}
		});
		add(btnDown);
	}

	private void onCurrentModChange(Game game){
		if(selectedIndex >= mods.size()){
			btnLoad.setFlag(Component.INACTIVE);
			btnDelete.setFlag(Component.INACTIVE);
		}else{
			btnLoad.setFlag(0);
			btnDelete.setFlag(0);
			TinyMod currentMod = mods.get(selectedIndex);
			if(game.getMods().isModEnabled(game, currentMod))
				btnLoad.setText("Disable");
			else
				btnLoad.setText("Enable");
		}
	}
}
