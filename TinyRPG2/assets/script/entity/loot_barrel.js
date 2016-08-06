function onAction(actor){
	var usedVarName = "barrel_loot_" + self.getRuntimeProperty("loot_id") + "_used";
	var used = game.getGlobalb(usedVarName);
	if(!used){
		game.setGlobalb(usedVarName, true);
		var rarity = self.getRuntimeProperty("rarity");
		if(rarity.equals("food")){
			
		}else if(rarity.equals("potion")){
			
		}else if(rarity.equals("bone")){

		}else if(rarity.equals("bronze")){

		}else if(rarity.equals("gold")){
			var money = game.getRandom().nextInt(20, 200);
			actor.addMoney(money);
			helper.dialog("You found " + money + " gold.");
		}else if(rarity.equals("gold_small")){
			var money = game.getRandom().nextInt(1, 20);
			actor.addMoney(money);
			helper.dialog("You found " + money + " gold.");
		}else if(rarity.equals("gold_large")){
			var money = game.getRandom().nextInt(200, 1000);
			actor.addMoney(money);
			helper.dialog("You found " + money + " gold.");
		}else if(rarity.equals("empty")){
			helper.dialog("You found nothing in this container.");
		}
	}else
		helper.dialog("You found nothing in this container.");
}

self.registerCallbacks(null, null, null, onAction);
