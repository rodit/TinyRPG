function onCutseneComplete(cutseneScript, actor){
	game.getInput().allowMovement(true);
	helper.log("Cutsene execution complete.");
}

function onCollide(actor){
	if(!actor.isPlayer())
		return;
	var shouldRun = true;
	if(self.getRuntimeProperty("unlimited") != "true" && game.getGlobalb("done_cutsene_" + self.getName()))
		shouldRun = false;
	if(!shouldRun)
		return;
	var cutseneScript = self.getRuntimeProperty("cutsene");
	game.setGlobalb("done_cutsene_" + self.getName(), true);
	game.getInput().allowMovement(false);
	game.getScripts().executeFunction(game, cutseneScript, "run", self, helper.empty(), helper.empty(), helper.arrayo(actor));
}

self.registerCallbacks(null, null, onCollide, null);
