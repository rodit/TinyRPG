function onSpawn(){
	var berryItem = helper.getItem(self.getRuntimeProperty("berry"));
	var growInterval = berryItem.getGrowInterval();
	var lastPick = game.getGloball("berry_pick_" + self.getName());
	if(game.getTime() - lastPick >= growInterval)
		setGrown(berryItem);
}

function setGrown(berryItem){
	self.setResource(berryItem.getPlantResource());
}

function pick(berryItem){
	self.setResource("");
	game.setGloball("berry_pick_" + self.getName(), game.getTime());
	game.getPlayer().getInventory().add(berryItem, 1);
	helper.dialog("You picked a " + berryItem.getShowName() + ".");
}

function onAction(actor){
	var berryItem = helper.getItem(self.getRuntimeProperty("berry"));
	var growInterval = berryItem.getGrowInterval();
	var lastPick = game.getGloball("berry_pick_" + self.getName());
	if(game.getTime() - lastPick >= growInterval)
		pick(berryItem);
}

self.registerCallbacks(onSpawn, null, null, onAction);