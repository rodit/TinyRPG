package net.site40.rodit.tinyrpg.game.battle;

import static net.site40.rodit.tinyrpg.game.render.Strings.getString;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import net.site40.rodit.tinyrpg.game.Game;
import net.site40.rodit.tinyrpg.game.SuperCalc;
import net.site40.rodit.tinyrpg.game.Values;
import net.site40.rodit.tinyrpg.game.entity.EntityLiving;
import net.site40.rodit.tinyrpg.game.entity.EntityPlayer;
import net.site40.rodit.tinyrpg.game.event.EventReceiver.EventType;
import net.site40.rodit.tinyrpg.game.map.Region;
import net.site40.rodit.tinyrpg.game.map.Region.RegionLocale;
import net.site40.rodit.tinyrpg.game.object.GameObject;
import net.site40.rodit.tinyrpg.game.render.FlashMixer;
import net.site40.rodit.tinyrpg.game.render.SpriteSheet.MovementState;
import net.site40.rodit.tinyrpg.game.render.Strings;
import net.site40.rodit.tinyrpg.game.util.Direction;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.graphics.RectF;

public class Battle extends GameObject{

	public static Comparator<EntityLiving> speedComparator = new Comparator<EntityLiving>(){
		@Override
		public int compare(EntityLiving e0, EntityLiving e1){
			return (int)(e1.getStats().getSpeed() * 100000f - e0.getStats().getSpeed() * 100000f);
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
	
	private boolean started = false;

	public Battle(Region region, Team attack, Team defence){
		setBounds(0, 0, 1280, 720);
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
		
		this.paint = null;
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

	private long reward = 0l;
	private boolean uIsThief = false;
	protected void win(Game game, Team winner){
		Team loser = getOpposition(winner);
		if(winner.getLeader().isPlayer()){
			reward = loser.getLeader().getMoney() / 5l;
			if(reward == 0)
				reward = 1;
			uIsThief = ((EntityPlayer)winner.getLeader()).getStartClass().equals("thief");
			if(uIsThief)
				reward *= SuperCalc.getThiefGoldBonusMulti(winner.getLeader().getStats());
			winner.getLeader().addMoney(reward);
			loser.getLeader().subtractMoney(reward);
			game.getHelper().dialog(getString(Strings.Dialog.BATTLE_WIN, reward));
		}else if(loser.getLeader().isPlayer()){
			reward = loser.getLeader().getMoney() / 7l;
			uIsThief = ((EntityPlayer)loser.getLeader()).getStartClass().equals("thief");
			if(uIsThief)
				reward /= 2l;
			winner.getLeader().addMoney(reward);
			loser.getLeader().subtractMoney(reward);
			game.getHelper().dialog(getString(Strings.Dialog.BATTLE_LOSE, reward));
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

	public ArrayList<EntityLiving> getOppositionMembersDrawOrdered(EntityLiving ent){
		return getOppositionMembersDrawOrdered(getTeam(ent));
	}

	public ArrayList<EntityLiving> getOppositionMembersDrawOrdered(Team current){
		ArrayList<EntityLiving> members = getOppositionMembersOrdered(current);
		return getDrawOrdered(members, false);
	}

	public ArrayList<EntityLiving> getDrawOrdered(ArrayList<EntityLiving> members, boolean alive){
		ArrayList<EntityLiving> ordered = new ArrayList<EntityLiving>();
		if(members.size() == 4){
			if(!members.get(2).isDead() || !alive)
				ordered.add(members.get(2));
			if(!members.get(0).isDead() || !alive)
				ordered.add(members.get(0));
			if(!members.get(1).isDead() || !alive)
				ordered.add(members.get(1));
			if(!members.get(3).isDead() || !alive)
				ordered.add(members.get(3));
		}else if(members.size() == 3){
			if(!members.get(2).isDead() || !alive)
				ordered.add(members.get(2));
			if(!members.get(0).isDead() || !alive)
				ordered.add(members.get(0));
			if(!members.get(1).isDead() || !alive)
				ordered.add(members.get(1));
		}else if(members.size() == 2){
			if(!members.get(0).isDead() || !alive)
				ordered.add(members.get(0));
			if(!members.get(1).isDead() || !alive)
				ordered.add(members.get(1));
		}else if(members.size() == 1){
			if(!members.get(0).isDead() || !alive)
				ordered.add(members.get(0));
		}
		return ordered;
	}

	public ArrayList<EntityLiving> getOppositionMembersDrawOrderedAlive(EntityLiving ent){
		return getOppositionMembersDrawOrderedAlive(getTeam(ent));
	}

	public ArrayList<EntityLiving> getOppositionMembersDrawOrderedAlive(Team current){
		return getDrawOrdered(getOppositionMembersOrdered(current), true);
	}

	public ArrayList<EntityLiving> getOppositionMembersOrdered(EntityLiving ent){
		return getOppositionMembersOrdered(getTeam(ent));
	}

	public ArrayList<EntityLiving> getOppositionMembersOrdered(Team current){
		return current == attack ? defenceMembers : attackMembers;
	}

	public ArrayList<EntityLiving> getOppositionMembersOrderedAlive(Team current){
		ArrayList<EntityLiving> alive = new ArrayList<EntityLiving>();
		for(EntityLiving ent : getOppositionMembersOrdered(current))
			if(!ent.isDead())
				alive.add(ent);
		return alive;
	}

	public void start(){
		this.started = true;
	}
	
	private boolean firstUpdate = true;
	private int uCIndex;
	private int uMaxIndex;
	private IBattleProvider uBattleProvider;
	@Override
	public void update(Game game){
		if(!started)
			return;
		
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
			uCIndex = memberIndexes[turn];
			uMaxIndex = getMembers(turn).size() - 1;
			if(uCIndex >= uMaxIndex){
				memberIndexes[turn] = 0;
				turn = turn == ATTACK ? DEFENCE : ATTACK;
			}else
				memberIndexes[turn]++;
			turnTaken = false;
		}

		uBattleProvider = getMembers(turn).get(memberIndexes[turn]).getBattleProvider();
		if(uBattleProvider == null)
			turnTaken = true;
		else if(!deciding){
			deciding = true;
			if(uBattleProvider.getEntity().isDead())
				madeDecision(game, uBattleProvider);
			else
				uBattleProvider.makeDecision(game, this);
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
	
	private Direction uDirection;
	private MovementState uMoveState;
	@Override
	public void draw(Game game, Canvas canvas){
		super.preRender(game, canvas);

		if(paint == null){
			paint = Game.getDefaultPaint();
			paint.setTextSize(14f);
			resourceCache.setResource(resource = RegionLocale.getResource(region));
		}
		
		resourceCache.draw(game, canvas, this);
		
		for(int i = 0; i < defenceMembers.size(); i++){
			EntityLiving ent = defenceMembers.get(i);
			if(ent.isDead())
				continue;
			bounds.getPooled0().set(ent.getBounds().get());
			uDirection = ent.getDirection();
			uMoveState = ent.getMoveState();
			bounds.getPooled1().set(BOUNDS_DEFENCE[i]);
			ent.getBounds().set(bounds.getPooled1());
			ent.setDirection(Direction.D_RIGHT);
			ent.setMoveState(MovementState.IDLE);
			ent.invalidate();
			ent.draw(game, canvas);
			ent.getBounds().set(bounds.getPooled0());
			ent.setDirection(uDirection);
			ent.setMoveState(uMoveState);
			paint.setColor(Color.RED);
			paint.setStyle(Style.FILL);
			bounds.getPooled0().set(bounds.getPooled1().left - 16, bounds.getPooled1().top - 32, bounds.getPooled1().right + 16, bounds.getPooled1().top - 16);
			canvas.drawRect(bounds.getPooled0(), paint);
			paint.setColor(Color.GREEN);
			bounds.getPooled0().set(bounds.getPooled1().left - 16, bounds.getPooled1().top - 32, bounds.getPooled1().left - 16 + (bounds.getPooled1().width() + 32) * ent.getHealthRatio(), bounds.getPooled1().top - 16);
			canvas.drawRect(bounds.getPooled0(), paint);
			paint.setColor(Color.WHITE);
			paint.setTextSize(Values.FONT_SIZE_TINY);
			paint.setTextAlign(Align.CENTER);
			canvas.drawText(ent.getHealth() + "/" + ent.getMaxHealth(), bounds.getPooled1().centerX(), bounds.getPooled1().top - 48f, paint);
		}
		
		for(int i = 0; i < attackMembers.size(); i++){
			EntityLiving ent = attackMembers.get(i);
			if(ent.isDead())
				continue;
			bounds.getPooled0().set(ent.getBounds().get());
			uDirection = ent.getDirection();
			uMoveState = ent.getMoveState();
			bounds.getPooled1().set(BOUNDS_ATTACK[i]);
			ent.getBounds().set(bounds.getPooled1());
			ent.setDirection(Direction.D_LEFT);
			ent.setMoveState(MovementState.IDLE);
			ent.invalidate();
			ent.draw(game, canvas);
			ent.getBounds().set(bounds.getPooled0());
			ent.setDirection(uDirection);
			ent.setMoveState(uMoveState);
			paint.setColor(Color.RED);
			paint.setStyle(Style.FILL);
			bounds.getPooled0().set(bounds.getPooled1().left - 16, bounds.getPooled1().top - 32, bounds.getPooled1().right + 16, bounds.getPooled1().top - 16);
			canvas.drawRect(bounds.getPooled0(), paint);
			paint.setColor(Color.GREEN);
			bounds.getPooled0().set(bounds.getPooled1().left - 16, bounds.getPooled1().top - 32, bounds.getPooled1().left - 16 + (bounds.getPooled1().width() + 32) * ent.getHealthRatio(), bounds.getPooled1().top - 16);
			canvas.drawRect(bounds.getPooled0(), paint);
			paint.setColor(Color.WHITE);
			paint.setTextSize(Values.FONT_SIZE_TINY);
			paint.setTextAlign(Align.CENTER);
			canvas.drawText(ent.getHealth() + "/" + ent.getMaxHealth(), bounds.getPooled1().centerX(), bounds.getPooled1().top - 48f, paint);
		}
		
		super.postRender(game, canvas);
	}
	
	@Override
	public int getRenderLayer(){
		return RenderLayer.TOP_OVER_ALL;
	}

	@Override
	public boolean shouldScale(){
		return false;
	}
}
