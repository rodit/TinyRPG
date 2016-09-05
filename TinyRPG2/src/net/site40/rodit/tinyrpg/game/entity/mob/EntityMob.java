package net.site40.rodit.tinyrpg.game.entity.mob;

import java.io.IOException;

import net.site40.rodit.tinyrpg.game.Game;
import net.site40.rodit.tinyrpg.game.battle.AIBattleProvider;
import net.site40.rodit.tinyrpg.game.battle.AIBattleProvider.AIDifficulty;
import net.site40.rodit.tinyrpg.game.entity.npc.EntityAI;
import net.site40.rodit.util.TinyInputStream;
import net.site40.rodit.util.TinyOutputStream;
import net.site40.rodit.util.Util;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class EntityMob extends EntityAI{

	private AIDifficulty difficulty;

	public EntityMob(){
		super();

		this.battleProvider = new AIBattleProvider(this);
	}

	public AIDifficulty getDifficulty(){
		return difficulty;	
	}

	public void setDifficulty(AIDifficulty difficulty){
		this.difficulty = difficulty;
	}

	@Override
	public void linkConfig(Document document){
		super.linkConfig(document);
		Element root = (Element)document.getElementsByTagName("entity").item(0);
		this.difficulty = Util.tryGetAIDifficulty(root.getAttribute("difficulty"));
		if(battleProvider instanceof AIBattleProvider)
			((AIBattleProvider)battleProvider).setDifficulty(difficulty);
	}

	@Override
	public void load(Game game, TinyInputStream in)throws IOException{
		super.load(game, in);
		this.difficulty = Util.tryGetAIDifficulty(in.readString());
	}

	@Override
	public void save(TinyOutputStream out)throws IOException{
		super.save(out);
		out.writeString(difficulty.toString());
	}
}
