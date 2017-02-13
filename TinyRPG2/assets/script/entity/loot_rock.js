function onAction(actor){
	var usedGlobal = "rock_searched_" + self.getName();
	var used = game.getGlobalb(usedGlobal);
	if(!used){
		var item = helper.getItem(self.getRuntimeProperty("item"));
		if(item == null)
			return;
		var count = util.tryGetInt(self.getRuntimeProperty("count"), 1);
		actor.getInventory().add(item, count);
		helper.dialog("You found " + item.getShowName() + " x " + count + ".");
		game.setGlobalb(usedGlobal, true);
	}
}

self.registerCallbacks(null, null, null, onAction);