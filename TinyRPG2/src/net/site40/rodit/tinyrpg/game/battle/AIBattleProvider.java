package net.site40.rodit.tinyrpg.game.battle;

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
		SuperCalc.attack(owner, battle.next(), (Weapon)Item.get("dagger_steel"));
		battle.next().attachPaintMixer(Battle.hitMixer);
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
