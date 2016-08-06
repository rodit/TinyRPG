package net.site40.rodit.tinyrpg.game.forge;

import java.util.ArrayList;

import net.site40.rodit.tinyrpg.game.entity.EntityPlayer;
import net.site40.rodit.tinyrpg.game.forge.ForgeRegistry.ForgeRecipy.ForgeType;
import net.site40.rodit.tinyrpg.game.item.Item;
import net.site40.rodit.tinyrpg.game.item.ItemStack;

public class ForgeRegistry {
	
	public static enum ForgeStatus{
		POSSIBLE, FORGE_STAT_LOW, MONEY_LOW, MATERIALS_LOW;
	}
	
	private ArrayList<ForgeRecipy> recipes;
	
	private String cRegId = "";
	private ItemStack[] cRegInputs = null;
	private ItemStack[] cRegOutputs = null;
	private long cRegCost = 0L;
	private float cRegMinForge = 1f;
	private ForgeType cRegType = ForgeType.CRAFT;
	
	public ForgeRegistry(){
		this.recipes = new ArrayList<ForgeRecipy>();
	}
	
	public ArrayList<ForgeRecipy> getAll(){
		return recipes;
	}
	
	protected ItemStack genStack(String iName, int amount){
		return new ItemStack(Item.get(iName), amount);
	}
	
	protected void setId(String id){
		this.cRegId = id;
	}
	
	protected void setInputs(ItemStack... inputs){
		this.cRegInputs = inputs;
	}
	
	protected void setOutputs(ItemStack... outputs){
		this.cRegOutputs = outputs;
	}
	
	protected void setCost(long cost){
		this.cRegCost = cost;
	}
	
	protected void setMinForge(float minForge){
		this.cRegMinForge = minForge;
	}
	
	protected void setType(ForgeType type){
		this.cRegType = type;
	}
	
	protected void push(){
		register(cRegId, cRegInputs, cRegOutputs, cRegCost, cRegMinForge, cRegType);
	}
	
	public void register(String id, ItemStack[] inputs, ItemStack[] outputs, long cost, float minForge, ForgeType type){
		register(new ForgeRecipy(id, inputs, outputs, cost, minForge, type));
	}
	
	public void register(ForgeRecipy recipy){
		if(!recipes.contains(recipy))
			recipes.add(recipy);
	}
	
	public void unregister(ForgeRecipy recipy){
		recipes.remove(recipy);
	}
	
	public ForgeStatus getForgeStatus(ForgeRecipy recipy, EntityPlayer player){
		if(recipy.cost > player.getMoney())
			return ForgeStatus.MONEY_LOW;
		if(recipy.minForge > player.getStats().getForge())
			return ForgeStatus.FORGE_STAT_LOW;
		for(ItemStack stack : recipy.input)
			if(player.getInventory().getCount(stack.getItem()) < stack.getAmount())
				return ForgeStatus.MATERIALS_LOW;
		return ForgeStatus.POSSIBLE;
	}
	
	public ArrayList<ForgeRecipy> getAvailable(EntityPlayer player, ForgeType type){
		ArrayList<ForgeRecipy> available = new ArrayList<ForgeRecipy>();
		for(ForgeRecipy recipy : recipes)
			if(getForgeStatus(recipy, player) == ForgeStatus.POSSIBLE && (type == null || recipy.type == type))
				available.add(recipy);
		return available;
	}
	
	public ArrayList<ForgeRecipy> getAvailable(ForgeProvider provider){
		if(provider.shouldShowAll())
			return recipes;
		else
			return getAvailable((EntityPlayer)provider.getOwner(), provider.getForgeType());
	}
	
	public static class ForgeRecipy{
		
		public static enum ForgeType{
			CRAFT, UPGRADE, INFUSE
		}
		
		private String id;
		private ArrayList<ItemStack> input;
		private ArrayList<ItemStack> output;
		private long cost;
		private float minForge;
		private ForgeType type;
		
		public ForgeRecipy(String id, ItemStack[] inputs, ItemStack[] outputs, long cost, float minForge, ForgeType type){
			this.id = id;
			this.input = new ArrayList<ItemStack>();
			this.output = new ArrayList<ItemStack>();
			for(int i = 0; i < inputs.length; i++)
				input.add(inputs[i]);
			for(int i = 0; i < outputs.length; i++)
				output.add(outputs[i]);
			this.cost = cost;
			this.minForge = minForge;
			this.type = type;
		}
		
		public String getId(){
			return id;
		}
		
		public ArrayList<ItemStack> getInput(){
			return input;
		}
		
		public ArrayList<ItemStack> getOutput(){
			return output;
		}
		
		public long getCost(){
			return cost;
		}
		
		public float getMinForge(){
			return minForge;
		}
		
		public void setMinForge(float minForge){
			this.minForge = minForge;
		}
		
		public ForgeType getType(){
			return type;
		}
		
		public void setType(ForgeType type){
			this.type = type;
		}
	}
}
