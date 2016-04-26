package net.site40.rodit.tinyrpg.game.entity;

import java.io.IOException;

import net.site40.rodit.tinyrpg.game.Game;
import net.site40.rodit.tinyrpg.game.Input;
import net.site40.rodit.tinyrpg.game.SuperCalc;
import net.site40.rodit.tinyrpg.game.battle.InputBattleProvider;
import net.site40.rodit.tinyrpg.game.gui.GuiIngameMenu;
import net.site40.rodit.util.TinyInputStream;
import net.site40.rodit.util.TinyOutputStream;
import android.graphics.Canvas;
import android.graphics.RectF;

public class EntityPlayer extends EntityLiving{
	
	public static final float SPEED_MULTI = 1f;
	
	private String username;
	
	public EntityPlayer(){
		super();
		this.name = "player";
		this.username = "New Player";
		this.script = "script/entity/player.js";
		this.resource = "entity/default.spr";
		this.width = 24;
		this.height = 24;
		stats.setSpeed(2f);
		stats.setStrength(2f);
		stats.setLuck(2f);
		stats.setMagika(2f);
		stats.setLevel(1);
		this.setMaxHealth(SuperCalc.getMaxHealth(this));
		this.setHealth(maxHealth);
		this.setMagika(SuperCalc.getMaxMagika(this));
		this.battleProvider = new InputBattleProvider(this);
		
		setRuntimeProperty("map_origin", "respawn");
		
		//TODO: REMOVE
		//this.setNoclip(true);
	}
	
	public String getUsername(){
		return username;
	}
	
	public void setUsername(String username){
		this.username = username;
	}
	
	public void teleport(Game game, float x, float y){
		this.x = x;
		this.y = y;
	}
	
	@Override
	public RectF getCollisionBounds(){
		return new RectF(x, y + height / 2f, x + width, y + height);
	}
	
	@Override
	public RectF getCollisionBounds(float x, float y){
		return new RectF(x, y + height / 2f, x + width, y + height);
	}
	
	@Override
	public void update(Game game){
		float deltaMulti = (float)game.getDelta() / 16.6f;
		Input input = game.getInput();
		if(input.allowMovement()){
			if(input.isDown(Input.KEY_UP))
				velocityY -= stats.getMoveSpeed() * SPEED_MULTI * deltaMulti;
			if(input.isDown(Input.KEY_DOWN))
				velocityY += stats.getMoveSpeed() * SPEED_MULTI * deltaMulti;
			if(input.isDown(Input.KEY_LEFT))
				velocityX -= stats.getMoveSpeed() * SPEED_MULTI * deltaMulti;
			if(input.isDown(Input.KEY_RIGHT))
				velocityX += stats.getMoveSpeed() * SPEED_MULTI * deltaMulti;
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
		
		super.update(game);
	}
	
	@Override
	public void draw(Game game, Canvas canvas){
		//game.pushTranslate(canvas);
		super.draw(game, canvas);
		//game.popTranslate(canvas);
	}
	
	@Override
	public void serialize(TinyOutputStream out)throws IOException{
		super.serialize(out);
		out.writeString(username);
	}
	
	@Override
	public void deserialize(TinyInputStream in)throws IOException{
		super.deserialize(in);
		username = in.readString();
	}
}
