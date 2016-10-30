package net.site40.rodit.tinyrpg.game.entity;

import java.io.IOException;

import android.graphics.Canvas;
import android.graphics.RectF;
import net.site40.rodit.tinyrpg.game.Game;
import net.site40.rodit.tinyrpg.game.Input;
import net.site40.rodit.tinyrpg.game.SuperCalc;
import net.site40.rodit.tinyrpg.game.battle.InputBattleProvider;
import net.site40.rodit.tinyrpg.game.entity.MovementProvider.TileMovementProvider;
import net.site40.rodit.tinyrpg.game.gui.GuiIngameMenu;
import net.site40.rodit.util.TinyInputStream;
import net.site40.rodit.util.TinyOutputStream;

public class EntityPlayer extends EntityLiving{

	private String username;

	private boolean hasMoved;

	protected MovementProvider movement = new TileMovementProvider();

	public EntityPlayer(){
		super();
		this.name = "player";
		this.username = "New Player";
		this.script = "script/entity/player.js";
		this.resource = "character/m/base/white.spr";
		this.width = 8;
		this.height = 8;
		stats.setSpeed(2f);
		stats.setStrength(2f);
		stats.setLuck(2f);
		stats.setMagika(2f);
		stats.setLevel(1);
		this.setMaxHealth(SuperCalc.getMaxHealth(this));
		this.setHealth(maxHealth);
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

	public boolean hasMoved(){
		boolean lHasMoved = hasMoved;
		hasMoved = false;
		return lHasMoved;
	}

	public void teleport(Game game, float x, float y){
		this.x = x;
		this.y = y;
	}

	@Override
	public void load(Game game, TinyInputStream in)throws IOException{
		super.load(game, in);
		this.username = in.readString();
	}

	@Override
	public void save(TinyOutputStream out)throws IOException{
		super.save(out);
		out.writeString(username);
	}

	@Override
	public float getCenterX(){
		return x + 8f;
	}

	@Override
	public float getCenterY(){
		return y + 16f;
	}

	@Override
	public RectF getBounds(){
		return new RectF(x, y, x + 16f, y + 32f);
	}

	@Override
	public RectF getCollisionBounds(){
		return getCollisionBounds(x, y);
	}

	@Override
	public RectF getCollisionBounds(float x, float y){
		return new RectF(x + 2f, y + 2f + 18f, x + 14f, y + 30f);
	}

	public RectF getCollisionBoundsOld(float x, float y){
		return new RectF(x, y + height / 2f, x + width, y + height);
	}

	@Override
	public void update(Game game){		
		movement.move(game, this);

		Input input = game.getInput();
		if(input.allowMovement()){
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

	public void copy(EntityPlayer player){
		super.copy(player);
		this.username = player.username;
	}
}
