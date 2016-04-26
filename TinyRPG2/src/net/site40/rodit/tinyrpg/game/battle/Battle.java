package net.site40.rodit.tinyrpg.game.battle;

import net.site40.rodit.tinyrpg.game.Game;
import net.site40.rodit.tinyrpg.game.GameObject;
import net.site40.rodit.tinyrpg.game.entity.EntityLiving;
import net.site40.rodit.tinyrpg.game.event.EventReceiver.EventType;
import net.site40.rodit.tinyrpg.game.map.Region;
import net.site40.rodit.tinyrpg.game.map.Region.RegionLocal;
import net.site40.rodit.tinyrpg.game.render.Animation;
import net.site40.rodit.tinyrpg.game.render.FlashMixer;
import net.site40.rodit.tinyrpg.game.render.SpriteSheet;
import net.site40.rodit.tinyrpg.game.util.Direction;
import net.site40.rodit.util.RenderUtil;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.RectF;
import android.util.Log;

public class Battle extends GameObject{

	public static final int COUNT_INDEX_ATTACK = 0;
	public static final int COUNT_INDEX_DEFENCE = 1;
	
	public static final FlashMixer hitMixer = new FlashMixer(100L, 1000L);
	
	private Direction oldAttackDir = Direction.D_DOWN;
	private Direction oldDefenceDir = Direction.D_DOWN;

	private Region region;
	private EntityLiving attack;
	private EntityLiving defence;

	private EntityLiving current;

	private int turnCountTotal = 0;

	private boolean decisionMade = false;
	private IBattleProvider decisionProvider = null;

	public Battle(Region region, EntityLiving attack, EntityLiving defence){
		this.region = region;
		this.attack = attack;
		this.oldAttackDir = attack.getDirection();
		attack.setDirection(Direction.D_LEFT);
		this.defence = defence;
		this.oldDefenceDir = defence.getDirection();
		defence.setDirection(Direction.D_RIGHT);
		this.current = first();
	}

	public Region getRegion(){
		return region;
	}

	public EntityLiving getAttack(){
		return attack;
	}

	public EntityLiving getDefence(){
		return defence;
	}

	public EntityLiving first(){
		return defence.getStats().getSpeed() > attack.getStats().getSpeed() ? defence : attack;
	}

	public EntityLiving next(){
		return current == attack ? defence : attack;
	}

	public EntityLiving current(){
		return current;
	}

	public void madeDecision(Game game, IBattleProvider provider){
		decisionMade = true;
		decisionProvider = provider;
		Log.i("Battle", "Decision made by " + decisionProvider.getEntity().getName());
	}

	private void win(Game game, EntityLiving winner, EntityLiving loser){
		if(winner.isPlayer()){
			long gold = loser.getMoney() / 2L;
			if(gold == 0)
				gold = 1;
			winner.addMoney(gold);
			loser.subtractMoney(gold);
			game.getHelper().dialog("Congratulations! You have won the battle!~You earned " + gold + " gold!");
		}else if(loser.isPlayer()){
			long gold = loser.getMoney() / 7L;
			winner.addMoney(gold);
			loser.subtractMoney(gold);
			game.getHelper().dialog("Oh no! You were killed in the battle!~You forfeited " + gold + " gold!");
		}
		game.setBattle(null);
		attack.setDirection(oldAttackDir);
		defence.setDirection(oldDefenceDir);
		
		game.getEvents().onEvent(game, EventType.BATTLE_END, this);
	}
	
	boolean first = true;
	@Override
	public void update(Game game){
		//if(turnCount[current == attack ? COUNT_INDEX_ATTACK : COUNT_INDEX_DEFENCE] == queryCount[current == attack ? COUNT_INDEX_ATTACK : COUNT_INDEX_DEFENCE]){
		//queryCount[current == attack ? COUNT_INDEX_ATTACK : COUNT_INDEX_DEFENCE]++;
		//current.getBattleProvider().makeDecision(game, this);
		//}
		
		if(first){
			game.getEvents().onEvent(game, EventType.BATTLE_START, this);
			first = false;
		}

		if(!decisionMade && decisionProvider == null && turnCountTotal == 0){
			Log.i("Battle", "Making initial decision.");
			current.getBattleProvider().makeDecision(game, this);
			turnCountTotal++;
		}

		if(decisionMade && decisionProvider != null){
			Log.i("Battle", "Switching turn.");
			decisionMade = false;
			decisionProvider = null;
			current = next();
			current.getBattleProvider().makeDecision(game, this);
			turnCountTotal++;
			Log.i("Battle", "Waiting for decision from " + current.getName() + ".");
		}

		if(attack.isDead())
			win(game, defence, attack);
		else if(defence.isDead())
			win(game, attack, defence);
	}

	private static final RectF BOUNDS_DEFENCE = new RectF(256, 256, 256 + 128, 256 + 128);
	private static final RectF BOUNDS_ATTACK = new RectF(512, 256, 512 + 128, 256 + 128);

	private Paint paint;
	@Override
	public void draw(Game game, Canvas canvas){
		super.preRender(game, canvas);
		
		if(paint == null){
			paint = Game.getDefaultPaint();
			paint.setTextSize(14f);
		}
		
		canvas.drawBitmap(game.getResources().getBitmap(RegionLocal.getResource(region)), null, new RectF(0, 0, 1280, 720), null);
		Object dObj = game.getResources().getObject(defence.getResource());
		defence.preRender(game, canvas);
		if(dObj instanceof Bitmap)
			canvas.drawBitmap((Bitmap)dObj, null, BOUNDS_DEFENCE, defence.getPaint());
		else if(dObj instanceof Animation)
			canvas.drawBitmap(((Animation)dObj).getFrame(game.getTime()), null, BOUNDS_DEFENCE, defence.getPaint());
		else if(dObj instanceof SpriteSheet)
			canvas.drawBitmap(((SpriteSheet)dObj).getBitmap(game, defence), null, BOUNDS_DEFENCE, defence.getPaint());
		defence.postRender(game, canvas);
		
		Object aObj = game.getResources().getObject(attack.getResource());
		attack.preRender(game, canvas);
		if(aObj instanceof Bitmap)
			canvas.drawBitmap((Bitmap)aObj, null, BOUNDS_ATTACK, attack.getPaint());
		else if(aObj instanceof Animation)
			canvas.drawBitmap(((Animation)aObj).getFrame(game.getTime()), null, BOUNDS_ATTACK, attack.getPaint());
		else if(aObj instanceof SpriteSheet)
			canvas.drawBitmap(((SpriteSheet)aObj).getBitmap(game, attack), null, BOUNDS_ATTACK, attack.getPaint());
		attack.postRender(game, canvas);
		
		float offsetX = 1280 - 256;
		float offsetY = 0;
		RenderUtil.drawBitmapBox(canvas, game, new RectF(offsetX, offsetY, offsetX + 256, offsetY + 128), paint);
		//ENEMY NAME
		paint.setColor(Color.BLACK);
		paint.setTextAlign(Align.LEFT);
		canvas.drawText(attack.getName(), offsetX + 24, offsetY + 32, paint);
		//ENEMY HEALTH
		int darkRed = Color.RED;
		int red = Color.GREEN;
		paint.setColor(darkRed);
		RectF healthBounds = new RectF(offsetX + 24, offsetY + 48, offsetX + 232, offsetY + 68);
		float hcx = healthBounds.centerX();
		float hcy = healthBounds.centerY() + 4;
		canvas.drawRect(healthBounds, paint);
		paint.setColor(red);
		float healthVal = attack.getHealthRatio() * healthBounds.width();
		healthBounds.right = healthBounds.left + healthVal;
		canvas.drawRect(healthBounds, paint);
		paint.setColor(Color.WHITE);
		paint.setTextAlign(Align.CENTER);
		canvas.drawText(attack.getHealth() + "/" + attack.getMaxHealth(), hcx, hcy, paint);
		//ENEMY MAGIKA
		int darkBlue = Color.BLUE;
		int blue = Color.CYAN;
		paint.setColor(darkBlue);
		RectF magikaBounds = new RectF(offsetX + 24, offsetY + 80, offsetX + 232, offsetY + 100);
		float mcx = magikaBounds.centerX();
		float mcy = magikaBounds.centerY() + 4;
		canvas.drawRect(magikaBounds, paint);
		paint.setColor(blue);
		float magikaVal = attack.getMagikaRatio() * magikaBounds.width();
		magikaBounds.right = magikaBounds.left + magikaVal;
		canvas.drawRect(magikaBounds, paint);
		paint.setColor(Color.WHITE);
		paint.setTextAlign(Align.CENTER);
		canvas.drawText((int)attack.getMagika() + "/" + (int)attack.getMaxMagika(), mcx, mcy, paint);
		
		super.postRender(game, canvas);
	}

	@Override
	public RenderLayer getRenderLayer(){
		return RenderLayer.TOP_ALL;
	}

	@Override
	public boolean shouldScale(){
		return false;
	}
}
