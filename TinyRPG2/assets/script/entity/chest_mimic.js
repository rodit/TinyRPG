function onSpawn(){
	if(game.getGlobalb("chest_mimic_opened_" + self.name))
		self.setResource("entity/chest/open_mimic.png");
	else
		self.setResource("entity/chest/closed_mimic.png");
}

function onAction(actor){
	if(!game.getGlobalb("chest_mimic_opened_" + self.name)){
		var loot = self.getRuntimeProperty("loot");
		var loot_count = util.tryGetInt(self.getRuntimeProperty("loot_count"), 1);
		var mimic_entity = helper.createEntity("config/entity/mimic.xml");
		game.setGlobal("current_mimic_loot", loot);
		game.setGlobali("current_mimic_loot_count", loot_count);
		game.setGlobal("current_mimic_ent", self);
		helper.registerEvent("mimic_battle_receiver", "BATTLE_END", onBattleComplete);
		helper.battle("grass", mimic_entity, game.getPlayer());
	}
}

function onBattleComplete(battle, winners){
	var winning = winners.getMembers().get(0);
	if(winning != game.getPlayer())
		return;
	var mimic_entity = battle.getMembers(0).get(0);
	var loot = helper.getItem(game.getGlobal("current_mimic_loot"));
	var loot_count = game.getGlobali("current_mimic_loot_count");
	game.getPlayer().getInventory().add(loot, loot_count);
	helper.dialog("You looted " + loot.getShowName() + " x " + loot_count + " from the mimic.");
	var mimic_spawn_entity = game.getGlobal("current_mimic_ent");
	game.setGlobalb("chest_mimic_opened_" + mimic_spawn_entity.name, true);
	mimic_spawn_entity.setResource("entity/chest/open_mimic.png");
}

self.registerCallbacks(onSpawn, null, null, onAction);
