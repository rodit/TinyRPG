function onSpawn(){}
function onDespawn(){}
function onAction(actor){
	var key = "loot_" + self.getRuntimeProperty("loot_id") + "_used";
	var used = game.getGlobalb(key);
	helper.log(key + ": " + used);
	if(!used){
		game.setGlobalb(key, true);
		var itemString = self.getRuntimeProperty("loot_items");
		var itemPairs = helper.safeSplit(itemString, ";");
		for(var i = 0; i < itemPairs.length; i++){
			var pair = itemPairs[i];
			var parts = helper.safeSplit(pair, ":");
			if(parts.length < 2)
				continue;
			var name = "";
			var count = helper.getUtil().tryGetInt(parts[1], 0);
			if(parts[0].equals("gold")){
				actor.addMoney(count);
				name = "Gold";
			}else{
				actor.getInventory().add(parts[0], count);
				name = helper.getItem(parts[0]).getShowName();
			}
			helper.dialog("You found " + name + "x" + count + "!");
		}
	}else
		helper.dialog("You found nothing in this container.");
}
function onCollide(collide){}

self.registerCallbacks(onSpawn, onDespawn, onCollide, onAction);
