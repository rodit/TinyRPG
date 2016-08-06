function onSpawn(){
	if(game.getGlobalb("locked_chest_opened_" + self.name))
		self.setResource("entity/chest/open_key.png");
	else
		self.setResource("entity/chest/closed_key.png");
}

function onAction(actor){
	if(!game.getGlobalb("locked_chest_opened_" + self.name)){
		var keyItem = helper.getItem(self.getRuntimeProperty("key"));
		if(actor.getInventory().hasItem(keyItem)){
			var loot = self.getRuntimeProperty("loot");
			var loot_count = util.tryGetInt(self.getRuntimeProperty("loot_count"), 1);
			var item = helper.getItem(loot);
			game.getPlayer().getInventory().add(item, loot_count);
			helper.dialog("You used the " + keyItem.getShowName() + " to unlock the chest.~You found " + item.getShowName() + " x " + loot_count + " in the chest.");
			game.setGlobalb("locked_chest_opened_" + self.name, true);
			self.setResource("entity/chest/open_key.png");
		}else
			helper.dialog("The chest is locked.");
	}
}

self.registerCallbacks(onSpawn, null, null, onAction);