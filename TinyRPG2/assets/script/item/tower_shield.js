function onEquip(user){
	if(user.stats.getStrength() < 3.8){
		helper.dialog("You do not have enough strength to wield this weapon effectively.");
		user.stats.addSpeedMulti(-0.25);
	}
}

function onUnEquip(user){
	if(user.stats.getStrength() < 3.8){
		helper.dialog("You suddenly feel much lighter...");
		user.stats.addSpeedMulti(0.25);
	}
}

function onHit(sender, receiver){
	if(user.stats.getStrength() < 3.8 && helper.should(35) && user.stats.getSpeedMulti() > 0.4){
		helper.dialog("Your tower shield is too heavy for you to hold and you fall back on to the floor.~Your speed has decreased by 5%.");
		user.stats.addSpeedMulti(-0.05);
		user.setRuntimeProperty("tower_shield_speed_decrease", util.tryGetFloat(user.getRuntimeProperty("tower_sheild_speed_decrease"), 0) + 0.05);
		helper.scheduleEvent("event_towershield_end_battle", "BATTLE_END", onBattleEnd);
	}
}

function onBattleEnd(battle){
	var ent = battle.getAttacker();
	var amount = util.tryGetFloat(ent.getRuntimeProperty("tower_shield_speed_decrease"), 0);
	ent.stats.addSpeedMulti(amount);
	ent.setRuntimeProperty("tower_shield_speed_decrease", "0");
	ent = battle.getDefender();
	amount = util.tryGetFloat(ent.getRuntimeProperty("tower_shield_speed_decrease"), 0);
	ent.stats.addSpeedMulti(amount);
	ent.setRuntimeProperty("tower_shield_speed_decrease", "0");
}

self.registerCallbacks(onEquip, onUnEquip, onHit);
