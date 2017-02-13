package net.site40.rodit.tinyrpg.game.entity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import net.site40.rodit.tinyrpg.game.Game;
import net.site40.rodit.tinyrpg.game.SuperCalc;
import net.site40.rodit.tinyrpg.game.battle.AIBattleProvider;
import net.site40.rodit.tinyrpg.game.battle.AIBattleProvider.AIDifficulty;
import net.site40.rodit.tinyrpg.game.battle.IBattleProvider;
import net.site40.rodit.tinyrpg.game.combat.Attack;
import net.site40.rodit.tinyrpg.game.effect.Effect;
import net.site40.rodit.tinyrpg.game.faction.Faction;
import net.site40.rodit.tinyrpg.game.faction.FactionStats;
import net.site40.rodit.tinyrpg.game.item.Hair;
import net.site40.rodit.tinyrpg.game.item.Item;
import net.site40.rodit.tinyrpg.game.item.ItemEquippable;
import net.site40.rodit.tinyrpg.game.item.ItemStack;
import net.site40.rodit.tinyrpg.game.item.Weapon;
import net.site40.rodit.tinyrpg.game.item.armour.Armour;
import net.site40.rodit.tinyrpg.game.render.Animation;
import net.site40.rodit.tinyrpg.game.render.SpriteSheet;
import net.site40.rodit.tinyrpg.game.render.SpriteSheet.MovementState;
import net.site40.rodit.tinyrpg.game.util.Direction;
import net.site40.rodit.util.TinyInputStream;
import net.site40.rodit.util.TinyOutputStream;
import net.site40.rodit.util.Util;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.graphics.Canvas;
import android.text.TextUtils;

public class EntityLiving extends Entity{

	protected int health;
	protected int maxHealth;
	protected int magika;
	protected float velocityX;
	protected float velocityY;
	protected EntityStats stats;
	protected ItemStack[] equipped;
	protected ArrayList<Attack> attacks;
	protected IBattleProvider battleProvider;
	protected ArrayList<Effect> effects;
	public boolean drawEquipmentOverlay;
	protected String displayName;
	protected int humanity;
	protected FactionStats faction;

	public boolean parryCritFlag = false;

	public EntityLiving(){
		this(10, 10);
	}

	public EntityLiving(int maxHealth){
		this(maxHealth, maxHealth);
	}

	public EntityLiving(int health, int maxHealth){
		super();
		this.health = health;
		this.maxHealth = maxHealth;
		this.magika = 0;
		this.velocityX = this.velocityY = 0f;
		this.stats = new EntityStats();
		this.equipped = new ItemStack[10];
		equipped[ItemEquippable.SLOT_HAIR] = new ItemStack(new Hair(), 1);
		this.attacks = new ArrayList<Attack>();
		this.battleProvider = new AIBattleProvider(this, AIDifficulty.MEDIUM);
		this.effects = new ArrayList<Effect>();
		this.drawEquipmentOverlay = true;
		this.humanity = 0;
		this.faction = new FactionStats();

		getHair().setId(0);
		getHair().setColor("black");
	}

	@Override
	public String getDisplayName(){
		return displayName;
	}

	public void setDisplayName(String displayName){
		this.displayName = displayName;
	}
	
	public int getHumanity(){
		return humanity;
	}
	
	public void setHumanity(int humanity){
		this.humanity = humanity;
	}
	
	public void addHumanity(int add){
		humanity += add;
	}
	
	public void subHumanity(int sub){
		humanity -= sub;
	}
	
	public FactionStats getFaction(){
		return faction;
	}
	
	public void setFaction(FactionStats faction){
		this.faction = faction;
	}
	
	public void setFaction(Faction faction){
		setFaction(faction, 0);
	}
	
	public void setFaction(Faction faction, int level){
		this.faction = new FactionStats(faction, level);
	}

	public Hair getHair(){
		return (Hair)equipped[ItemEquippable.SLOT_HAIR].getItem();
	}

	public int getHealth(){
		return health;
	}

	public void setHealth(int health){
		this.health = health;
	}

	public int getMaxHealth(){
		return stats.getMaxHealth(maxHealth);
	}

	public int getMaxHealthUnmodified(){
		return maxHealth;
	}

	public void setMaxHealth(int maxHealth){
		this.maxHealth = maxHealth;
	}

	public float getHealthRatio(){
		return (float)health / (float)getMaxHealth();
	}

	public void heal(int amount){
		int nHealth = health + amount;
		if(nHealth >= getMaxHealth())
			health = getMaxHealth();
		else
			health = nHealth;
	}

	public float getTotalDefence(){
		float defence = 0f;
		for(int i = 0; i < equipped.length; i++){
			ItemStack stack = equipped[i];
			Item it = stack == null ? null : stack.getItem();
			if(it instanceof Armour)
				defence += ((Armour)it).getArmourValue();
		}
		return defence * EntityStats.fPointF(1f, stats.getDefence() * 10);
	}

	public void hit(Game game, Entity user, Damage damage){
		Object source0 = damage.getSource().pop();
		if(source0 instanceof Weapon)
			((Weapon)source0).onHit(game, user, this);
		hurt((int)damage.getDamage());
		for(int i = 0; i < equipped.length; i++){
			ItemStack stack = equipped[i];
			Item item = stack == null ? null : stack.getItem();
			if(item instanceof Armour)
				((Armour)item).onHit(game, user, this);
		}
	}

	protected void hurt(int amount){
		amount = (int)Math.abs(amount);
		health -= amount;
		if(health < 0)
			health = 0;
	}

	public void addHealth(float amount){
		health += amount;
	}

	public int getMagika(){
		return magika;
	}

	public int getMaxMagika(){
		return SuperCalc.getMaxMagika(this);
	}

	public float getMagikaRatio(){
		return (float)magika / (float)getMaxMagika();
	}

	public void setMagika(int magika){
		this.magika = magika;
	}

	public void healMagika(int amount){
		int nMagika = magika + amount;
		int max = getMaxMagika();
		magika = magika > max ? max : nMagika;
	}

	public void useMagika(int amount){
		magika = magika - amount >= 0 ? magika - amount : 0;
	}

	public boolean isDead(){
		return health <= 0f;
	}

	public void move(float x, float y){
		velocityX += x;
		velocityY += y;
	}

	public float getVelocityX(){
		return velocityX;
	}

	public float getVelocityY(){
		return velocityY;
	}

	public void incVelocityX(float x){
		this.velocityX += x;
	}

	public void incVelocityY(float y){
		this.velocityY += y;
	}

	public void setVelocityX(float x){
		this.velocityX = x;
	}

	public void setVelocityY(float y){
		this.velocityY = y;
	}

	public void setVelocity(float x, float y){
		velocityX = x;
		velocityY = y;
	}

	public EntityStats getStats(){
		return stats;
	}

	public void setStats(EntityStats stats){ 
		this.stats = stats;
	}

	public ItemStack[] getEquipped(){
		return equipped;
	}

	public ItemStack getEquipped(int slot){
		return slot < 0 || slot >= equipped.length ? null : equipped[slot];
	}

	public void setEquipped(int slot, ItemStack item){
		ItemStack cEquip = equipped[slot];
		if((cEquip != null && item == null) || (cEquip == null && item != null) || (cEquip != null && item != null && item.getItem() != cEquip.getItem())){
			resetCache();
		}
		equipped[slot] = item;
	}

	public int getSlot(ItemStack item){
		for(int i = 0; i < equipped.length; i++)
			if(equipped[i] == item)
				return i;
		return -1;
	}

	public ItemStack getEquippedItem(int slot){
		return getEquipped(slot);
	}

	public boolean isEquipped(ItemStack item){
		return getSlot(item) > -1;
	}

	public ArrayList<Attack> getAttacks(){
		return attacks;
	}

	public void addAttack(Attack attack){
		if(!attacks.contains(attack))
			attacks.add(attack);
	}

	public void removeAttack(Attack attack){
		attacks.remove(attack);
	}

	public Attack getAttack(int index){
		return attacks.get(index);
	}

	public IBattleProvider getBattleProvider(){
		return battleProvider;
	}

	public void setBattleProvider(IBattleProvider battleProvider){
		this.battleProvider = battleProvider;
	}

	public ArrayList<Effect> getEffects(){
		return effects;
	}

	public void clearNegativeEffects(){
		ArrayList<Effect> remove = new ArrayList<Effect>();
		for(Effect effect : effects)
			if(effect.isNegative())
				remove.add(effect);
		effects.removeAll(remove);
		remove.clear();
	}

	public void addEffect(String effect){
		addEffect(Effect.get(effect));
	}

	public void addEffect(String effect, int level){
		addEffect(Effect.get(effect), level);
	}

	public void addEffect(Effect effect){
		addEffect(effect, effect.getLevel());
	}

	public void addEffect(Effect effect, int level){
		Effect nEffect = null;
		try{
			nEffect = effect.getClass().newInstance();
		}catch(Exception e){
			e.printStackTrace();
		}
		if(nEffect == null)
			return;
		nEffect.copy(effect);
		nEffect.setLevel(level);
		effects.add(nEffect);
	}

	public static final String BATTLE_PACKAGE = "net.site40.rodit.tinyrpg.game.battle";
	@SuppressWarnings("unchecked")
	@Override
	public void linkConfig(Document document){
		super.linkConfig(document);
		Element root = (Element)document.getElementsByTagName("entity").item(0);

		this.maxHealth = Util.tryGetInt(root.getAttribute("maxHealth"), 10);
		this.health = Util.tryGetInt(root.getAttribute("health"), maxHealth);
		this.magika = Util.tryGetInt(root.getAttribute("magika"), 0);
		this.drawEquipmentOverlay = Util.tryGetBool(root.getAttribute("drawEquipment"), true);

		Element statsNode = (Element)root.getElementsByTagName("stats").item(0);
		stats.setSpeed(Util.tryGetFloat(statsNode.getAttribute("speed"), stats.getSpeed()));
		stats.setStrength(Util.tryGetFloat(statsNode.getAttribute("strength"), stats.getStrength()));
		stats.setDefence(Util.tryGetFloat(statsNode.getAttribute("defence"), stats.getDefence()));
		stats.setLuck(Util.tryGetFloat(statsNode.getAttribute("luck"), stats.getLuck()));
		stats.setMagika(Util.tryGetFloat(statsNode.getAttribute("magika"), stats.getMagika()));
		stats.setForge(Util.tryGetFloat(statsNode.getAttribute("forge"), stats.getForge()));

		NodeList equipped = root.getElementsByTagName("equip");
		for(int i = 0; i < equipped.getLength(); i++){
			Node n = equipped.item(i);
			if(n.getNodeType() != Node.ELEMENT_NODE)
				continue;
			Element equip = (Element)n;
			String itemName = equip.getAttribute("item");
			int slot = Util.tryGetSlot(equip.getAttribute("slot"), -1);
			if(slot >= 0 && slot < this.equipped.length)
				this.equipped[slot] = inventory.getExistingStack(Item.get(itemName));
		}

		NodeList attackNodes = root.getElementsByTagName("attack");
		for(int i = 0; i < attackNodes.getLength(); i++){
			Node n = attackNodes.item(i);
			if(n.getNodeType() != Node.ELEMENT_NODE)
				continue;
			Element attackEl = (Element)n;
			String name = attackEl.getAttribute("name");
			Attack attack = Attack.get(name);
			if(attack != null)
				attacks.add(attack);
		}

		String nDisplayName = root.getAttribute("displayName");
		this.displayName = TextUtils.isEmpty(nDisplayName) ? displayName : nDisplayName;

		try{
			String className = root.getAttribute("battleProvider");
			if(!className.startsWith(BATTLE_PACKAGE))
				className = BATTLE_PACKAGE + className;
			Class<? extends IBattleProvider> bpClass = (Class<? extends IBattleProvider>)Class.forName(className);
			IBattleProvider provider = bpClass.getConstructor(EntityLiving.class).newInstance(this);
			if(provider != null)
				this.battleProvider = provider;
		}catch(Exception e){}
	}

	@Override
	public void load(Game game, TinyInputStream in)throws IOException{
		super.load(game, in);
		this.health = in.readInt();
		this.maxHealth = in.readInt();
		this.magika = in.readInt();
		this.stats = new EntityStats();
		stats.load(in);
		this.equipped = new ItemStack[10];
		for(int i = 0; i < ItemEquippable.SLOT_HAIR; i++){
			int stackIndex = in.readInt();
			ItemStack stack = stackIndex > -1 ? inventory.getItemStackByIndex(stackIndex) : null;
			setEquipped(i, stack);
			if(stack != null)
				stack.getItem().onEquip(game, this);
		}
		Hair hair = new Hair();
		hair.setId(in.readInt());
		hair.setColor(in.readString());
		this.equipped[ItemEquippable.SLOT_HAIR] = new ItemStack(hair, 1);
		this.attacks = new ArrayList<Attack>();
		int attackCount = in.readInt();
		int attackRead = 0;
		while(attackRead < attackCount){
			attacks.add(Attack.get(in.readString()));
			attackRead++;
		}
		int effectCount = in.readInt();
		int effectRead = 0;
		while(effectRead < effectCount){
			String effectCls = in.readString();
			try{
				Class<? extends Effect> cls = (Class<? extends Effect>)Class.forName(effectCls);
				Effect e = cls.newInstance();
				e.load(game, in, this);
				effects.add(e);
			}catch(Exception e){
				e.printStackTrace();
			}
			effectRead++;
		}
		this.drawEquipmentOverlay = in.readBoolean();
		this.displayName = in.readString();
		this.humanity = in.readInt();
		this.faction.load(in);
	}

	@Override
	public void save(TinyOutputStream out)throws IOException{
		super.save(out);
		out.write(health);
		out.write(maxHealth);
		out.write(magika);
		stats.save(out);
		for(int i = 0; i < ItemEquippable.SLOT_HAIR; i++)
			out.write(equipped[i] == null ? -1 : inventory.getIndexByItemStack(equipped[i]));
		Hair hair = (Hair)equipped[ItemEquippable.SLOT_HAIR].getItem();
		out.write(hair.getId());
		out.writeString(hair.getColor());
		out.write(attacks.size());
		for(Attack attack : attacks)
			out.writeString(attack.getName());
		out.write(effects.size());
		for(Effect e : effects){
			out.writeString(e.getClass().getCanonicalName());
			e.save(out);
		}
		out.write(drawEquipmentOverlay);
		out.writeString(displayName);
		out.write(humanity);
		faction.save(out);
	}

	private ArrayList<Effect> uEffRemove = new ArrayList<Effect>();
	private float uNx, uNy;
	@Override
	public void update(Game game){
		super.update(game);

		uEffRemove.clear();
		for(Effect effect : effects){
			if(!effect.isStarted())
				effect.start(game, this);
			if(effect.isStopped())
				uEffRemove.add(effect);
		}
		effects.removeAll(uEffRemove);

		if(health > getMaxHealth())
			health = getMaxHealth();

		if(velocityX != 0 || velocityY != 0){
			moveState = MovementState.WALK;
		}else{
			moveState = MovementState.IDLE;
		}

		if(velocityX > 0){
			direction = Direction.D_RIGHT;
		}else if(velocityX < 0){
			direction = Direction.D_LEFT;
		}

		if(velocityY > 0){
			direction = Direction.D_DOWN;
		}else if(velocityY < 0){
			direction = Direction.D_UP;
		}

		uNx = bounds.getX() + velocityX;
		uNy = bounds.getY() + velocityY;
		if(!isDead() && game.getMap() != null && game.getMap().checkMove(game, this, uNx, uNy)){
			bounds.setX(uNx);
			bounds.setY(uNy);
		}

		velocityX = velocityY = 0f;
	}

	public void resetCache(){
		if(equipCache != null){
			synchronized(equipCache){
				equipCache.clear();
			}
		}
	}

	private Animation equippedCache;
	private HashMap<MovementState, HashMap<Direction, Animation>> equipCache = new HashMap<MovementState, HashMap<Direction, Animation>>();
	@Override
	public void draw(Game game, Canvas canvas){
		Object res = game.getResources().getObject(resource);
		if(res instanceof SpriteSheet){
			SpriteSheet sheet = (SpriteSheet)res;
			equippedCache = null;
			HashMap<Direction, Animation> bmpCache = equipCache.get(moveState);
			if(bmpCache == null){
				equipCache.put(moveState, bmpCache = new HashMap<Direction, Animation>());
			}
			equippedCache = bmpCache.get(direction);

			if(equippedCache == null){
				//equippedCache = Bitmap.createBitmap((int)this.width, (int)this.height, Config.ARGB_8888);

				//Canvas equippedCanvas = new Canvas(equippedCache);
				//((Hair)equipped[ItemEquippable.SLOT_HAIR]).drawSpriteOverlay(equippedCanvas, game, this, this.paint);
				ArrayList<Animation> animations = new ArrayList<Animation>();
				animations.add(sheet.getAnimation(moveState, direction));
				animations.add(((SpriteSheet)game.getResources().getObject(((Hair)equipped[ItemEquippable.SLOT_HAIR].getItem()).getDefaultSpriteSheet())).getAnimation(moveState, direction));
				if(drawEquipmentOverlay){
					for(int i = 0; i < equipped.length; i++){
						ItemStack stack = equipped[i];
						Item item = stack == null ? null : stack.getItem();
						if(item instanceof ItemEquippable && i != ItemEquippable.SLOT_HAIR){
							SpriteSheet itemSheet = (SpriteSheet)game.getResources().getObject(((ItemEquippable)item).getDefaultSpriteSheet());
							if(itemSheet != null)
								animations.add(itemSheet.getAnimation(moveState, direction));
							//((ItemEquippable)item).drawSpriteOverlay(equippedCanvas, game, this, this.paint);
						}
					}
					if(moveState == MovementState.IDLE)
						equippedCache = Animation.mergeSpriteIdleAnimations(animations.toArray(new Animation[0]));
					else
						equippedCache = Animation.mergeSpriteMoveAnimations(animations.toArray(new Animation[0]));
				}
				bmpCache.put(direction, equippedCache);
			}

			if(equippedCache != null){
				synchronized(equippedCache){
					canvas.drawBitmap(equippedCache.getFrame(game.getTime()), null, getBounds().get(), this.paint);
				}
			}
		}else
			super.draw(game, canvas);
	}

	public void copy(EntityLiving entity){
		super.copy(entity);
		this.health = entity.health;
		this.maxHealth = entity.maxHealth;
		this.magika = entity.magika;
		this.stats = entity.stats;
		this.equipped = entity.equipped;
		this.attacks = entity.attacks;
		this.battleProvider = entity.battleProvider;
		this.effects = entity.effects;
		this.drawEquipmentOverlay = entity.drawEquipmentOverlay;
		this.displayName = entity.displayName;
		this.humanity = entity.humanity;
	}
}
