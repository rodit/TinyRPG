function onSpawn(){}
function onDespawn(){}
function onCollide(collide){}

function onAction(actor){
	var count = game.getGlobali("merek_speak_count");
	if(count == 0)
		helper.dialog("Hello there. You must be a new here. I'm Merek, the blacksmith of Saker Keep.");
	game.incGlobal("merek_speak_count", 1);
}

self.registerCallbacks(onSpawn, onDespawn, onCollide, onAction);
