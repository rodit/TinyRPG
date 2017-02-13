function onAction(actor){
	var owner = self.getRuntimeProperty("owner");
	if(owner.equals("player")){
		helper.dialog("Would you like to sleep in this bed? You will recover HP and MP while sleeping.", helper.array("Yes", "No"), sleepCallback);
	}else
		helper.dialog("You do not own this bed.");
}

function sleepCallback(option){
	if(option == 0){
		game.getInput().allowMovement(false);
		game.skipFrames(60 * 2);
		helper.scheduleFunction(sleepFinished, self, 2000);
	}
}

function sleepFinished(){
	game.getInput().allowMovement(true);
	game.getPlayer().setHealth(game.getPlayer().getMaxHealth());
	game.getPlayer().setMagika(game.getPlayer().getMaxMagika());
	helper.dialog("You had a good sleep!~Your HP and MP are now fully recovered!");
}

self.registerCallbacks(null, null, null, onAction);