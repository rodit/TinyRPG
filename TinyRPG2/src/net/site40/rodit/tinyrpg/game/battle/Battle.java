package net.site40.rodit.tinyrpg.game.battle;

import net.site40.rodit.tinyrpg.game.Game;
import net.site40.rodit.tinyrpg.game.GameObject;
import net.site40.rodit.tinyrpg.game.battle.Team.Attribs;
import net.site40.rodit.tinyrpg.game.entity.EntityLiving;
import net.site40.rodit.tinyrpg.game.event.EventReceiver.EventType;
import net.site40.rodit.tinyrpg.game.map.Region;
import net.site40.rodit.tinyrpg.game.map.Region.RegionLocale;
import net.site40.rodit.tinyrpg.game.render.FlashMixer;
import net.site40.rodit.tinyrpg.game.util.Direction;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.util.Log;

public class Battle extends GameObject{

	public static final int COUNT_INDEX_ATTACK = 0;
	public static final int COUNT_INDEX_DEFENCE = 1;

	public static FlashMixer newHitMixer(){
		return new FlashMixer(100L, 1000L);
	}

	private Direction oldAttackDir = Direction.D_DOWN;
	private Direction oldDefenceDir = Direction.D_DOWN;

	private Region region;
	private Team attack;
	private Team defence;

	private Team current;
	private EntityLiving currentMember;

	private int turnCountTotal = 0;

	private boolean decisionMade = false;
	private IBattleProvider decisionProvider = null;

	public Battle(Region region, Team attack, Team defence){
		this.region = region;
		this.attack = attack;
		this.oldAttackDir = attack.getMembers().get(0).getDirection();
		attack.getMembers().get(0).setDirection(Direction.D_LEFT);
		this.defence = defence;
		this.oldDefenceDir = defence.getMembers().get(0).getDirection();
		defence.getMembers().get(0).setDirection(Direction.D_RIGHT);
		this.current = first();
		this.currentMember = current.getNext();

		attack.setAttribute(Attribs.TEAM_ATTACK, true);
		defence.setAttribute(Attribs.TEAM_DEFENCE, true);
	}

	public Region getRegion(){
		return region;
	}

	public Team getAttack(){
		return attack;
	}

	public Team getDefence(){
		return defence;
	}

	public Team first(){
		return defence.getSpeed() > attack.getSpeed() ? defence : attack;
	}

	public Team next(){
		return current.hasExhaustedMembers() ? (current == attack ? defence : attack) : current;
	}

	public EntityLiving nextMember(){
		return next().getNext();
	}

	public Team current(){
		return current;
	}

	public EntityLiving currentMember(){
		return currentMember;
	}

	public Team getOpposition(EntityLiving member){
		return attack.getMembers().contains(member) ? defence : attack;
	}

	public void madeDecision(Game game, IBattleProvider provider){
		decisionMade = true;
		decisionProvider = provider;
		Log.i("Battle", "Decision made by " + decisionProvider.getEntity().getName());
	}

	private void win(Game game, Team winner, Team loser){
		if(winner.getLeader().isPlayer()){
			long gold = loser.getLeader().getMoney() / 2L;
			if(gold == 0)
				gold = 1;
			winner.getLeader().addMoney(gold);
			loser.getLeader().subtractMoney(gold);
			game.getHelper().dialog("Congratulations! You have won the battle!~You earned " + gold + " gold!");
		}else if(loser.getLeader().isPlayer()){
			long gold = loser.getLeader().getMoney() / 7L;
			winner.getLeader().addMoney(gold);
			loser.getLeader().subtractMoney(gold);
			game.getHelper().dialog("Oh no! You were killed in the battle!~You forfeited " + gold + " gold!");
		}
		game.setBattle(null);
		attack.getMembers().get(0).setDirection(oldAttackDir);
		defence.getMembers().get(0).setDirection(oldDefenceDir);

		game.getEvents().onEvent(game, EventType.BATTLE_END, this);
	}

	boolean first = true;
	@Override
	public void update(Game game){
		if(first){
			game.getEvents().onEvent(game, EventType.BATTLE_START, this);
			first = false;
		}

		if(!decisionMade && decisionProvider == null && turnCountTotal == 0){
			Log.i("Battle", "Making initial decision.");
			currentMember.getBattleProvider().makeDecision(game, this);
			turnCountTotal++;
		}

		if(decisionMade && decisionProvider != null){
			Log.i("Battle", "Switching turn.");
			decisionMade = false;
			decisionProvider = null;
			current = next();
			currentMember = nextMember();
			currentMember.getBattleProvider().makeDecision(game, this);
			turnCountTotal++;
			Log.i("Battle", "Waiting for decision from " + currentMember.getName() + ".");
		}

		if(attack.isDefeated())
			win(game, defence, attack);
		else if(defence.isDefeated())
			win(game, attack, defence);
	}
	
	private static final RectF BOUNDS_DEFENCE[] = new RectF[] {
		new RectF(256, 256, 256 + 64, 256 + 64),
		new RectF(256, 384, 256 + 64, 384 + 64),
		new RectF(256, 128, 256 + 64, 128 + 64),
		new RectF(256, 512, 256 + 64, 512 + 64)
	};
	private static final RectF BOUNDS_ATTACK[] = new RectF[] {
		new RectF(1280 - 256 - 64, 256, 1280 - 256, 256 + 64),
		new RectF(1280 - 256 - 64, 384, 1280 - 256, 384 + 64),
		new RectF(1280 - 256 - 64, 128, 1280 - 256, 128 + 64),
		new RectF(1280 - 256 - 64, 512, 1280 - 256, 512 + 64)
	};

	private Paint paint;
	@Override
	public void draw(Game game, Canvas canvas){
		super.preRender(game, canvas);

		if(paint == null){
			paint = Game.getDefaultPaint();
			paint.setTextSize(14f);
		}

		canvas.drawBitmap(game.getResources().getBitmap(RegionLocale.getResource(region)), null, new RectF(0, 0, 1280, 720), null);

		for(int i = 0; i < defence.getMembers().size(); i++){
			EntityLiving ent = defence.getMembers().get(i);
			float ox = ent.getX();
			float oy = ent.getY();
			float ow = ent.getWidth();
			float oh = ent.getHeight();
			RectF bounds = BOUNDS_DEFENCE[i];
			ent.setX(bounds.left);
			ent.setY(bounds.top);
			ent.setWidth(bounds.width());
			ent.setHeight(bounds.height());
			ent.draw(game, canvas);
			ent.setX(ox);
			ent.setY(oy);
			ent.setWidth(ow);
			ent.setHeight(oh);
			paint.setColor(Color.RED);
			paint.setStyle(Style.FILL);
			canvas.drawRect(new RectF(bounds.left - 16, bounds.top - 32, bounds.right + 16, bounds.top - 16), paint);
			paint.setColor(Color.GREEN);
			canvas.drawRect(new RectF(bounds.left - 16, bounds.top - 32, bounds.left - 16 + (bounds.width() + 16) * ent.getHealthRatio(), bounds.top - 16), paint);
		}
		
		for(int i = 0; i < attack.getMembers().size(); i++){
			EntityLiving ent = attack.getMembers().get(i);
			float ox = ent.getX();
			float oy = ent.getY();
			float ow = ent.getWidth();
			float oh = ent.getHeight();
			RectF bounds = BOUNDS_ATTACK[i];
			ent.setX(bounds.left);
			ent.setY(bounds.top);
			ent.setWidth(bounds.width());
			ent.setHeight(bounds.height());
			ent.draw(game, canvas);
			ent.setX(ox);
			ent.setY(oy);
			ent.setWidth(ow);
			ent.setHeight(oh);
			paint.setColor(Color.RED);
			paint.setStyle(Style.FILL);
			canvas.drawRect(new RectF(bounds.left - 16, bounds.top - 32, bounds.right + 16, bounds.top - 16), paint);
			paint.setColor(Color.GREEN);
			canvas.drawRect(new RectF(bounds.left - 16, bounds.top - 32, bounds.left - 16 + (bounds.width() + 16) * ent.getHealthRatio(), bounds.top - 16), paint);
		}

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
