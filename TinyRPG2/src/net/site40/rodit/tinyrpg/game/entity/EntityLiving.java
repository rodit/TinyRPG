package net.site40.rodit.tinyrpg.game.entity;

import java.io.IOException;
import java.util.ArrayList;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.text.TextUtils;
import net.site40.rodit.tinyrpg.game.Game;
import net.site40.rodit.tinyrpg.game.SuperCalc;
import net.site40.rodit.tinyrpg.game.battle.AIBattleProvider;
import net.site40.rodit.tinyrpg.game.battle.AIBattleProvider.AIDifficulty;
import net.site40.rodit.tinyrpg.game.battle.IBattleProvider;
import net.site40.rodit.tinyrpg.game.combat.Attack;
import net.site40.rodit.tinyrpg.game.effect.Effect;
import net.site40.rodit.tinyrpg.game.item.Hair;
import net.site40.rodit.tinyrpg.game.item.Item;
import net.site40.rodit.tinyrpg.game.item.ItemEquippable;
import net.site40.rodit.tinyrpg.game.item.Weapon;
import net.site40.rodit.tinyrpg.game.item.armour.Armour;
import net.site40.rodit.tinyrpg.game.render.SpriteSheet.MovementState;
import net.site40.rodit.tinyrpg.game.util.Direction;
import net.site40.rodit.util.TinyInputStream;
import net.site40.rodit.util.TinyOutputStream;
import net.site40.rodit.util.Util;

public class EntityLiving extends Entity{

	protected int health;
	protected int maxHealth;
	protected int magika;
	protected float velocityX;
	protected float velocityY;
	protected EntityStats stats;
	protected Item[] equipped;
	protected ArrayList<Attack> attacks;
	protected IBattleProvider battleProvider;
	protected ArrayList<Effect> effects;
	public boolean drawEquipmentOverlay;
	protected String displayName;

	protected float lastX;
	protected float lastY;

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
		this.equipped = new Item[10];
		equipped[ItemEquippable.SLOT_HAIR] = new Hair();
		this.attacks = new ArrayList<Attack>();
		this.battleProvider = new AIBattleProvider(this, AIDifficulty.MEDIUM);
		this.effects = new ArrayList<Effect>();
		this.drawEquipmentOverlay = true;

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

	public Hair getHair(){
		return (Hair)equipped[ItemEquippable.SLOT_HAIR];
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
			Item it = equipped[i];
			if(it instanceof Armour)
				defence += ((Armour)it).getArmourValue();
		}
		return defence * EntityStats.fPointF(1f, stats.getDefence() * 10);
	}

	public void hurt(int amount){
		amount = (int)Math.abs(amount);
		health -= amount;
		if(health < 0)
			health = 0;
	}

	public void hurt(Game game, EntityLiving user, Weapon weapon){
		SuperCalc.attack(game, user, this, weapon);
	}

	public void addHealth(float amount){
		health += amount;
	}

	public void removeHealth(float amount){
		health -= amount;
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

	public Item[] getEquipped(){
		return equipped;
	}

	public Item getEquipped(int slot){
		return slot < 0 || slot >= equipped.length ? null : equipped[slot];
	}

	public void setEquipped(int slot, Item item){
		if(equipped[slot] != item)
			equippedCache = null;
		equipped[slot] = item;
	}

	public int getSlot(Item item){
		for(int i = 0; i < equipped.length; i++)
			if(equipped[i] == item)
				return i;
		return -1;
	}

	public Item getEquippedItem(int slot){
		return getEquipped(slot);
	}

	public boolean isEquipped(Item item){
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

	public float getLastX(){
		return lastX;
	}

	public float getlastY(){
		return lastY;
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
				this.equipped[slot] = Item.get(itemName);
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
		this.equipped = new Item[10];
		for(int i = 0; i < ItemEquippable.SLOT_HAIR; i++){
			Item item = Item.get(in.readString());
			setEquipped(i, item);
			if(item != null)
				item.onEquip(game, this);
		}
		Hair hair = new Hair();
		hair.setId(in.readInt());
		hair.setColor(in.readString());
		this.equipped[ItemEquippable.SLOT_HAIR] = hair;
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
	}
	
	@Override
	public void save(TinyOutputStream out)throws IOException{
		super.save(out);
		out.write(health);
		out.write(maxHealth);
		out.write(magika);
		stats.save(out);
		for(int i = 0; i < ItemEquippable.SLOT_HAIR; i++)
			out.writeString(equipped[i] == null ? "null" : equipped[i].getName());
		Hair hair = (Hair)equipped[ItemEquippable.SLOT_HAIR];
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
	}

	@Override
	public void update(Game game){
		super.update(game);

		ArrayList<Effect> effRemove = new ArrayList<Effect>();
		for(Effect effect : effects){
			if(!effect.isStarted())
				effect.start(game, this);
			if(effect.isStopped())
				effRemove.add(effect);
		}
		effects.removeAll(effRemove);

		if(health > getMaxHealth())
			health = getMaxHealth();

		if(velocityX != 0 || velocityY != 0){
			moveState = MovementState.WALK;
			resetCache();
		}else{
			moveState = MovementState.IDLE;
			resetCache();
		}

		if(velocityX > 0){
			direction = Direction.D_RIGHT;
			resetCache();
		}else if(velocityX < 0){
			direction = Direction.D_LEFT;
			resetCache();
		}
		
		if(velocityY > 0){
			direction = Direction.D_DOWN;
			resetCache();
		}else if(velocityY < 0){
			direction = Direction.D_UP;
			resetCache();
		}

		float nx = x + velocityX;
		float ny = y + velocityY;
		if(!isDead() && game.getMap() != null && game.getMap().checkMove(game, this, nx, ny)){
			x = nx;
			y = ny;
		}

		velocityX = velocityY = 0f;

		lastX = x;
		lastY = y;
	}

	@Override
	public void resetCache(){
		super.resetCache();
		if(equippedCache != null){
			synchronized(equippedCache){
				equippedCache.recycle();
				this.equippedCache = null;
			}
		}
	}
	
	private Bitmap equippedCache;
	@Override
	public void draw(Game game, Canvas canvas){
		super.draw(game, canvas);
		if(equippedCache == null){
			equippedCache = Bitmap.createBitmap((int)this.width, (int)this.height, Config.ARGB_8888);
			
			Canvas equippedCanvas = new Canvas(equippedCache);
			((Hair)equipped[ItemEquippable.SLOT_HAIR]).drawSpriteOverlay(equippedCanvas, game, this, this.paint);
			if(drawEquipmentOverlay){
				for(int i = 0; i < equipped.length; i++){
					Item item = equipped[i];
					if(item instanceof ItemEquippable && i != ItemEquippable.SLOT_HAIR)
						((ItemEquippable)item).drawSpriteOverlay(equippedCanvas, game, this, this.paint);
				}
			}
		}

		if(equippedCache != null){
			synchronized(equippedCache){
				canvas.drawBitmap(equippedCache, null, getBounds(), this.paint);
			}
		}
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
	}
}
