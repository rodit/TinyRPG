function onSpawn(){
	if(!game.getGlobalb("trap_done_" + self.getName()))
		this.setResource(self.getRuntimeProperty("resource"));
}

function onCollide(actor){
	if(actor.isPlayer()){
		this.setResource("");
		var mapName = self.getRuntimeProperty("map");
		if(!game.getGlobalb("trap_done_" + self.getName()))
			helper.dialog("You fell into a trap!", helper.array(), callback0, helper.arrayo(actor, mapName, self));
		else{
			game.setGlobalb("trap_done_" + self.getName(), true);
			callback0(0, actor, mapName, self);
		}
	}
}

function callback0(option, actor, map, trap){
	actor.setRuntimeProperty("map_origin", "trap_" + trap.getName());
	helper.setMap(map);
}

self.registerCallbacks(onSpawn, null, onCollide, null);