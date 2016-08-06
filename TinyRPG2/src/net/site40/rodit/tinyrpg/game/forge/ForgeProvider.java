package net.site40.rodit.tinyrpg.game.forge;

import java.util.ArrayList;

import net.site40.rodit.tinyrpg.game.entity.Entity;
import net.site40.rodit.tinyrpg.game.entity.EntityPlayer;
import net.site40.rodit.tinyrpg.game.forge.ForgeRegistry.ForgeRecipy;
import net.site40.rodit.tinyrpg.game.forge.ForgeRegistry.ForgeRecipy.ForgeType;
import net.site40.rodit.tinyrpg.game.item.Inventory;
import net.site40.rodit.tinyrpg.game.item.Inventory.InventoryProvider;
import net.site40.rodit.tinyrpg.game.item.ItemStack;

public class ForgeProvider extends InventoryProvider{

	private boolean showAll = false;
	private ForgeRegistry forge;
	private ForgeType forgeType;
	private ForgeRecipy selectedRecipy;
	
	public ForgeProvider(Inventory inventory, Entity owner, ForgeRegistry forge, ForgeType forgeType){
		super(inventory, owner);
		this.forge = forge;
		this.forgeType = forgeType;
	}
	
	public boolean shouldShowAll(){
		return showAll;
	}
	
	public void setShowAll(boolean showAll){
		this.showAll = showAll;
	}
	
	public ForgeType getForgeType(){
		return forgeType;
	}
	
	public ForgeRecipy getSelectedRecipy(){
		return selectedRecipy;
	}
	
	public void setSelectedRecipy(ForgeRecipy selectedRecipy){
		this.selectedRecipy = selectedRecipy;
	}
	
	@Override
	public ArrayList<ItemStack> provide(int type){
		ArrayList<ForgeRecipy> recipes = new ArrayList<ForgeRecipy>();
		if(showAll)
			recipes = forge.getAll();
		else
			recipes = forge.getAvailable((EntityPlayer)getOwner(), forgeType);
		ArrayList<ItemStack> stacks = new ArrayList<ItemStack>();
		for(ForgeRecipy recipy : recipes)
			stacks.add(recipy.getOutput().get(0));
		return stacks;
	}
	
	@Override
	public ItemStack provide(int type, int index){
		return super.provide(-1, index);
	}
	
	public ForgeRecipy getRecipyForStack_DOESNT_WORK_FOR_ITEMS_WITH_MULTIPLE_SOURCE_RECIPES(ItemStack stack){
		ArrayList<ForgeRecipy> recipes = new ArrayList<ForgeRecipy>();
		if(showAll)
			recipes = forge.getAll();
		else
			recipes = forge.getAvailable((EntityPlayer)getOwner(), forgeType);
		ArrayList<ItemStack> stacks = provide(-1);
		return recipes.get(stacks.indexOf(stack));
	}
}
