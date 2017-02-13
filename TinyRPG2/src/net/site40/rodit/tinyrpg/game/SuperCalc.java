package net.site40.rodit.tinyrpg.game;

import net.site40.rodit.tinyrpg.game.entity.EntityLiving;
import net.site40.rodit.tinyrpg.game.entity.EntityPlayer;
import net.site40.rodit.tinyrpg.game.entity.EntityStats;
import net.site40.rodit.tinyrpg.game.item.Item;
import net.site40.rodit.tinyrpg.game.item.ItemStack;
import net.site40.rodit.tinyrpg.game.item.Weapon;

public class SuperCalc {
	
	public static final float BASE_MAGIKA = 10f;
	public static final float BASE_HEALTH = 25f;
	
	public static float getDamage(Game game, EntityLiving user, EntityLiving receiver, Weapon weapon){
		float rand = game.getRandom().nextFloat() - 0.5f;
		rand /= 0.5f;
		float direct = getDirectDamage(game, user, receiver, weapon) * (1f + rand);
		float critChance = getCriticalHitChance(user);
		float multi = game.getRandom().should(critChance) ? getCriticalMultiplier(user) : 1f;
		return multi * direct;
	}
	
	public static float getDirectDamage(Game game, EntityLiving user, EntityLiving receiver, Weapon weapon){
		float dmgMulti = weapon.isMagic() ? getMagikaDamageMulti(user, receiver) : getDamageMulti(user, receiver);
		float def = getDefenceMulti(user, receiver) * receiver.getTotalDefence();
		float damage = dmgMulti * weapon.getDamage() - def;
		damage = damage < 0f ? 0f : damage;
		return (int)damage;
	}
	
	public static float getDamageMulti(EntityLiving user, EntityLiving receiver){
		return receiver.getStats().getLevel() - user.getStats().getLevel() < 3 ? 0.88f : 1f;
	}
	
	public static int getLevelDifference(EntityLiving user, EntityLiving receiver){
		return receiver.getStats().getLevel() - user.getStats().getLevel();
	}
	
	public static float getMagikaDamageMulti(EntityLiving user, EntityLiving receiver){
		return getMagikaDifference(user, receiver) < 3 ? 1 : 0.9f;
	}
	
	public static float getMagikaDifference(EntityLiving user, EntityLiving receiver){
		return receiver.getStats().getMagika() - user.getStats().getMagika();
	}
	
	public static float getDefenceMulti(EntityLiving user, EntityLiving receiver){
		return getDefenceAttackDifference(user, receiver) < 1.5f ? 1f : 0.75f;
	}
	
	public static float getDefenceAttackDifference(EntityLiving user, EntityLiving receiver){
		return receiver.getStats().getDefence() - user.getStats().getStrength();
	}
	
	public static int getMaxMagika(EntityLiving ent){
		return (int)(BASE_MAGIKA + 20f * ((float)Math.pow(1.13f, (float)ent.getStats().getLevel() / 5f)));
	}
	
	public static int getMaxHealth(EntityLiving ent){
		return (int)(BASE_HEALTH + 30f * (float)Math.pow(1.13f, (float)ent.getStats().getLevel() / 3.5f)) + getHealthBonus(ent);
	}
	
	private static int getHealthBonus(EntityLiving ent){
		return (int)(((10f * ent.getStats().getStrength()) / 2f) * 8f);
	}
	
	public static int getXpForLevel(int level){
		return (int)((6f / 5f) * Math.pow(level, 3) - 15 * Math.pow(level, 2) + 100 * level - 140);
	}
	
	public static int getXpForLevelDifficult(int level){
		return (int)(Math.pow(level, 3) * (((float)level / 2f) + 32) / 50);
	}
	
	public static int getXpForLevelExp(int level){
		return (int)(Math.pow(2, (float)level / 4f) * 300f - Math.pow(2, 1f / 4f) * 300f);
	}
	
	public static int getXpForLevelLog(int level){
		return log(level, 1.2f) * 300;
	}
	
	public static int log(float x, float base){
		return (int)(Math.log(x) / Math.log(base));
	}
	
	public static long getItemValue(Item item, EntityLiving seller){
		float luckVal = seller == null ? 0f : seller.getStats().getLuck();
		float itemVal = (float)item.getValue();
		if(seller != null)
			itemVal /= 8 * (1 + luckVal);
		itemVal = Math.min(itemVal, item.getValue());
		return (long)itemVal;
	}
	
	public static long getStackValue(ItemStack stack, EntityLiving seller){
		return getItemValue(stack.getItem(), seller) * stack.getAmount();
	}
	
	public static long getItemValueFromShop(Item item, EntityPlayer player){
		return item.getValue();
	}
	
	public static long getStackValueFromShop(ItemStack stack, EntityPlayer player){
		return getItemValueFromShop(stack.getItem(), player) * stack.getAmount();
	}
	
	public static float getCriticalHitChance(EntityLiving entity){
		if(entity.parryCritFlag){
			entity.parryCritFlag = false;
			return 1f;
		}
		return (float)(Math.pow(0.98d, -(double)entity.getStats().getLevel() + Math.pow(0.975d, -(double)entity.getStats().getLuck()))) / 100f;
	}
	
	public static float getCriticalMultiplier(EntityLiving entity){
		return (float)(Math.pow(1.1d, (double)entity.getStats().getLevel() / 20d) + Math.pow(1.1d, (double)entity.getStats().getStrength() / 15d) - 1d);
	}
	
	public static float getAssassinParryChance(EntityStats stats){
		float luckAdd = stats.getLuck() > 9 ? ((stats.getLuck() * 10f) - 9) * 0.025f : 0f;
		return (2f + (float)stats.getLevel() * 0.1f + luckAdd) / 100f;
	}
	
	public static float getMageDamageReductionMulti(EntityStats stats){
		return (10f + (float)stats.getLevel() * 0.5f) / 100f;
	}
	
	public static float getThiefGoldBonusMulti(EntityStats stats){
		float luckAdd = stats.getLuck() > 9 ? ((stats.getLuck() * 10f) - 9) * 0.025f : 0f;
		return (10f + (float)stats.getLevel() * 0.5f + luckAdd) / 100f;
	}
}
