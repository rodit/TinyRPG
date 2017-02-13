package net.site40.rodit.tinyrpg.game.entity;

import java.io.IOException;

import net.site40.rodit.tinyrpg.game.Game;
import net.site40.rodit.tinyrpg.game.Input;
import net.site40.rodit.tinyrpg.game.SuperCalc;
import net.site40.rodit.tinyrpg.game.battle.InputBattleProvider;
import net.site40.rodit.tinyrpg.game.entity.Damage.DamageType;
import net.site40.rodit.tinyrpg.game.entity.MovementProvider.TileMovementProvider;
import net.site40.rodit.tinyrpg.game.gui.GuiIngameMenu;
import net.site40.rodit.tinyrpg.game.item.Item;
import net.site40.rodit.tinyrpg.game.item.ItemEquippable;
import net.site40.rodit.tinyrpg.game.item.ItemStack;
import net.site40.rodit.tinyrpg.game.item.Weapon;
import net.site40.rodit.tinyrpg.game.item.armour.Shield;
import net.site40.rodit.util.TinyInputStream;
import net.site40.rodit.util.TinyOutputStream;
import android.graphics.Canvas;
import android.graphics.RectF;

public class EntityPlayer extends EntityLiving{

	private String username;
	private String startClass;

	private boolean hasMoved;

	protected MovementProvider movement = new TileMovementProvider();

	public EntityPlayer(){
		super();
		this.name = "player";
		this.username = "New Player";
		this.startClass = "null";
		this.script = "script/entity/player.js";
		this.resource = "character/m/base/white.spr";
		setWidth(16f);
		setHeight(32f);
		stats.setSpeed(2f);
		stats.setStrength(2f);
		stats.setLuck(2f);
		stats.setMagika(2f);
		stats.setLevel(1);
		this.setHealth(getMaxHealth());
		this.setMagika(SuperCalc.getMaxMagika(this));
		this.battleProvider = new InputBattleProvider(this);

		ticker.setInterval(1000L);

		setRuntimeProperty("map_origin", "respawn");

		//TODO: REMOVE
		//this.setNoclip(true);
		if(Game.DEBUG){
			stats.setForge(100f);
			setMoney(1000000l);
		}
	}

	public TileMovementProvider getTileMovementProvider(){
		return (TileMovementProvider)movement;
	}

	//	@Override
	//	public int getMaxHealth(){
	//		return Protect.instance.get(Protect.PLAYER_MAX_HEALTH);
	//	}
	//	
	//	@Override
	//	public void setMaxHealth(int maxHealth){
	//		Protect.instance.set(Protect.PLAYER_MAX_HEALTH, maxHealth);
	//	}
	//	
	//	@Override
	//	public int getHealth(){
	//		return Protect.instance.get(Protect.PLAYER_HEALTH);
	//	}
	//	
	//	@Override
	//	public void setHealth(int health){
	//		Protect.instance.set(Protect.PLAYER_HEALTH, health);
	//	}
	//	
	//	@Override
	//	public int getMagika(){
	//		return Protect.instance.get(Protect.PLAYER_MAGIKA);
	//	}
	//	
	//	@Override
	//	public void setMagika(int magika){
	//		Protect.instance.set(Protect.PLAYER_MAGIKA, magika);
	//	}
	//
	//	@Override
	//	public long getMoney(){
	//		return Protect.instance.getLong(Protect.PLAYER_MONEY);
	//	}
	//	
	//	@Override
	//	public void setMoney(long money){
	//		Protect.instance.setLong(Protect.PLAYER_MONEY, money);
	//	}

	@Override
	public void hit(Game game, Entity user, Damage damage){
		if(startClass.equals("assassin") && damage.getType() == DamageType.PHYSICAL_ATTACK && damage.isFromBattle()){
			float parryChance = SuperCalc.getAssassinParryChance(stats);
			if(game.getRandom().should(parryChance)){
				parryCritFlag = true;
				damage = new Damage(damage.getSource(), DamageType.PHYSICAL_ATTACK, 0f);
				game.getHelper().dialog("You parried the attack!");
			}
		}else if(startClass.equals("mage") && damage.getType() == DamageType.MAGIC_ATTACK){
			float damageMulti = SuperCalc.getMageDamageReductionMulti(stats);
			damage = new Damage(damage.getSource(), DamageType.MAGIC_ATTACK, damageMulti * damage.getDamage());
		}
		super.hit(game, user, damage);
	}

	@Override
	public int getMaxHealth(){
		return (int)((float)SuperCalc.getMaxHealth(this) * stats.hpMulti);
	}

	@Override
	public boolean showName(){
		return true;
	}

	@Override
	public String getDisplayName(){
		return getUsername();
	}

	public String getUsername(){
		return username;
	}

	public void setUsername(String username){
		this.username = username;
	}

	public String getStartClass(){
		return startClass;
	}

	public void setStartClass(String startClass){
		this.startClass = startClass;
	}

	public boolean equipItem(Game game, ItemStack stack){
		if(stack == null)
			return false;
		Item item = stack.getItem();
		if(!(item instanceof ItemEquippable))
			return false;
		if(item instanceof Weapon){
			Weapon weapon = (Weapon)item;
			if(weapon.isTwoHanded()){
				if(!startClass.equals("knight")){
					setEquipped(ItemEquippable.SLOT_HAND_0, stack);
					setEquipped(ItemEquippable.SLOT_HAND_1, null);
				}else
					findAndEquip(game, stack, true, false);
			}else{
				if(startClass.equals("knight") || startClass.equals("warrior"))
					findAndEquip(game, stack, true, true);
				else
					findAndEquip(game, stack, false, true);
			}
		}else{
			ItemEquippable ie = (ItemEquippable)item;
			boolean wasEquipped = false;
			for(int i = 0; i < ie.getEquipSlots().length; i++){
				if(game.getPlayer().getEquippedItem(ie.getEquipSlots()[i]) == null){
					game.getPlayer().setEquipped(ie.getEquipSlots()[i], stack);
					ie.onEquip(game, game.getPlayer());
					wasEquipped = true;
					break;
				}
			}
			if(!wasEquipped){
				ItemStack item0 = game.getPlayer().getEquipped(ie.getEquipSlots()[0]);
				if(item0 != null)
					item0.getItem().onUnEquip(game, game.getPlayer());
				game.getPlayer().setEquipped(ie.getEquipSlots()[0], stack);
				ie.onEquip(game, game.getPlayer());
			}
		}
		return true;
	}

	private void findAndEquip(Game game, ItemStack stack, boolean canDuelWield, boolean canHoldOffhand){
		Item item = stack.getItem();
		if(item instanceof Weapon){
			Weapon weapon = (Weapon)item;
			if(canDuelWield || item.isSmall()){
				if(getEquipped(ItemEquippable.SLOT_HAND_0) == null)
					setEquipped(ItemEquippable.SLOT_HAND_0, stack);
				else if(getEquipped(ItemEquippable.SLOT_HAND_1) == null)
					setEquipped(ItemEquippable.SLOT_HAND_1, stack);
				else{
					ItemStack equipped = getEquipped(ItemEquippable.SLOT_HAND_0);
					if(equipped != null)
						equipped.getItem().onUnEquip(game, this);
					setEquipped(ItemEquippable.SLOT_HAND_0, stack);
				}
				weapon.onEquip(game, this);
			}else{
				setEquipped(ItemEquippable.SLOT_HAND_0, stack);
				weapon.onEquip(game, this);
				ItemStack offhandStack = getEquipped(ItemEquippable.SLOT_HAND_1);
				Item offhand = offhandStack == null ? null : offhandStack.getItem();
				if(offhand != null && (!canHoldOffhand || (!(offhand instanceof Shield) && !offhand.isSmall()))){
					if(offhand != null)
						offhand.onUnEquip(game, this);
					setEquipped(ItemEquippable.SLOT_HAND_1, null);
				}
			}
		}
	}

	public boolean hasMoved(){
		boolean lHasMoved = hasMoved;
		hasMoved = false;
		return lHasMoved;
	}

	public void teleport(Game game, float x, float y){
		setX(x);
		setY(y);
		this.velocityX = 0;
		this.velocityY = 0;
	}

	@Override
	public void load(Game game, TinyInputStream in)throws IOException{
		super.load(game, in);
		this.username = in.readString();
		this.startClass = in.readString();
	}
	
	@Override
	public void save(TinyOutputStream out)throws IOException{
		super.save(out);
		out.writeString(username);
		out.writeString(startClass);
	}
	
	@Override
	public RectF getCollisionBounds(){
		collisionBounds.getPooled1().set(bounds.getX() + 2f, bounds.getY() + 2f + 18f, bounds.getX() + 14f, bounds.getY() + 30f);
		return collisionBounds.getPooled1();
	}
	
	@Override
	public RectF getCollisionBounds(float x, float y){
		collisionBounds.getPooled0().set(x + 2f, y + 2f + 18f, x + 14f, y + 30f);
		return collisionBounds.getPooled0();
	}

	public RectF getCollisionBoundsOld(float x, float y){
		return new RectF(x, y + collisionBounds.getHeight() / 2f, x + collisionBounds.getWidth(), y + collisionBounds.getHeight());
	}

	@Override
	public void update(Game game){		
		movement.move(game, this);

		Input input = game.getInput();
		if(input.allowMovement() && !game.isShowingDialog()){
			if(input.isUp(Input.KEY_ACTION)){
				Object o = game.trace(this);
				if(o != null && o instanceof Entity)
					((Entity)o).onAction(game, this);
			}
			if(input.isUp(Input.KEY_MENU)){
				if(game.getGuis().isVisible(GuiIngameMenu.class))
					game.getGuis().hide(GuiIngameMenu.class);
				else
					game.getGuis().show(GuiIngameMenu.class);
			}
		}

		hasMoved = velocityX > 0 || velocityY > 0;

		super.update(game);
	}

	public void updateMap(Game game){
		/*
		if(game.getBattle() == null && game.getMap() != null && game.getMap().getMap() != null){
			String areaKey = game.getMap().getMap().getMobSpawnAreaKeys(x, y);
			for(MobSpawn spawn : game.getMobSpawns().getMobSpawns(areaKey)){
				if(spawn.shouldEncounterFrame(game)){
					spawn.encounter(game, new Team(game.getPlayer()));
					break;
				}
			}
		}
		 */
	}

	@Override
	public void draw(Game game, Canvas canvas){
		//game.pushTranslate(canvas);
		super.draw(game, canvas);
		//game.popTranslate(canvas);
	}
	
	@Override
	public int getRenderLayer(){
		return RenderLayer.TOP_OVER_ALL;
	}

	public void copy(EntityPlayer player){
		super.copy(player);
		this.username = player.username;
	}
}
