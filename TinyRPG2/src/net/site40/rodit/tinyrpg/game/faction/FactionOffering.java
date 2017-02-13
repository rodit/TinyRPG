package net.site40.rodit.tinyrpg.game.faction;

import java.util.ArrayList;

import net.site40.rodit.tinyrpg.game.Game;
import net.site40.rodit.tinyrpg.game.entity.EntityLiving;
import net.site40.rodit.tinyrpg.game.faction.FactionOffering.FactionReward.RewardType;
import net.site40.rodit.tinyrpg.game.item.ItemStack;

public class FactionOffering {
	
	public static class FactionReward{
		
		public static enum RewardType{
			GOLD, ITEM, FACTION_LEVEL;
		}
		
		protected RewardType type;
		protected Object reward;
		
		public FactionReward(RewardType type, Object reward){
			this.type = type;
			this.reward = reward;
		}
		
		public RewardType getType(){
			return type;
		}
		
		public Object getReward(){
			return reward;
		}
		
		public void give(EntityLiving entity, Game game){
			switch(type){
			case GOLD:
				entity.addMoney((Long)reward);
				if(game != null)
					game.getHelper().dialog("You received " + reward + " gold.");
				break;
			case ITEM:
				ItemStack stack = (ItemStack)reward;
				entity.getInventory().add(stack);
				if(game != null)
					game.getHelper().dialog("You received " + stack.getItem().getShowName() + "x" + stack.getAmount() + ".");
				break;
			case FACTION_LEVEL:
				entity.getFaction().addLevel((Integer)reward);
				if(game != null)
					game.getHelper().dialog("Your faction level increased by " + reward + ".");
				break;
			}
		}
	}
	
	private ItemStack offering;
	private int minFactionLevel;
	private int maxFactionLevel;
	private ArrayList<FactionReward> rewards;
	
	public FactionOffering(ItemStack offering, int minFactionLevel, int maxFactionLevel, FactionReward... rewards){
		this.offering = offering;
		this.minFactionLevel = minFactionLevel;
		this.maxFactionLevel = maxFactionLevel;
		this.rewards = new ArrayList<FactionReward>();
		if(rewards != null)
			for(int i = 0; i < rewards.length; i++)
				this.rewards.add(rewards[i]);
	}
	
	public ItemStack getOffering(){
		return offering;
	}
	
	public int getMinFactionLevel(){
		return minFactionLevel;
	}
	
	public int getMaxFactionLevel(){
		return maxFactionLevel;
	}
	
	public ArrayList<FactionReward> getRewards(){
		return rewards;
	}
	
	public boolean canOffer(EntityLiving entity){
		return entity.getInventory().getCount(offering.getItem()) >= offering.getAmount() && entity.getFaction().getLevel() >= minFactionLevel && entity.getFaction().getLevel() <= maxFactionLevel;
	}
	
	public Builder getBuilder(){
		return new Builder();
	}
	
	public static Builder builder(){
		return new FactionOffering(null, 0, 0).getBuilder();
	}
	
	public class Builder{
		
		public Builder setOffering(ItemStack bOffering){
			offering = bOffering;
			return this;
		}
		
		public Builder setMinFactionLevel(int bMinFactionLevel){
			minFactionLevel = bMinFactionLevel;
			return this;
		}
		
		public Builder setMaxFactionLevel(int bMaxFactionLevel){
			maxFactionLevel = bMaxFactionLevel;
			return this;
		}
		
		public Builder addReward(FactionReward reward){
			rewards.add(reward);
			return this;
		}
		
		public Builder addReward(RewardType type, Object reward){
			rewards.add(new FactionReward(type, reward));
			return this;
		}
		
		public FactionOffering build(){
			return FactionOffering.this;
		}
	}
}
