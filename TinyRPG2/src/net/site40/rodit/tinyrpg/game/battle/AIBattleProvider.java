package net.site40.rodit.tinyrpg.game.battle;

import static net.site40.rodit.tinyrpg.game.render.MM.EMPTY_STRING_ARRAY;
import static net.site40.rodit.tinyrpg.game.render.Strings.DIALOG_AI_HEAL;
import static net.site40.rodit.tinyrpg.game.render.Strings.DIALOG_AI_NOTHING;
import static net.site40.rodit.tinyrpg.game.render.Strings.DIALOG_AI_WEAPONS;
import static net.site40.rodit.tinyrpg.game.render.Strings.getString;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import android.util.Log;
import net.site40.rodit.tinyrpg.game.Dialog.DialogCallback;
import net.site40.rodit.tinyrpg.game.Game;
import net.site40.rodit.tinyrpg.game.SuperCalc;
import net.site40.rodit.tinyrpg.game.entity.EntityLiving;
import net.site40.rodit.tinyrpg.game.entity.mob.EntityMob;
import net.site40.rodit.tinyrpg.game.item.InventoryUtil;
import net.site40.rodit.tinyrpg.game.item.Item;
import net.site40.rodit.tinyrpg.game.item.Weapon;

public class AIBattleProvider implements IBattleProvider{

	public static enum AIDifficulty{
		VERY_STUPID, STUPID, VERY_EASY, EASY, MEDIUM, HARD, VERY_HARD, IMPOSSIBLE, GENIUS;

		public static int getIndex(AIDifficulty difficulty){
			AIDifficulty[] values = AIDifficulty.values();
			for(int i = 0; i < values.length; i++)
				if(values[i] == difficulty)
					return i;
			return -1;
		}
	}

	private Game game;
	private Battle battle;
	private AIDifficulty difficulty;
	private ArrayList<AIOption> options;
	private AITargetProvider targetProvider;

	private EntityLiving owner;

	public AIBattleProvider(EntityMob mob){
		this(mob, mob.getDifficulty());
	}

	public AIBattleProvider(EntityLiving owner, AIDifficulty difficulty){
		this.owner = owner;
		this.difficulty = difficulty;
		this.options = new ArrayList<AIOption>();
		initOptions();
		this.targetProvider = new AITargetProvider();
	}

	public void setDifficulty(AIDifficulty difficulty){
		this.difficulty = difficulty;
	}

	protected void initOptions(){
		//TODO: Add all options.
		options.add(new NothingOption());
		options.add(new WeaponAttackOption());
		options.add(new HealOption());
	}

	protected void sortOptions(){
		Collections.sort(options, new Comparator<AIOption>(){
			@Override
			public int compare(AIOption o0, AIOption o1){
				return o0.getPriority(difficulty) - o1.getPriority(difficulty);
			}
		});
	}

	public void makeDecision(Game game, Battle battle){
		this.game = game;
		this.battle = battle;
		Log.i("AIBattleProvider", "AI choosing option...");
		sortOptions();
		AIOption option = null;
		while(option == null){
			for(AIOption possible : options){
				if(possible.canChoose(game, this)){
					option = possible;
					break;
				}
			}
		}
		Log.i("AIBattleProvider", "AI chosen option " + option.getClass().getName() + ".");
		option.choose(game, this);
	}

	public void decisionMade(Game game, Battle battle){
		if(battle != null)
			battle.madeDecision(game, this);
	}

	@Override
	public EntityLiving getEntity(){
		return owner;
	}

	public class AITargetProvider{

		public AITargetProvider(){}

		public EntityLiving getTarget(Game game, AIBattleProvider provider, AIOption option){
			ArrayList<EntityLiving> opposition = game.getBattle().getOpposition(provider.getEntity()).getMembers();
			int randomIndex = opposition.size() <= 1 ? 0 : game.getRandom().nextInt(opposition.size() - 1);
			switch(provider.difficulty){
			case VERY_STUPID:
			case STUPID:
			case VERY_EASY:
			case EASY:
			case MEDIUM:
				return opposition.get(randomIndex);
			case HARD:
			case VERY_HARD:
			case IMPOSSIBLE:
			case GENIUS:
				int[] chosenCount = new int[opposition.size()];
				for(int i = 0; i < opposition.size(); i++){
					EntityLiving target = opposition.get(i);
					float predictedDamage = 0f;
					if(option instanceof WeaponAttackOption){
						for(Item item : provider.getEntity().getEquipped()){
							if(item instanceof Weapon){
								Weapon weapon = (Weapon)item;
								predictedDamage += SuperCalc.getDirectDamage(game, provider.getEntity(), target, weapon);
							}
						}
					}
					if(predictedDamage >= target.getMaxHealth() - target.getHealth())
						chosenCount[i]++;
				}

				//TODO: Add more checks

				int maxIndex = 0;
				int maxChosen = 0;
				boolean changed = false;
				for(int i = 0; i < chosenCount.length; i++){
					if(chosenCount[i] > maxChosen){
						maxChosen = chosenCount[i];
						maxIndex = i;
						changed = true;
					}
				}
				if(changed)
					return opposition.get(maxIndex);
				else
					return opposition.get(randomIndex);
			}
			return null;
		}
	}

	public abstract static class AIOption{

		private Game game;
		private AIBattleProvider provider;

		private HashMap<AIDifficulty, Integer> priorities;
		private HashMap<AIDifficulty, Float> chances;
		private AIDifficulty minDifficulty;
		private AIDifficulty maxDifficulty;

		public AIOption(){
			this.priorities = new HashMap<AIDifficulty, Integer>();
			this.chances = new HashMap<AIDifficulty, Float>();
			initChances();
			this.minDifficulty = AIDifficulty.STUPID;
			this.maxDifficulty = AIDifficulty.IMPOSSIBLE;
		}

		public void initChances(){
			AIDifficulty[] values = AIDifficulty.values();
			for(int i = 0; i < values.length; i++)
				setChance(values[i], 1f);
		}

		public int getPriority(AIDifficulty difficulty){
			Integer priority = priorities.get(difficulty);
			if(priority == null)
				return Integer.MAX_VALUE;
			return priority;
		}

		public void setPriority(AIDifficulty difficulty, int priority){
			priorities.put(difficulty, priority);
		}

		public float getChance(AIDifficulty difficulty){
			Float chance = chances.get(difficulty);
			if(chance == null)
				return 0f;
			return chance;
		}

		public void setChance(AIDifficulty difficulty, float chance){
			chances.put(difficulty, chance);
		}

		public AIDifficulty getMinDifficulty(){
			return minDifficulty;
		}

		public void setMinDifficulty(AIDifficulty minDifficulty){
			this.minDifficulty = minDifficulty;
		}

		public AIDifficulty getMaxDifficulty(){
			return maxDifficulty;
		}

		public void setMaxDifficulty(AIDifficulty maxDifficulty){
			this.maxDifficulty = maxDifficulty;
		}

		public boolean canChoose(Game game, AIBattleProvider provider){
			this.game = game;
			this.provider = provider;
			boolean flag0 = AIDifficulty.getIndex(minDifficulty) <= AIDifficulty.getIndex(provider.difficulty);
			boolean flag1 = AIDifficulty.getIndex(maxDifficulty) >= AIDifficulty.getIndex(provider.difficulty);
			boolean flag2 = game.getRandom().should(getChance(provider.difficulty));
			return flag0 && flag1 && flag2;
		}

		public abstract void choose(Game game, AIBattleProvider provider);

		public void onChosen(Game game, AIBattleProvider provider){
			provider.decisionMade(game, game.getBattle());
		}

		protected DialogCallback defaultCallback = new DialogCallback(){
			public void onSelected(int selected){
				onChosen(game, provider);
			}
		};
	}

	public static class NothingOption extends AIOption{

		public NothingOption(){
			super();
			setPriority(AIDifficulty.VERY_STUPID, 10);
			setChance(AIDifficulty.VERY_STUPID, 0.2f);
			setMinDifficulty(AIDifficulty.VERY_STUPID);
			setMaxDifficulty(AIDifficulty.VERY_STUPID);
		}

		public void choose(Game game, AIBattleProvider provider){
			game.getHelper().dialog(getString(DIALOG_AI_NOTHING, provider.getEntity().getDisplayName()), EMPTY_STRING_ARRAY, defaultCallback);
		}
	}

	public static class WeaponAttackOption extends AIOption{

		public WeaponAttackOption(){
			super();
			setPriority(AIDifficulty.VERY_STUPID, 0);
			setPriority(AIDifficulty.STUPID, 0);
			setPriority(AIDifficulty.VERY_EASY, 0);
			setPriority(AIDifficulty.EASY, 3);
			setPriority(AIDifficulty.MEDIUM, 4);
			setPriority(AIDifficulty.HARD, 6);
			setPriority(AIDifficulty.VERY_HARD, 7);
			setPriority(AIDifficulty.IMPOSSIBLE, 7);
			setPriority(AIDifficulty.GENIUS, 7);
			setChance(AIDifficulty.VERY_STUPID, 0.95f);
			setChance(AIDifficulty.STUPID, 0.92f);
			setChance(AIDifficulty.VERY_EASY, 0.9f);
			setChance(AIDifficulty.EASY, 0.88f);
			setChance(AIDifficulty.MEDIUM, 0.85f);
			setChance(AIDifficulty.HARD, 0.84f);
			setChance(AIDifficulty.VERY_HARD, 0.82f);
			setChance(AIDifficulty.IMPOSSIBLE, 0.8f);
			setChance(AIDifficulty.GENIUS, 0.78f);
			setMinDifficulty(AIDifficulty.VERY_STUPID);
			setMaxDifficulty(AIDifficulty.GENIUS);
		}

		public boolean canChoose(Game game, AIBattleProvider provider){
			return super.canChoose(game, provider) && InventoryUtil.hasWeaponEquipped(provider.getEntity());
		}

		public void choose(Game game, AIBattleProvider provider){
			for(Item item : provider.getEntity().getEquipped()){
				if(item instanceof Weapon){
					Weapon weapon = (Weapon)item;
					EntityLiving target = provider.targetProvider.getTarget(game, provider, this);
					SuperCalc.attack(game, provider.getEntity(), target, weapon);
					target.attachPaintMixer(Battle.newHitMixer());
				}
			}
			game.getHelper().dialog(getString(DIALOG_AI_WEAPONS, provider.getEntity().getDisplayName()), EMPTY_STRING_ARRAY, defaultCallback);
		}
	}

	public static class HealOption extends AIOption{

		public static final float HEALTH_THRESHOLD = 0.25f;

		public HealOption(){
			super();
			setPriority(AIDifficulty.EASY, 10);
			setPriority(AIDifficulty.MEDIUM, 7);
			setPriority(AIDifficulty.HARD, 5);
			setPriority(AIDifficulty.VERY_HARD, 2);
			setPriority(AIDifficulty.IMPOSSIBLE, 1);
			setPriority(AIDifficulty.GENIUS, 0);
			setChance(AIDifficulty.EASY, 0.5f);
			setChance(AIDifficulty.MEDIUM, 0.57f);
			setChance(AIDifficulty.HARD, 0.65f);
			setChance(AIDifficulty.VERY_HARD, 0.75f);
			setChance(AIDifficulty.IMPOSSIBLE, 0.9f);
			setChance(AIDifficulty.GENIUS, 1f);
			setMinDifficulty(AIDifficulty.EASY);
			setMaxDifficulty(AIDifficulty.GENIUS);
		}

		public boolean canChoose(Game game, AIBattleProvider provider){
			return provider.getEntity().getHealthRatio() <= HEALTH_THRESHOLD && InventoryUtil.containsHealingItem(provider.getEntity().getInventory()) && super.canChoose(game, provider);
		}

		public void choose(Game game, AIBattleProvider provider){
			Item bestHealing = InventoryUtil.getBestHealingItem(provider.getEntity().getInventory());
			if(bestHealing != null){
				bestHealing.onEquip(game, provider.getEntity());
				game.getHelper().dialog(getString(DIALOG_AI_HEAL, provider.getEntity().getDisplayName(), InventoryUtil.grammer(bestHealing)), EMPTY_STRING_ARRAY, defaultCallback);
			}
		}
	}
}
