package net.site40.rodit.tinyrpg.game.battle;

import java.util.ArrayList;

import net.site40.rodit.tinyrpg.game.Dialog.DialogCallback;
import net.site40.rodit.tinyrpg.game.Game;
import net.site40.rodit.tinyrpg.game.SuperCalc;
import net.site40.rodit.tinyrpg.game.entity.EntityLiving;
import net.site40.rodit.tinyrpg.game.item.Item;
import net.site40.rodit.tinyrpg.game.item.Weapon;
import android.util.Log;

public class AIBattleProvider implements IBattleProvider{
	
	private Game game;
	private Battle battle;
	
	private AIInformationCallback callback = new AIInformationCallback();
	
	private EntityLiving owner;
	
	public AIBattleProvider(EntityLiving owner){
		this.owner = owner;
	}
	
	public void makeDecision(Game game, Battle battle){
		this.game = game;
		this.battle = battle;
		game.getHelper().dialog("AI battle provider option chosen...", new String[0], callback);
		Log.i("AIBattleProvider", "AI battle provider option chosen...");
		ArrayList<EntityLiving> opposition = battle.getOpposition(owner).getMembers();
		int targetIndex = opposition.size() == 1 ? 0 : game.getRandom().nextInt(1, opposition.size()) - 1;
		EntityLiving target = opposition.get(targetIndex);
		SuperCalc.attack(game, owner, target, (Weapon)Item.get("dagger_steel_1"));
		target.attachPaintMixer(Battle.newHitMixer());
	}
	
	@Override
	public EntityLiving getEntity(){
		return owner;
	}
	
	public class AIInformationCallback implements DialogCallback{
		
		@Override
		public void onSelected(int item){
			battle.madeDecision(game, AIBattleProvider.this);
		}
	}
}
