function wellCallback(option){
	if(option == 0){
		if(game.getPlayer().getMoney() >= 1)
			game.getPlayer().subtractMoney(1);
		else{
			helper.dialog("You do not have enough gold!");
			return;
		}
		if(helper.should(98)){
			var amount = game.getRandom().nextInt(20 * game.getPlayer().getStats().getLevel());
			game.getPlayer().addMoney(amount);
			helper.dialog("You threw a gold coin into the well and made a wish. You found " + amount + " gold!");
		}else
			helper.dialog("You threw a gold coin into the well and made a wish. There was no effect.");
	}
}

function onAction(actor){
	if(actor.isPlayer())
		helper.dialog("Would you like to throw a gold coin into the well?", helper.array("Yes", "No"), wellCallback);
}

self.registerCallbacks(null, null, null, onAction);
