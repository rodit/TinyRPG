package net.site40.rodit.tinyrpg.game.battle;

import static net.site40.rodit.tinyrpg.game.render.Strings.getString;

import java.util.ArrayList;

import net.site40.rodit.tinyrpg.game.Dialog.DialogCallback;
import net.site40.rodit.tinyrpg.game.Game;
import net.site40.rodit.tinyrpg.game.SuperCalc;
import net.site40.rodit.tinyrpg.game.combat.Attack;
import net.site40.rodit.tinyrpg.game.entity.Damage;
import net.site40.rodit.tinyrpg.game.entity.Damage.DamageType;
import net.site40.rodit.tinyrpg.game.entity.Damage.SourceStack;
import net.site40.rodit.tinyrpg.game.entity.Entity;
import net.site40.rodit.tinyrpg.game.entity.EntityLiving;
import net.site40.rodit.tinyrpg.game.entity.EntityPlayer;
import net.site40.rodit.tinyrpg.game.event.EventReceiver;
import net.site40.rodit.tinyrpg.game.event.EventReceiver.EventType;
import net.site40.rodit.tinyrpg.game.gui.windows.Window;
import net.site40.rodit.tinyrpg.game.gui.windows.WindowInventory;
import net.site40.rodit.tinyrpg.game.gui.windows.WindowItemInfo;
import net.site40.rodit.tinyrpg.game.item.Item;
import net.site40.rodit.tinyrpg.game.item.ItemEquippable;
import net.site40.rodit.tinyrpg.game.item.ItemStack;
import net.site40.rodit.tinyrpg.game.item.Weapon;
import net.site40.rodit.tinyrpg.game.render.Strings;
import net.site40.rodit.tinyrpg.game.render.Strings.GameData;
import net.site40.rodit.util.Util;

public class InputBattleProvider implements IBattleProvider{

	public static final int TARGET_SELECT_ATTACK_WEAPON = 1;
	public static final int TARGET_SELECT_ATTACK_SP = 2;

	private Game game;
	private Battle battle;

	private ReturnCallback returnCallback = new ReturnCallback();
	private MainOptionsCallback callback0 = new MainOptionsCallback();
	private AttackCallback attackCallback = new AttackCallback();
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
		game.getHelper().dialog(Strings.Dialog.BATTLE_INPUT_ASK, Strings.Dialog.BATTLE_INPUT_OPTIONS, callback0);
	}

	@Override
	public EntityLiving getEntity(){
		return player;
	}

	public void attackWeapon(EntityLiving target){
		ItemStack[] equipped = new ItemStack[] { player.getEquipped(ItemEquippable.SLOT_HAND_0), player.getEquipped(ItemEquippable.SLOT_HAND_1) };
		int notNull = 0;
		for(int i = 0; i < equipped.length; i++){
			ItemStack stack = equipped[i];
			Item item = stack == null ? null : stack.getItem();
			if(item != null){
				Weapon weapon = (Weapon)item;
				target.hit(game, player, new Damage(new SourceStack(battle, player, weapon), DamageType.PHYSICAL_ATTACK, SuperCalc.getDamage(game, player, target, weapon)));
				target.attachPaintMixer(Battle.newHitMixer());
				notNull++;
			}
		}
		if(notNull == 0)
			game.getHelper().dialog(Strings.Dialog.NO_WEAPON, new String[0], returnCallback);
		else
			game.getHelper().dialog(Strings.Dialog.WEAPONS_USED, new String[0], finishCallback);
	}

	public String[] genNameList(ArrayList<EntityLiving> entities){
		String[] names = new String[entities.size()];
		for(int i = 0; i < entities.size(); i++)
			names[i] = entities.get(i).getDisplayName();
		return names;
	}

	public void selectTarget(int mode){
		ArrayList<EntityLiving> opposition = battle.getOppositionMembersDrawOrderedAlive(player);
		targetSelectionMode = mode;
		if(opposition.size() > 1)
			game.getHelper().dialog(Strings.Dialog.BATTLE_ASK_TARGET, Util.join(genNameList(opposition), new String[] { "Cancel" }), targetSelectionCallback);
		else if(opposition.size() > 0)
			attackWeapon(opposition.get(0));
		else
			game.getHelper().dialog(Strings.Dialog.BATTLE_NO_OPPOSITION, new String[] { "Hmm... That's strange..." }, returnCallback);
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
					game.getHelper().dialog(Strings.Dialog.NO_SP_ATTACK, new String[0], returnCallback);
				}else{
					String[] attackNames = new String[player.getAttacks().size() + 1];
					for(int i = 0; i < player.getAttacks().size() - 1; i++)
						attackNames[i] = player.getAttack(i).getShowName();
					attackNames[attackNames.length - 1] = "Back";
					game.getHelper().dialog(Strings.Dialog.SELECT_ATTACK, attackNames, attackCallback);
				}
				break;
			case 2:
				final WindowInventory windowInv = new WindowInventory(game);
				EventReceiver itemUsedEvent = new EventReceiver("event_battle_use_item", "", EventType.ITEM_EQUIP){
					@Override
					public void onEvent(Game game, Object... args){
						Entity user = (Entity)args[1];
						if(user != player)
							return;
						windowInv.close();
						WindowItemInfo windowInfo = (WindowItemInfo)game.getWindows().get(WindowItemInfo.class);
						if(windowInfo != null)
							windowInfo.close();
						game.unregisterEvent(this);
						battle.madeDecision(game, InputBattleProvider.this);
					}
				};
				EventReceiver invClosedEvent = new EventReceiver("event_inv_window_closed_battle", "", EventType.WINDOW_CLOSED_BACK){
					@Override
					public void onEvent(Game game, Object... args){
						Window win = (Window)args[0];
						if(win instanceof WindowInventory){
							returnCallback.onSelected(0);
							game.unregisterEvent(this);
						}
					}
				};
				game.registerEvent(itemUsedEvent);
				game.registerEvent(invClosedEvent);
				game.getWindows().register(windowInv);
				windowInv.show();
				break;
			case 3:
				if(game.getRandom().should(0.9f))
					game.getHelper().dialog(Strings.Dialog.ESCAPE_SUCCESSFUL, GameData.EMPTY_STRING_ARRAY, runCallback);
				else
					game.getHelper().dialog(Strings.Dialog.ESCAPE_FAILURE, GameData.EMPTY_STRING_ARRAY, finishCallback);
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

	public class AttackCallback implements DialogCallback{

		@Override
		public void onSelected(int option){
			int lastOption = player.getAttacks().size();
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
			ArrayList<EntityLiving> members = battle.getOppositionMembersDrawOrderedAlive(player);
			if(option == members.size()){
				returnCallback.onSelected(0);
				return;
			}
			System.out.println(option);
			if(targetSelectionMode == TARGET_SELECT_ATTACK_WEAPON)
				attackWeapon(members.get(option));
			else if(targetSelectionMode == TARGET_SELECT_ATTACK_SP){
				Attack attack = player.getAttack(option);
				if(attack == null)
					returnCallback.onSelected(0);
				else{
					attack.onUse(game, player, members.get(option));
					game.getHelper().dialog(getString(Strings.Dialog.SP_ATTACK_USED, attack.getShowName()), new String[0], finishCallback);
				}
			}else
				game.getHelper().dialog(Strings.Dialog.INVALID_SELECTION_MODE);
		}
	}
}
