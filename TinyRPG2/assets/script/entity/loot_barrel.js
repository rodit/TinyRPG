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
			actor.addMoney(game.getRandom().nextInt(20, 200));
		}else if(rarity.equals("gold_small")){
			actor.addMoney(game.getRandom().nextInt(1, 20));
		}else if(rarity.equals("gold_large")){
			actor.addMoney(game.getRandom().nextInt(200, 1000));
		}
	}else
		helper.dialog("You found nothing in this container.");
}

self.registerCallbacks(null, null, null, onAction);
