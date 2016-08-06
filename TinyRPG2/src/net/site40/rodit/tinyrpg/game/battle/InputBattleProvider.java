package net.site40.rodit.tinyrpg.game.battle;

import java.util.ArrayList;

import net.site40.rodit.tinyrpg.game.Dialog.DialogCallback;
import net.site40.rodit.tinyrpg.game.Game;
import net.site40.rodit.tinyrpg.game.SuperCalc;
import net.site40.rodit.tinyrpg.game.combat.Attack;
import net.site40.rodit.tinyrpg.game.entity.EntityLiving;
import net.site40.rodit.tinyrpg.game.entity.EntityPlayer;
import net.site40.rodit.tinyrpg.game.item.Inventory.InventoryProvider;
import net.site40.rodit.tinyrpg.game.item.Item;
import net.site40.rodit.tinyrpg.game.item.ItemEquippable;
import net.site40.rodit.tinyrpg.game.item.ItemStack;
import net.site40.rodit.tinyrpg.game.item.Weapon;
import net.site40.rodit.util.Util;

public class InputBattleProvider implements IBattleProvider{

	public static final int TARGET_SELECT_ATTACK_WEAPON = 1;
	public static final int TARGET_SELECT_ATTACK_SP = 2;

	private Game game;
	private Battle battle;

	private ReturnCallback returnCallback = new ReturnCallback();
	private MainOptionsCallback callback0 = new MainOptionsCallback();
	private AttackCallback attackCallback = new AttackCallback();
	private ItemCallback itemCallback = new ItemCallback();
	private RunCallback runCallback = new RunCallback();
	private FinishCallback finishCallback = new FinishCallback();
	private TargetSelectionCallback targetSelectionCallback = new TargetSelectionCallback();

	private int targetSelectionMode = -1;

	private EntityPlayer player;

	public InputBattleProvider(EntityPlayer player){
		this.player = player;
	}

	@Override
	public void makeDecision(Game game, Battle battle){
		this.game = game;
		this.battle = battle;
		ask();
	}

	public void ask(){
		game.getHelper().dialog("What would you like to do?", new String[] { "Use Weapon", "Sp. Attack", "Potion", "Run" }, callback0);
	}

	@Override
	public EntityLiving getEntity(){
		return player;
	}

	public void attackWeapon(EntityLiving target){
		Item[] equipped = new Item[] { player.getEquipped(ItemEquippable.SLOT_HAND_0), player.getEquipped(ItemEquippable.SLOT_HAND_1) };
		int notNull = 0;
		for(int i = 0; i < equipped.length; i++){
			Item item = equipped[i];
			if(item != null){
				Weapon weapon = (Weapon)item;
				SuperCalc.attack(game, getEntity(), target, weapon);
				target.attachPaintMixer(Battle.newHitMixer());
				notNull++;
			}
		}
		if(notNull == 0)
			game.getHelper().dialog("You do not have a weapon equipped.", new String[0], returnCallback);
		else
			game.getHelper().dialog("You used equipped weapon(s).", new String[0], finishCallback);
	}

	public String[] genNameList(ArrayList<EntityLiving> entities){
		String[] names = new String[entities.size()];
		for(int i = 0; i < entities.size(); i++)
			names[i] = entities.get(i).getDisplayName();
		return names;
	}

	public void selectTarget(int mode){
		ArrayList<EntityLiving> opposition = battle.getOpposition(player).getMembers();
		targetSelectionMode = mode;
		if(opposition.size() > 0)
			game.getHelper().dialog("Who will you target?", Util.join(genNameList(opposition), new String[] { "Cancel" }), targetSelectionCallback);
		else
			attackWeapon(opposition.get(0));
	}

	public class ReturnCallback implements DialogCallback{

		@Override
		public void onSelected(int option){
			makeDecision(game, battle);
		}
	}

	public class MainOptionsCallback implements DialogCallback{

		@Override
		public void onSelected(int option){
			switch(option){
			case 0:
				selectTarget(TARGET_SELECT_ATTACK_WEAPON);
				break;
			case 1:
				if(player.getAttacks().size() == 0){
					game.getHelper().dialog("You do not have any special attacks.", new String[0], returnCallback);
				}else{
					String[] attackNames = new String[player.getAttacks().size() + 1];
					for(int i = 0; i < player.getAttacks().size() - 1; i++)
						attackNames[i] = player.getAttack(i).getShowName();
					attackNames[attackNames.length - 1] = "Back";
					game.getHelper().dialog("Select an attack.", attackNames, attackCallback);
				}
				break;
			case 2:
				ArrayList<String> options = new ArrayList<String>();
				for(ItemStack stack : player.getInventory().getProvider(player).provide(InventoryProvider.TAB_POTIONS))
					options.add(stack.getItem().getShowName());
				options.add("Back");
				String[] optionsArr = options.toArray(new String[0]);
				game.getHelper().dialog("Which item would you like to use?", optionsArr, itemCallback);
				break;
			case 3:
				if(game.getRandom().should(0.9f))
					game.getHelper().dialog("You escaped successfully.", new String[0], runCallback);
				else
					game.getHelper().dialog("You tried to escape but your attempt failed miserably.", new String[0], finishCallback);

				break;
			}
		}
	}

	public class FinishCallback implements DialogCallback{

		@Override
		public void onSelected(int option){
			battle.madeDecision(game, InputBattleProvider.this);
		}
	}

	public class ItemCallback implements DialogCallback{

		@Override
		public void onSelected(int option){
			ArrayList<Item> options = new ArrayList<Item>();
			for(ItemStack stack : player.getInventory().getProvider(player).provide(InventoryProvider.TAB_POTIONS))
				options.add(stack.getItem());
			if(option == options.size())
				returnCallback.onSelected(0);
			else{
				Item selectedItem = options.get(option);
				selectedItem.onEquip(game, player);
				if(selectedItem.isConsumed())
					player.getInventory().getStack(selectedItem).consume();
				game.getHelper().dialog("You used a " + selectedItem.getShowName() + ".", new String[0], finishCallback);
			}
		}
	}

	public class AttackCallback implements DialogCallback{

		@Override
		public void onSelected(int option){
			final int lastOption = player.getAttacks().size();
			if(option == lastOption)
				returnCallback.onSelected(0);
			else
				selectTarget(TARGET_SELECT_ATTACK_SP);
		}
	}

	public class RunCallback implements DialogCallback{

		@Override
		public void onSelected(int option){
			battle.madeDecision(game, InputBattleProvider.this);
			game.setBattle(null);
		}
	}

	public class TargetSelectionCallback implements DialogCallback{

		@Override
		public void onSelected(int option){
			ArrayList<EntityLiving> members = battle.getOpposition(player).getMembers();
			if(option == members.size()){
				returnCallback.onSelected(0);
				return;
			}
			if(targetSelectionMode == TARGET_SELECT_ATTACK_WEAPON)
				attackWeapon(members.get(option));
			else if(targetSelectionMode == TARGET_SELECT_ATTACK_SP){
				Attack attack = player.getAttack(option);
				if(attack == null)
					returnCallback.onSelected(0);
				else{
					attack.onUse(game, player, members.get(option));
					game.getHelper().dialog("You used " + attack.getShowName() + ".", new String[0], finishCallback);
				}
			}else
				game.getHelper().dialog("Invalid selection mode.");
		}
	}
}
