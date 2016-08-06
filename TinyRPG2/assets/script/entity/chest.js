function onSpawn(){
	if(game.getGlobalb("chest_opened_" + self.name))
		self.setResource("entity/chest/open.png");
	else
		self.setResource("entity/chest/closed.png");
}

function onAction(actor){
	if(!game.getGlobalb("chest_opened_" + self.name)){
		var loot = self.getRuntimeProperty("loot");
		var loot_count = util.tryGetInt(self.getRuntimeProperty("loot_count"), 1);
		var item = helper.getItem(loot);
		game.getPlayer().getInventory().add(item, loot_count);
		helper.dialog("You found " + item.getShowName() + " x " + loot_count + " in the chest.");
		game.setGlobalb("chest_opened_" + self.name, true);
		self.setResource("entity/chest/open.png");
	}
}

self.registerCallbacks(onSpawn, null, null, onAction);