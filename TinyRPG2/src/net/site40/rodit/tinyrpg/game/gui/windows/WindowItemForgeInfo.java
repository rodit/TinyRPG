package net.site40.rodit.tinyrpg.game.gui.windows;

import net.site40.rodit.tinyrpg.game.Dialog.DialogCallback;
import net.site40.rodit.tinyrpg.game.Game;
import net.site40.rodit.tinyrpg.game.forge.ForgeProvider;
import net.site40.rodit.tinyrpg.game.forge.ForgeRegistry.ForgeRecipy;
import net.site40.rodit.tinyrpg.game.forge.ForgeRegistry.ForgeStatus;
import net.site40.rodit.tinyrpg.game.gui.windows.WindowSlotted.ProviderInfo;
import net.site40.rodit.tinyrpg.game.item.ItemStack;
import android.util.Log;

public class WindowItemForgeInfo extends WindowItemInfo{

	public WindowItemForgeInfo(Game game, ItemStack stack, ProviderInfo provider){
		super(game, stack, provider);
		initialize(game);
	}

	@Override
	public void initialize(Game game){
		if(info == null || info.provider == null)
			return;

		super.initialize(game);
		
		btnDispose.setFlag(WindowComponent.FLAG_INVISIBLE, true);
		txtItemRarity.setFlag(WindowComponent.FLAG_INVISIBLE, true);
		txtItemValue.setFlag(WindowComponent.FLAG_INVISIBLE, true);
		
		ForgeProvider provider = (ForgeProvider)info.provider;

		final ForgeRecipy current = provider.getSelectedRecipy();
		final ForgeStatus status = game.getForge().getForgeStatus(current, game.getPlayer());
		
		btnEquipUse.getListeners().clear();
		btnEquipUse.setText("Forge");
		btnEquipUse.addListener(new WindowListener(){
			public void touchUp(final Game game, WindowComponent component){
				game.getWindows().get(WindowUpgradeItem.class).hide();
				WindowItemForgeInfo.this.hide();
				DialogCallback callback = new DialogCallback(){
					public void onSelected(int option){
						game.getWindows().get(WindowUpgradeItem.class).show();
						WindowItemForgeInfo.this.show();
					}
				};
				switch(status){
				case POSSIBLE:
					for(ItemStack inStack : current.getInput())
						game.getPlayer().getInventory().remove(inStack.getItem(), inStack.getAmount());
					for(ItemStack outStack : current.getOutput())
						game.getPlayer().getInventory().add(outStack);
					game.getHelper().dialog("Forge was successful!", new String[0], callback);
					break;
				case FORGE_STAT_LOW:
					game.getHelper().dialog("You do not meet the skill requirements to forge this item.", new String[0], callback);
					break;
				case MONEY_LOW:
					game.getHelper().dialog("You do not have enough gold to forge this item.", new String[0], callback);
					break;
				case MATERIALS_LOW:
					game.getHelper().dialog("You do not have the required materials to forge this item.", new String[0], callback);
					break;
				default:
					game.getHelper().dialog("An unknown error occured while processing your forge request.~Please try again later.", new String[0], callback);
					break;
				}
			}
		});
		
		txtItemDescription.setText("Input:");
		for(ItemStack inStack : current.getInput()){
			if(inStack == null)
				Log.e("Forge", "Input stack for forge recipe == null!");
			else
				txtItemDescription.setText(txtItemDescription.getText() + "\n" + inStack.getItem().getShowName() + " x " + inStack.getAmount());
		}

		txtItemDescription.setText(txtItemDescription.getText() + "\nOutput:");
		for(ItemStack outStack : current.getOutput()){
			if(outStack == null)
				Log.e("Forge", "Output stack for forge recipe == null!");
			else
				txtItemDescription.setText(txtItemDescription.getText() + "\n" + outStack.getItem().getShowName() + " x " + outStack.getAmount());
		}

		switch(status){
		case POSSIBLE:
		default:
			break;
		case FORGE_STAT_LOW:
			txtItemDescription.setText(txtItemDescription.getText() + "\n\nForge skill too low.");
			break;
		case MONEY_LOW:
			txtItemDescription.setText(txtItemDescription.getText() + "\n\nNot enough gold.");
			break;
		case MATERIALS_LOW:
			txtItemDescription.setText(txtItemDescription.getText() + "\n\nNot enough materials.");
			break;
		}
	}
	
	public boolean isVanillaWindow(){
		return false;
	}
	
	@Override
	public void initAfterInit(Game game){
		super.initAfterInit(game);
	}
}
