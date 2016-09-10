package net.site40.rodit.tinyrpg.game.gui;

import java.io.IOException;
import java.util.ArrayList;

import net.site40.rodit.tinyrpg.R;
import net.site40.rodit.tinyrpg.game.Game;
import net.site40.rodit.tinyrpg.game.Values;
import net.site40.rodit.tinyrpg.game.gui.ComponentListener.ComponentListenerImpl;
import net.site40.rodit.tinyrpg.game.render.TransitionalRenderer;
import net.site40.rodit.tinyrpg.game.render.effects.EffectCompletionHolder;
import net.site40.rodit.tinyrpg.game.render.effects.FadeInEffect;
import net.site40.rodit.tinyrpg.game.render.effects.FadeOutEffect;
import android.graphics.Color;
import android.media.MediaPlayer;

public class GuiMenu extends Gui{

	public GuiMenu(){
		super("");
	}

	@Override
	public void init(){
		ArrayList<String> transitionRes = new ArrayList<String>();
		transitionRes.add("menu/0.png");
		transitionRes.add("menu/1.png");
		transitionRes.add("menu/2.png");
		transitionRes.add("menu/3.png");
		transitionRes.add("menu/4.png");
		transitionRes.add("menu/5.png");
		final TransitionalRenderer r = new TransitionalRenderer(transitionRes, 10000L);

		Component txtTinyRpg = new Component("txtTinyRpg", "TinyRPG");
		txtTinyRpg.getPaint().setTextSize(Values.FONT_SIZE_HUGE);
		txtTinyRpg.getPaint().setColor(Color.WHITE);
		txtTinyRpg.setX(640);
		txtTinyRpg.setY(92);
		add(txtTinyRpg);

		Component btnPlay = new Component("btnPlay", "New Game");
		btnPlay.getPaint().setTextSize(Values.FONT_SIZE_MEDIUM);
		btnPlay.setWidth(320);
		btnPlay.setHeight(92);
		btnPlay.setX(48f);
		btnPlay.setY(720f - btnPlay.getHeight() - 48f);
		btnPlay.addListener(new ComponentListenerImpl(){
			public void touchUp(Component component, final Game game){
				FadeOutEffect effect = new FadeOutEffect(500L);
				EffectCompletionHolder holder = new EffectCompletionHolder(effect, new Runnable(){
					public void run(){
						game.getPostProcessor().add(new FadeInEffect(500L));
						game.skipFrames(1);
						game.getScripts().execute(game, "script/init/play.js", new String[0], new Object[0]);
						game.removeObject(r);
					}
				});
				game.getPostProcessor().add(effect);
				game.getPostProcessor().add(holder);
			}
		});
		add(btnPlay);

		Component btnContinue = new Component("btnContinue", "Continue");
		btnContinue.getPaint().setTextSize(Values.FONT_SIZE_MEDIUM);
		btnContinue.setWidth(256);
		btnContinue.setHeight(92);
		btnContinue.setX(btnPlay.getX() + btnPlay.getWidth() + 48f);
		btnContinue.setY(btnPlay.getY());
		btnContinue.addListener(new ComponentListenerImpl(){
			public void touchUp(Component component, Game game){
				if(game.getSaves().canContinue(game)){
					try{
						game.getSaves().load(game);
						game.removeObject(r);
						game.getGuis().hide(GuiMenu.class);
						game.getGuis().show(GuiIngame.class);
					}catch(IOException e){
						game.showMessage("There was an error while loading your save.\n" + e.getClass().getName() + ": " + e.getMessage() + "\n" + e.getLocalizedMessage(), GuiMenu.this);
						e.printStackTrace();
					}
				}
			}

			public void update(Component component, Game game){
				if(game.getSaves().canContinue(game))
					component.getPaint().setColor(Color.WHITE);
				else
					component.getPaint().setColor(Color.GRAY);
			}
		});
		add(btnContinue);

		Component btnMods = new Component("btnSaves", "Mods");
		btnMods.getPaint().setTextSize(Values.FONT_SIZE_MEDIUM);
		btnMods.setX(btnContinue.getX() + btnContinue.getWidth() + 48f);
		btnMods.setY(btnContinue.getY());
		btnMods.setWidth(256);
		btnMods.setHeight(92);
		btnMods.addListener(new ComponentListenerImpl(){
			public void touchUp(Component component, Game game){
				game.getGuis().hide(GuiMenu.class);
				game.getGuis().show(GuiMods.class);
			}
		});
		add(btnMods);

		Component btnExit = new Component("btnExit", "Exit");
		btnExit.getPaint().setTextSize(Values.FONT_SIZE_MEDIUM);
		btnExit.setX(btnMods.getX() + btnMods.getWidth() + 32f);
		btnExit.setY(btnMods.getY());
		btnExit.setWidth(256);
		btnExit.setHeight(92);
		btnExit.addListener(new ComponentListenerImpl(){
			public void touchUp(Component component, Game game){
				System.exit(0);
			}
		});
		add(btnExit);

		RendererComponent bg = new RendererComponent(r){
			boolean f0 = true;
			@Override
			public void update(Game game){
				super.update(game);
				if(f0){
					MediaPlayer bgMusic = game.getAudio().get(R.raw.menu_music);
					bgMusic.setLooping(true);
					bgMusic.start();
					f0 = false;
				}
			}
		};
		add(bg);
	}
}
