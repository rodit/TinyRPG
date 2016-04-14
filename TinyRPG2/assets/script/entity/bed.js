function onHealComplete(ent){
	game.getInput().enable();
	game.removeObject(game.getGlobal("bed_shadow_obj"));
	game.removeObject(game.getGlobal("bed_shadow_txt"));
	ent.setHealth(ent.getMaxHealth());
	helper.dialog("Your health has been fully restored.");
}

function dialogCallback0(option, ent){
	if(option == 0){
		if(ent.isPlayer()){
			helper.scheduleFunction(onHealComplete, self, 2000, helper.arrayo(ent));
			var black = helper.createObject("black.png", 0, 0, 1280, 720, false);
			var restTxt = helper.createText("Resting...", 720, 360, 92);
			game.setGlobal("bed_shadow_obj", black);
			game.setGlobal("bed_shadow_txt", restTxt);
			game.addObject(black);
			game.addObject(restTxt);
			game.getInput().disable();
		}
	}
}

function onAction(actor){
	helper.var_dump(actor);
	helper.dialog("Would you like to restore your health? This will take a couple of seconds.", helper.array("Sure!", "No Thanks"), dialogCallback0, helper.arrayo(actor));
}

self.registerCallbacks(null, null, null, onAction);
