package net.site40.rodit.tinyrpg.game.battle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import net.site40.rodit.tinyrpg.game.Game;
import net.site40.rodit.tinyrpg.game.GameObject;
import net.site40.rodit.tinyrpg.game.entity.EntityLiving;
import net.site40.rodit.tinyrpg.game.event.EventReceiver.EventType;
import net.site40.rodit.tinyrpg.game.map.Region;
import net.site40.rodit.tinyrpg.game.map.Region.RegionLocale;
import net.site40.rodit.tinyrpg.game.render.FlashMixer;
import net.site40.rodit.tinyrpg.game.render.SpriteSheet.MovementState;
import net.site40.rodit.tinyrpg.game.util.Direction;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint.Style;
import android.graphics.RectF;

public class Battle extends GameObject{
	
	public static Comparator<EntityLiving> speedComparator = new Comparator<EntityLiving>(){
		@Override
		public int compare(EntityLiving e0, EntityLiving e1){
			return (int)(e1.getStats().getSpeed() - e0.getStats().getSpeed() * 100000);
		}
	};
	
	public static FlashMixer newHitMixer(){
		return new FlashMixer(100L, 1000L);
	}
	
	static final int ATTACK = 0;
	static final int DEFENCE = 1;
	
	private Region region;
	private Team attack;
	private Team defence;
	
	private ArrayList<EntityLiving> attackMembers;
	private ArrayList<EntityLiving> defenceMembers;
	
	private int turn;
	private boolean turnTaken;
	private boolean deciding;
	private boolean ended;
	
	private int[] memberIndexes;
	
	public Battle(Region region, Team attack, Team defence){
		this.region = region;
		this.attack = attack;
		this.defence = defence;
		
		this.attackMembers = new ArrayList<EntityLiving>(attack.getMembers());
		Collections.sort(attackMembers, speedComparator);
		this.defenceMembers = new ArrayList<EntityLiving>(defence.getMembers());
		Collections.sort(defenceMembers, speedComparator);
		
		this.turn = getStartingTurn();
		this.turnTaken = false;
		
		this.memberIndexes = new int[2];
	}
	
	public int getStartingTurn(){
		return attack.getSpeed() >= defence.getSpeed() ? ATTACK : DEFENCE;
	}
	
	public ArrayList<EntityLiving> getMembers(int team){
		return team == ATTACK ? attackMembers : defenceMembers;
	}

	public void madeDecision(Game game, IBattleProvider provider){
		turnTaken = true;
		deciding = false;
	}
	
	protected void win(Game game, Team winner){
		Team loser = getOpposition(winner);
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
		game.getEvents().onEvent(game, EventType.BATTLE_END, this, winner);
		ended = true;
	}
	
	public Team getTeam(EntityLiving ent){
		return attackMembers.contains(ent) ? attack : defence;
	}
	
	public Team getOpposition(EntityLiving ent){
		return getOpposition(getTeam(ent));
	}
	
	public Team getOpposition(Team current){
		return current == attack ? defence : attack;
	}
	
	public ArrayList<EntityLiving> getOppositionMembersOrdered(EntityLiving ent){
		return getOppositionMembersOrdered(getTeam(ent));
	}
	
	public ArrayList<EntityLiving> getOppositionMembersOrdered(Team current){
		return current == attack ? defenceMembers : attackMembers;
	}
	
	private boolean firstUpdate = true;
	@Override
	public void update(Game game){
		if(firstUpdate){
			game.getEvents().onEvent(game, EventType.BATTLE_START, this);
			firstUpdate = false;
		}
		
		if(attack.isDefeated())
			win(game, defence);
		else if(defence.isDefeated())
			win(game, attack);
		
		if(ended){
			game.setBattle(null);
			return;
		}
		
		if(turnTaken){
			int cIndex = memberIndexes[turn];
			int maxIndex = getMembers(turn).size() - 1;
			if(cIndex >= maxIndex){
				memberIndexes[turn] = 0;
				turn = turn == ATTACK ? DEFENCE : ATTACK;
			}else
				memberIndexes[turn]++;
			turnTaken = false;
		}
		
		IBattleProvider provider = getMembers(turn).get(memberIndexes[turn]).getBattleProvider();
		if(provider == null)
			turnTaken = true;
		else if(!deciding){
			deciding = true;
			provider.makeDecision(game, this);
		}
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
			Direction od = ent.getDirection();
			MovementState oms = ent.getMoveState();
			RectF bounds = BOUNDS_DEFENCE[i];
			ent.setX(bounds.left);
			ent.setY(bounds.top);
			ent.setWidth(bounds.width());
			ent.setHeight(bounds.height());
			ent.setDirection(Direction.D_RIGHT);
			ent.setMoveState(MovementState.IDLE);
			ent.draw(game, canvas);
			ent.setX(ox);
			ent.setY(oy);
			ent.setWidth(ow);
			ent.setHeight(oh);
			ent.setDirection(od);
			ent.setMoveState(oms);
			paint.setColor(Color.RED);
			paint.setStyle(Style.FILL);
			canvas.drawRect(new RectF(bounds.left - 16, bounds.top - 32, bounds.right + 16, bounds.top - 16), paint);
			paint.setColor(Color.GREEN);
			canvas.drawRect(new RectF(bounds.left - 16, bounds.top - 32, bounds.left - 16 + (bounds.width() + 32) * ent.getHealthRatio(), bounds.top - 16), paint);
		}
		
		for(int i = 0; i < attack.getMembers().size(); i++){
			EntityLiving ent = attack.getMembers().get(i);
			float ox = ent.getX();
			float oy = ent.getY();
			float ow = ent.getWidth();
			float oh = ent.getHeight();
			Direction od = ent.getDirection();
			MovementState oms = ent.getMoveState();
			RectF bounds = BOUNDS_ATTACK[i];
			ent.setX(bounds.left);
			ent.setY(bounds.top);
			ent.setWidth(bounds.width());
			ent.setHeight(bounds.height());
			ent.setDirection(Direction.D_LEFT);
			ent.setMoveState(MovementState.IDLE);
			ent.draw(game, canvas);
			ent.setX(ox);
			ent.setY(oy);
			ent.setWidth(ow);
			ent.setHeight(oh);
			ent.setDirection(od);
			ent.setMoveState(oms);
			paint.setColor(Color.RED);
			paint.setStyle(Style.FILL);
			canvas.drawRect(new RectF(bounds.left - 16, bounds.top - 32, bounds.right + 16, bounds.top - 16), paint);
			paint.setColor(Color.GREEN);
			canvas.drawRect(new RectF(bounds.left - 16, bounds.top - 32, bounds.left - 16 + (bounds.width() + 32) * ent.getHealthRatio(), bounds.top - 16), paint);
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
