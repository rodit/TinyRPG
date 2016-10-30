package net.site40.rodit.tinyrpg.mp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import net.site40.rodit.rlib.util.ByteArrayReader;
import net.site40.rodit.rlib.util.ByteArrayWriter;
import net.site40.rodit.tinyrpg.game.Game;
import net.site40.rodit.tinyrpg.game.battle.IBattleProvider;
import net.site40.rodit.tinyrpg.game.combat.Attack;
import net.site40.rodit.tinyrpg.game.effect.Effect;
import net.site40.rodit.tinyrpg.game.entity.Entity;
import net.site40.rodit.tinyrpg.game.entity.EntityLiving;
import net.site40.rodit.tinyrpg.game.entity.EntityPlayer;
import net.site40.rodit.tinyrpg.game.entity.EntityStats;
import net.site40.rodit.tinyrpg.game.item.Inventory;
import net.site40.rodit.tinyrpg.game.item.Item;
import net.site40.rodit.tinyrpg.game.item.ItemEquippable;
import net.site40.rodit.tinyrpg.game.item.ItemStack;
import net.site40.rodit.util.Util;
public class Proxy {
	
	public static class Common{
		
		public static final byte HASHMAP = 0;
		public static final byte INVENTORY = 1;
		public static final byte ATTACKS = 2;
		public static final byte EFFECTS = 3;
		public static final byte ENTITY_STATS = 4;
		public static final byte ENTITY_EQUIPPED = 5;
		public static final byte ENTITY = 6;
		public static final byte ENTITY_LIVING = 7;
		public static final byte ENTITY_PLAYER = 8;
		public static final byte INPUT_EVENT = 9;
		public static final byte MAP_UPDATE = 10;
		public static final byte MARK_PLACE = 11;
		public static final byte MARK_REMOVE = 12;
		public static final byte MARK_PLACED = 13;
		public static final byte MARK_ACCEPTED = 14;
	}
	
	public static class RemoteInputEvent{
		
		protected int key;
		protected boolean up;
		
		public RemoteInputEvent(int key, boolean up){
			this.key = key;
			this.up = up;
		}
	}

	public static class Read {

		public static HashMap<String, String> readHashMapState(HashMap<String, String> map, ByteArrayReader reader){
			int count = reader.readInt();
			for(int i = 0; i < count; i++)
				map.put(reader.readString(), reader.readString());
			return map;
		}

		public static Inventory readInventoryState(Inventory inv, ByteArrayReader reader){
			int itemCount = reader.readInt();
			for(int i = 0; i < itemCount; i++)
				inv.add(Item.get(reader.readString()), reader.readInt());
			return inv;
		}

		public static ArrayList<Attack> readAttackStates(ArrayList<Attack> attacks, ByteArrayReader reader){
			int count = reader.readInt();
			for(int i = 0; i < count; i++)
				attacks.add(Attack.get(reader.readString()));
			return attacks;
		}

		public static ArrayList<Effect> readEffectStates(ArrayList<Effect> effects, ByteArrayReader reader){
			int count = reader.readInt();
			for(int i = 0; i < count; i++){
				//TODO: make this work...
			}
			return effects;
		}

		public static EntityStats readEntityStatsState(EntityStats stats, ByteArrayReader reader){
			stats.setLevel(reader.readInt());
			stats.setXp(reader.readInt());
			stats.setSpeed(reader.readFloat());
			stats.setStrength(reader.readFloat());
			stats.setDefence(reader.readFloat());
			stats.setLuck(reader.readFloat());
			stats.setMagika(reader.readFloat());
			stats.setForge(reader.readFloat());
			return stats;
		}

		public static void readEntityEquippedState(Game game, EntityLiving e, ByteArrayReader reader){
			for(int i = 0; i < e.getEquipped().length; i++){
				Item item = Item.get(reader.readString());
				if(item == null || !(item instanceof ItemEquippable))
					continue;
				ItemEquippable ie = (ItemEquippable)item;
				e.setEquipped(i, ie);
				ie.onEquip(game, e);
			}
		}

		public static Entity readEntityState(Entity e, ByteArrayReader reader){
			e.setX(reader.readFloat());
			e.setY(reader.readFloat());
			e.setWidth(reader.readFloat());
			e.setHeight(reader.readFloat());
			e.setResource(reader.readString());
			e.setDirection(Util.tryGetDirection(reader.readString()));
			e.setMoveState(Util.tryGetMoveState(reader.readString()));
			e.setNoclip(reader.readBool());
			e.setMoney(reader.readLong());
			readInventoryState(e.getInventory(), reader);
			e.setScript(reader.readString());
			readHashMapState(e.getRuntimeProperties(), reader);
			return e;
		}

		public static EntityLiving readEntityLivingState(Game game, EntityLiving e, ByteArrayReader reader){
			readEntityState(e, reader);
			e.setHealth(reader.readInt());
			e.setMaxHealth(reader.readInt());
			e.setMagika(reader.readInt());
			readEntityStatsState(e.getStats(), reader);
			readEntityEquippedState(game, e, reader);
			readAttackStates(e.getAttacks(), reader);
			try{
				Class<? extends IBattleProvider> providerCls = (Class<? extends IBattleProvider>)Class.forName(reader.readString());
				IBattleProvider provider = providerCls.getConstructor(EntityLiving.class).newInstance(e);
				e.setBattleProvider(provider);
			}catch(Exception ex){
				ex.printStackTrace();
			}
			//TODO: Finish this...
			readEffectStates(e.getEffects(), reader);
			e.drawEquipmentOverlay = reader.readBool();
			e.setDisplayName(reader.readString());
			return e;
		}
		
		public static EntityPlayer readEntityPlayerState(Game game, EntityPlayer e, ByteArrayReader reader){
			readEntityLivingState(game, e, reader);
			e.setUsername(reader.readString());
			return e;
		}
		
		public static RemoteInputEvent readInputEvent(ByteArrayReader reader){
			return new RemoteInputEvent(reader.readInt(), reader.readBool());
		}
		
		public static MarkObject readMark(ByteArrayReader in){
			return new MarkObject(in.readString(), in.readString(), in.readFloat(), in.readFloat());
		}
	}
	
	public static class Write{
		
		public static void writeHashMapState(HashMap<String, String> map, ByteArrayWriter writer)throws IOException{
			writer.write(map.size());
			for(String key : map.keySet()){
				writer.write(key);
				writer.write(map.get(key));
			}
		}
		
		public static void writeInventoryState(Inventory inventory, ByteArrayWriter writer)throws IOException{
			writer.write(inventory.getItems().size());
			for(ItemStack stack : inventory.getItems()){
				writer.write(stack.getItem().getName());
				writer.write(stack.getAmount());
			}
		}
		
		public static void writeAttackStates(ArrayList<Attack> attacks, ByteArrayWriter writer)throws IOException{
			writer.write(attacks.size());
			for(Attack attack : attacks)
				writer.write(attack.getName());
		}
		
		public static void writeEffectStates(ArrayList<Effect> effects, ByteArrayWriter writer)throws IOException{
			writer.write(effects.size());
			//TODO: Make this work.
		}
		
		public static void writeEntityStatsState(EntityStats stats, ByteArrayWriter writer)throws IOException{
			writer.write(stats.getLevel());
			writer.write(stats.getXp());
			writer.write(stats.getSpeed());
			writer.write(stats.getStrength());
			writer.write(stats.getDefence());
			writer.write(stats.getLuck());
			writer.write(stats.getMagika());
			writer.write(stats.getForge());
		}

		public static void readEntityEquippedState(Game game, EntityLiving e, ByteArrayReader reader){
			for(int i = 0; i < e.getEquipped().length; i++){
				Item item = Item.get(reader.readString());
				if(item == null || !(item instanceof ItemEquippable))
					continue;
				ItemEquippable ie = (ItemEquippable)item;
				e.setEquipped(i, ie);
				ie.onEquip(game, e);
			}
		}
		
		public static void writeEntityEquippedState(EntityLiving e, ByteArrayWriter writer)throws IOException{
			for(int i = 0; i < e.getEquipped().length; i++)
				writer.write(e.getEquipped(i).getName());
		}
		
		public static void writeEntityState(Entity e, ByteArrayWriter writer)throws IOException{
			writer.write(e.getX());
			writer.write(e.getY());
			writer.write(e.getWidth());
			writer.write(e.getHeight());
			writer.write(e.getResource());
			writer.write(e.getDirection().toString());
			writer.write(e.getMoveState().toString());
			writer.write(e.isNoclip());
			writer.write(e.getMoney());
			writeInventoryState(e.getInventory(), writer);
			writer.write(e.getScript());
			writeHashMapState(e.getRuntimeProperties(), writer);
		}
		
		public static void writeEntityLivingState(EntityLiving e, ByteArrayWriter writer)throws IOException{
			writeEntityState(e, writer);
			writer.write(e.getHealth());
			writer.write(e.getMaxHealthUnmodified());
			writer.write(e.getMagika());
			writer.write(e.getMaxMagika());
			writeEntityStatsState(e.getStats(), writer);
			writeEntityEquippedState(e, writer);
			writeAttackStates(e.getAttacks(), writer);
			writer.write(e.getBattleProvider().getClass().getCanonicalName());
			writeEffectStates(e.getEffects(), writer);
			writer.write(e.drawEquipmentOverlay);
			writer.write(e.getDisplayName());
		}
		
		public static void writeEntityPlayerState(EntityPlayer e, ByteArrayWriter writer)throws IOException{
			writeEntityLivingState(e, writer);
			writer.write(e.getUsername());
		}
		
		public static void writeInputEvent(RemoteInputEvent event, ByteArrayWriter writer)throws IOException{
			writer.write(event.key);
			writer.write(event.up);
		}
		
		public static void writeMapUpdate(String mapName, ByteArrayWriter writer)throws IOException{
			writer.write(mapName);
		}
		
		public static void writeMark(String mapName, String username, float x, float y, ByteArrayWriter writer)throws IOException{
			writer.write(mapName);
			writer.write(username);
			writer.write(x);
			writer.write(y);
		}
	}
}
