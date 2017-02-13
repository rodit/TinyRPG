package net.site40.rodit.tinyrpg.game.gui.windows;

import java.util.ArrayList;

import net.site40.rodit.tinyrpg.game.Game;
import net.site40.rodit.tinyrpg.game.Values;
import net.site40.rodit.tinyrpg.game.forge.ForgeRegistry.ForgeRecipy;
import net.site40.rodit.tinyrpg.game.forge.ForgeRegistry.ForgeStatus;
import net.site40.rodit.tinyrpg.game.item.Inventory;
import net.site40.rodit.tinyrpg.game.item.Inventory.InventoryProvider;
import net.site40.rodit.tinyrpg.game.item.ItemStack;
import net.site40.rodit.util.GrammarUtil;
import android.graphics.Paint.Align;

public class WindowItemForgeInfo extends WindowSlotted{

	private static final String KEY_INPUT = "input";
	private static final String KEY_OUTPUT = "output";

	private static final int BASE_X = 156;
	private static final int BASE_Y = 156;
	private static final int OFFSET_X_OUT = 512;
	private static final int OFFSET_Y_OUT = 0;

	private ForgeRecipy recipy;

	private WindowComponent txtTitle;
	private WindowComponent txtInput;
	private WindowComponent txtCost;
	private WindowComponent txtOutput;
	private WindowComponent btnForge;
	private WindowComponent txtOutputCount;

	public WindowItemForgeInfo(Game game, ForgeRecipy recipy){
		super(game);
		this.recipy = recipy;
		initialize(game);
	}

	@Override
	public void initialize(Game game){
		if(recipy == null)
			return;
		super.initialize(game);

		this.setBounds(64, 32, 1152, 640);

		registerProvider(KEY_INPUT, createInventoryProvider(recipy.getInput()));
		registerProvider(KEY_OUTPUT, createInventoryProvider(recipy.getOutput()));

		for(int y = 0; y < 3; y++){
			for(int x = 0; x < 3; x++){
				addSlot(BASE_X + x * WindowSlot.SLOT_WIDTH, BASE_Y + y * WindowSlot.SLOT_HEIGHT, KEY_INPUT, "input_" + x + "_" + y);
				addSlot(BASE_X + OFFSET_X_OUT + x * WindowSlot.SLOT_WIDTH, BASE_Y + OFFSET_Y_OUT + y * WindowSlot.SLOT_HEIGHT, KEY_OUTPUT, "output_" + x + "_" + y);
			}
		}

		txtTitle = new WindowComponent("txtTitle");
		txtTitle.setText("Forge - " + GrammarUtil.capitalise(recipy.getType().toString()));
		txtTitle.setX(bounds.getWidth() / 2);
		txtTitle.setY(BASE_Y - 128f);
		txtTitle.getPaint().setTextSize(Values.FONT_SIZE_MEDIUM);
		txtTitle.getPaint().setTextAlign(Align.CENTER);
		add(txtTitle);

		txtInput = new WindowComponent("txtInput");
		txtInput.setText("Input:");
		txtInput.setX(BASE_X);
		txtInput.setY(BASE_Y - 48f);
		txtInput.getPaint().setTextSize(Values.FONT_SIZE_SMALL);
		txtInput.getPaint().setTextAlign(Align.LEFT);
		add(txtInput);

		txtCost = new WindowComponent("txtCost");
		txtCost.setText("Cost: " + recipy.getCost() + " Gold");
		txtCost.setX(txtInput.getBounds().getX());
		txtCost.setY(txtInput.getBounds().getY() + 28f);
		txtCost.getPaint().setTextSize(Values.FONT_SIZE_SMALL - 8f);
		txtCost.getPaint().setTextAlign(Align.LEFT);
		add(txtCost);

		txtOutput = new WindowComponent("txtOutput");
		txtOutput.setText("Output:");
		txtOutput.setX(BASE_X + OFFSET_X_OUT);
		txtOutput.setY(txtInput.getBounds().getY() + 32f);
		txtOutput.getPaint().setTextSize(Values.FONT_SIZE_SMALL);
		txtOutput.getPaint().setTextAlign(Align.LEFT);
		add(txtOutput);
		
		btnForge = new WindowComponent("btnForge");
		btnForge.setText(GrammarUtil.capitalise(recipy.getType().toString()));
		btnForge.setX(BASE_X + 3 * WindowSlot.SLOT_WIDTH + 32f);
		btnForge.setY(BASE_Y + 1.5f * WindowSlot.SLOT_HEIGHT);
		btnForge.addListener(new WindowListener(){
			@Override
			public void touchUp(Game game, WindowComponent component){
				ForgeStatus status = game.getForge().getForgeStatus(recipy, game.getPlayer());
				switch(status){
				
				}
			}
		});
		add(btnForge);
	}

	@Override
	public void onSlotSelected(Game game, WindowSlot slot){
		ProviderInfo info = getProviderInfo(slot.getProviderKey());
		ItemStack stack = info.provider.provide(1, slot.getIndex());
		if(stack != null){
			WindowItemInfo itemInfo = new WindowItemInfo(game, stack, info){
				@Override
				public void initialize(Game game){
					super.initialize(game);
					setX(640 - bounds.getWidth() / 2f);
					setY(WindowItemForgeInfo.this.bounds.getY());
					btnDispose.setFlag(WindowComponent.FLAG_INVISIBLE, true);
					btnEquipUse.setFlag(WindowComponent.FLAG_INVISIBLE, true);
				}

				@Override
				public boolean isVanillaWindow(){ return false; }
			};
			game.getWindows().register(itemInfo);
			itemInfo.zIndex = 1;
			itemInfo.show();
		}
	}

	private InventoryProvider createInventoryProvider(ArrayList<ItemStack> stacks){
		Inventory inventory = new Inventory();
		for(ItemStack stack : stacks)
			inventory.add(stack);
		return new InventoryProvider(inventory, null);
	}
}
