package net.site40.rodit.tinyrpg.game.gui;

import net.site40.rodit.tinyrpg.game.Game;
import net.site40.rodit.tinyrpg.game.entity.EntityPlayer;


public class GuiInventoryPlayer extends Gui{
	
	public GuiInventoryPlayer(){
		super("");
	}

	private int slot = 0;

	@Override
	public void init(){
		Component secondBg = new Component("secondBg");
		secondBg.setBackgroundDefault("gui/generic_white.png");
		secondBg.setBounds(392, 32, 856, 438);
		add(secondBg);

		final Component helmSlot = new Component("helmSlot");
		helmSlot.setBackgroundDefault("gui/helm_bg.png");
		helmSlot.setBounds(128, 48, 64, 64);
		add(helmSlot);

		final Component armSlot = new Component("armSlot");
		armSlot.setBackgroundDefault("gui/arm_bg.png");
		armSlot.setBounds(48, 124, 64, 64);
		add(armSlot);

		final Component chestSlot = new Component("chestSlot");
		chestSlot.setBackgroundDefault("gui/chest_bg.png");
		chestSlot.setBounds(128, 124, 64, 64);
		add(chestSlot);

		final Component weaponSlot = new Component("weaponSlot");
		weaponSlot.setBackgroundDefault("gui/weapon_bg.png");
		weaponSlot.setBounds(208, 204, 64, 64);
		add(weaponSlot);

		final Component shieldSlot = new Component("shieldSlot");
		shieldSlot.setBackgroundDefault("gui/shield_bg.png");
		shieldSlot.setBounds(48, 204, 64, 64);
		add(shieldSlot);

		final Component necklaceSlot = new Component("necklaceSlot");
		necklaceSlot.setBackgroundDefault("gui/necklace_bg.png");
		necklaceSlot.setBounds(304, 124, 64, 64);
		add(necklaceSlot);

		final Component ringSlot = new Component("ringSlot");
		ringSlot.setBackgroundDefault("gui/ring_bg.png");
		ringSlot.setBounds(304, 124, 64, 64);
		add(ringSlot);

		Component mainBg = new Component("mainBg");
		mainBg.setBackgroundDefault("gui/generic_grey.png");
		mainBg.setBounds(16, 16, 1248, 470);
		add(mainBg);
	}
	
	private void addSlot(String name, String background, int x, int y, int width, int height, int slotId){
		Component comp = new Component(name);
		comp.setBackgroundDefault(background);
		comp.setBounds(x, y, width, height);
		if(slotId != -1){
			comp.addListener(new ComponentListener(){
				public void update(Component component, Game game){
					
				}
				public void touchDown(Component component, Game game){}
				public void touchUp(Component component, Game game){}
			});
		}
	}
}
