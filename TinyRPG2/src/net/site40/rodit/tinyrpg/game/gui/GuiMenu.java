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
import android.graphics.Paint.Align;
import android.media.MediaPlayer;

public class GuiMenu extends Gui{

	public GuiMenu(){
		super("");
	}

	@Override
	public void onShown(){
		for(Component component : getComponents()){
			component.setFlag(0);
		}
	}

	@Override
	public void init(){
		final ArrayList<String> transitionRes = new ArrayList<String>();
		transitionRes.add("menu/0.png");
		transitionRes.add("menu/1.png");
		transitionRes.add("menu/2.png");
		transitionRes.add("menu/3.png");
		transitionRes.add("menu/4.png");
		transitionRes.add("menu/5.png");
		final TransitionalRenderer r = new TransitionalRenderer(transitionRes, 10000L);
		r.ignoreScroll = true;

		Component txtTinyRpg = new Component("txtTinyRpg", "TinyRPG");
		txtTinyRpg.getPaint().setTextSize(Values.FONT_SIZE_HUGE);
		txtTinyRpg.getPaint().setColor(Color.WHITE);
		txtTinyRpg.getPaint().setTextAlign(Align.CENTER);
		txtTinyRpg.setX(640);
		txtTinyRpg.setY(92);
		txtTinyRpg.setWidth(512f);
		add(txtTinyRpg);
		
		Component btnPlay = new Component("btnPlay", "New Game");
		btnPlay.getPaint().setTextSize(Values.FONT_SIZE_MEDIUM);
		btnPlay.setWidth(320);
		btnPlay.setHeight(92);
		btnPlay.setX(48f);
		btnPlay.setY(720f - btnPlay.getBounds().getHeight() - 48f);
		btnPlay.addListener(new ComponentListenerImpl(){
			public void touchUp(Component component, final Game game){
				for(Component component0 : getComponents())
					component0.setFlag(Component.INACTIVE);
				FadeOutEffect effect = new FadeOutEffect(500L);
				EffectCompletionHolder holder = new EffectCompletionHolder(effect, new Runnable(){
					public void run(){
						game.getPostProcessor().add(new FadeInEffect(500L));
						game.skipFrames(1);
						game.getScript().runScript(game, "script/init/play.js");
						game.removeObject(r);
						game.getScheduler().schedule(new Runnable(){
							@Override
							public void run(){
								for(String res : transitionRes)
									game.getResources().putObject(res, null);
							}
						}, game.getTime(), 1000L);
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
		btnContinue.setX(btnPlay.getBounds().getX() + btnPlay.getBounds().getWidth() + 48f);
		btnContinue.setY(btnPlay.getBounds().getY());
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
		btnMods.setX(btnContinue.getBounds().getX() + btnContinue.getBounds().getWidth() + 48f);
		btnMods.setY(btnContinue.getBounds().getY());
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
		btnExit.setX(btnMods.getBounds().getX() + btnMods.getBounds().getWidth() + 32f);
		btnExit.setY(btnMods.getBounds().getY());
		btnExit.setWidth(256);
		btnExit.setHeight(92);
		btnExit.addListener(new ComponentListenerImpl(){
			public void touchUp(Component component, final Game game){
				for(Component component0 : getComponents())
					component0.setFlag(Component.INACTIVE);
				FadeOutEffect out = new FadeOutEffect(250L);
				EffectCompletionHolder holder = new EffectCompletionHolder(out, new Runnable(){
					@Override
					public void run(){
						game.skipFrames(1);
						game.cleanExit();
					}
				});
				game.getPostProcessor().add(out);
				game.getPostProcessor().add(holder);
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
					//bgMusic.start();
					f0 = false;
				}
			}
		};
		add(bg);
	}
}
