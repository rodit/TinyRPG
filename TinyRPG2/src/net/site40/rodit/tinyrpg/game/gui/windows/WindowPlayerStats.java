package net.site40.rodit.tinyrpg.game.gui.windows;

import java.util.ArrayList;

import net.site40.rodit.tinyrpg.game.Game;
import net.site40.rodit.tinyrpg.game.SuperCalc;
import net.site40.rodit.tinyrpg.game.Values;
import net.site40.rodit.tinyrpg.game.entity.EntityPlayer;
import net.site40.rodit.tinyrpg.game.entity.EntityStats;
import net.site40.rodit.util.Util;
import android.graphics.Paint.Align;

public class WindowPlayerStats extends Window{
	
	private EntityPlayer player;
	private EntityStats modStats;
	
	private WindowComponent txtTitle;
	private WindowComponent txtStats;
	private WindowComponent txtStatValues;
	private WindowComponent btnUpSpeed;
	private WindowComponent btnUpStrength;
	private WindowComponent btnUpDefence;
	private WindowComponent btnUpLuck;
	private WindowComponent btnUpMagika;
	private WindowComponent btnUpForge;
	private WindowComponent btnClearCurrent;
	private WindowComponent btnConfirm;
	private WindowComponent txtHumanity;
	private WindowComponent txtFaction;
	
	private ArrayList<WindowComponent> upBtns;
	
	private int initUpgradePoints;
	private int cUpgradePoints;

	public WindowPlayerStats(Game game, EntityPlayer player){
		super(game);
		this.player = player;
		this.modStats = new EntityStats(player.getStats());
		this.upBtns = new ArrayList<WindowComponent>();
		initialize(game);
	}
	
	@Override
	public void initialize(Game game){
		if(player == null)
			return;
		
		this.setBounds(64, 32, 1152, 640);
		
		initUpgradePoints = player.getStats().getUpgradePoints();
		cUpgradePoints = initUpgradePoints;
		
		this.txtTitle = new WindowComponent("txtTitle");
		txtTitle.setText(game.getPlayer().getUsername());
		txtTitle.setX(576);
		txtTitle.setY(72);
		txtTitle.getPaint().setTextSize(Values.FONT_SIZE_MEDIUM);
		txtTitle.getPaint().setTextAlign(Align.CENTER);
		add(txtTitle);
		
		this.txtStats = new WindowComponent("txtStats");
		txtStats.setX(48);
		txtStats.setY(128);
		txtStats.getPaint().setTextSize(Values.FONT_SIZE_MEDIUM);
		txtStats.getPaint().setTextAlign(Align.LEFT);
		txtStats.setFlag(WindowComponent.FLAG_MULTILINE_TEXT, true);
		add(txtStats);
		
		this.txtStatValues = new WindowComponent("txtStatValues");
		txtStatValues.setX(txtStats.getBounds().getX() + 178f);
		txtStatValues.setY(txtStats.getBounds().getY());
		txtStatValues.getPaint().setTextSize(Values.FONT_SIZE_MEDIUM);
		txtStatValues.getPaint().setTextAlign(Align.LEFT);
		txtStatValues.setFlag(WindowComponent.FLAG_MULTILINE_TEXT, true);
		add(txtStatValues);
		
		btnUpSpeed = generateUpgradeButton("speed", 300, 280);
		btnUpStrength = generateUpgradeButton("strength", 300, 330);
		btnUpDefence = generateUpgradeButton("defence", 300, 380);
		btnUpLuck = generateUpgradeButton("luck", 300, 430);
		btnUpMagika = generateUpgradeButton("magika", 300, 480);
		btnUpForge = generateUpgradeButton("forge", 300, 530);
		
		this.btnClearCurrent = new WindowComponent("btnClearCurrent");
		btnClearCurrent.setBounds(txtStats.getBounds().getX(), 580, 165, 32);
		btnClearCurrent.setText("Clear Current");
		btnClearCurrent.setBackground(WindowComponent.STATE_IDLE, "gui/button.png");
		btnClearCurrent.setBackground(WindowComponent.STATE_DOWN, "gui/button_selected.png");
		btnClearCurrent.getPaint().setTextSize(Values.FONT_SIZE_TINY);
		btnClearCurrent.addListener(new WindowListener(){
			@Override
			public void touchUp(Game game, WindowComponent component){
				modStats = new EntityStats(player.getStats());
				cUpgradePoints = initUpgradePoints;
				onUpgradePointsChanged();
				onStatsChanged();
			}
		});
		add(btnClearCurrent);
		
		this.btnConfirm = new WindowComponent("btnConfirm");
		btnConfirm.setBounds(btnClearCurrent.getBounds().getX() + btnClearCurrent.getBounds().getWidth() + 16f, btnClearCurrent.getBounds().getY(), 105, 32);
		btnConfirm.setText("Confirm");
		btnConfirm.setBackground(WindowComponent.STATE_IDLE, "gui/button.png");
		btnConfirm.setBackground(WindowComponent.STATE_DOWN, "gui/button_selected.png");
		btnConfirm.getPaint().setTextSize(Values.FONT_SIZE_TINY);
		btnConfirm.addListener(new WindowListener(){
			@Override
			public void touchUp(Game game, WindowComponent component){
				player.setStats(modStats);
				initUpgradePoints = cUpgradePoints;
				player.getStats().setUpgradePoints(cUpgradePoints);
				onUpgradePointsChanged();
				onStatsChanged();
			}
		});
		add(btnConfirm);
		
		this.txtHumanity = new WindowComponent("txtHumanity");
		txtHumanity.setX(640f);
		txtHumanity.setY(128f);
		txtHumanity.getPaint().setTextSize(Values.FONT_SIZE_MEDIUM);
		txtHumanity.getPaint().setTextAlign(Align.LEFT);
		add(txtHumanity);
		
		this.txtFaction = new WindowComponent("txtFaction");
		txtFaction.setX(txtHumanity.getBounds().getX());
		txtFaction.setY(txtHumanity.getBounds().getY() + txtHumanity.getPaint().getTextSize() + 8f);
		txtFaction.getPaint().setTextSize(Values.FONT_SIZE_MEDIUM);
		txtFaction.getPaint().setTextAlign(Align.LEFT);
		txtFaction.setFlag(WindowComponent.FLAG_MULTILINE_TEXT, true);
		add(txtFaction);
				
		onUpgradePointsChanged();
		onStatsChanged();
	}
	
	private void showUpButtons(){
		for(WindowComponent upBtn : upBtns)
			upBtn.setFlag(WindowComponent.FLAG_INVISIBLE, false);
	}
	
	private void hideUpButtons(){
		for(WindowComponent upBtn : upBtns)
			upBtn.setFlag(WindowComponent.FLAG_INVISIBLE, true);
	}
	
	public void onUpgradePointsChanged(){
		if(cUpgradePoints <= 0 && modStats.equals(player.getStats())){
			btnClearCurrent.setFlag(WindowComponent.FLAG_INVISIBLE, true);
			btnConfirm.setFlag(WindowComponent.FLAG_INVISIBLE, true);
			hideUpButtons();
		}else{
			btnClearCurrent.setFlag(WindowComponent.FLAG_INVISIBLE, false);
			btnConfirm.setFlag(WindowComponent.FLAG_INVISIBLE, false);
			showUpButtons();
		}
	}
	
	public void onStatsChanged(){
		txtStats.setText("Stats " + (cUpgradePoints > 0 ? "(+" + cUpgradePoints + ")" : "") + "\n"
				+ "LVL\n"
				+ "HP\n"
				+ "MP\n"
				+ "SPD\n"
				+ "STR\n"
				+ "DEF\n"
				+ "LCK\n"
				+ "MGK\n"
				+ "FRG\n");
		txtStatValues.setText("\n"
				+ modStats.getLevel() + "\n"
				+ player.getHealth() + "/" + SuperCalc.getMaxHealth(player) + "\n"
				+ player.getMagika() + "/" + SuperCalc.getMaxMagika(player) + "\n"
				+ Util.getStatInt(modStats.getSpeed()) + "\n"
				+ Util.getStatInt(modStats.getStrength()) + "\n"
				+ Util.getStatInt(modStats.getDefence()) + "\n"
				+ Util.getStatInt(modStats.getLuck()) + "\n"
				+ Util.getStatInt(modStats.getMagika()) + "\n"
				+ Util.getStatInt(modStats.getForge()));
		txtHumanity.setText("Humanity: " + player.getHumanity());
		txtFaction.setText("Faction: " + player.getFaction().getFaction().getDisplayName() + "\nLevel: " + player.getFaction().getLevel());
	}
	
	public WindowComponent generateUpgradeButton(final String sId, int x, int y){
		WindowComponent btn = new WindowComponent("btnUp" + sId);
		btn.setBounds(x, y, 38, 38);
		btn.setBackground(WindowComponent.STATE_IDLE, "gui/scroll_right.png");
		btn.setBackground(WindowComponent.STATE_DOWN, "gui/scroll_right_selected.png");
		btn.addListener(new WindowListener(){
			@Override
			public void touchUp(Game game, WindowComponent component){
				if(cUpgradePoints <= 0)
					return;
				if(sId.equals("speed"))
					modStats.setSpeed(modStats.getSpeed() + 0.1f);
				else if(sId.equals("strength"))
					modStats.setStrength(modStats.getStrength() + 0.1f);
				else if(sId.equals("defence"))
					modStats.setDefence(modStats.getDefence() + 0.1f);
				else if(sId.equals("luck"))
					modStats.setLuck(modStats.getLuck() + 0.1f);
				else if(sId.equals("magika"))
					modStats.setMagika(modStats.getMagika() + 0.1f);
				else if(sId.equals("forge"))
					modStats.setForge(modStats.getForge() + 0.1f);
				else
					throw new IllegalArgumentException("Invalid stat id.");
				cUpgradePoints--;
				onUpgradePointsChanged();
				onStatsChanged();
			}
		});
		upBtns.add(btn);
		add(btn);
		return btn;
	}
}
