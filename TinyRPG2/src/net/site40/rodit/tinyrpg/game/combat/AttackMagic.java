package net.site40.rodit.tinyrpg.game.combat;

import net.site40.rodit.tinyrpg.game.Game;
import net.site40.rodit.tinyrpg.game.entity.EntityLiving;

import static net.site40.rodit.tinyrpg.game.render.Strings.DIALOG_NO_MAGIKA_SP;

import org.w3c.dom.Element;

import android.text.TextUtils;
import android.util.Log;

public class AttackMagic extends Attack{
	
	private int magikaUsage;

	public AttackMagic(){
		super();
	}
	
	public AttackMagic(String name, String showName, String resource, String script, float damage, int magikaUsage){
		super(name, showName, resource, script, damage);
		this.magikaUsage = magikaUsage;
	}
	
	public float getMagikaUsage(){
		return magikaUsage;
	}
	
	public void setMagikaUsage(int magikaUsage){
		this.magikaUsage = magikaUsage;
	}
	
	@Override
	public void onUse(Game game, EntityLiving user, EntityLiving target){
		if(user.getMagika() >= magikaUsage){
			user.useMagika(magikaUsage);
			super.onUse(game, user, target);
		}else
			if(user.isPlayer())
				game.getHelper().dialog(DIALOG_NO_MAGIKA_SP);
			else
				Log.w("AttackMagic", "Non-player entity attempted to use magic attack without sufficient magika.");
	}
	
	@Override
	public void deserializeXmlElement(Element e){
		super.deserializeXmlElement(e);
		String magikaStr = e.getAttribute("magikaUsage");
		if(!TextUtils.isEmpty(magikaStr))
			magikaUsage = Integer.valueOf(magikaStr);
	}
}
