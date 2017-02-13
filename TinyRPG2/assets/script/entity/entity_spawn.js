function onSpawn(){
	game.getMap().despawn(game, self);
	var config = self.getRuntimeProperty("config");
	var ent = helper.createEntity(config);
	helper.queueFunction(spawnEnt, self, helper.arrayo(ent));
}

function spawnEnt(ent){
	helper.log("Spawned entity name=" + ent.getName() + " showName=" + ent.getDisplayName() + ".");
	ent.setX(self.getBounds().getX());
	ent.setY(self.getBounds().getY());
	if(self.getRuntimeProperty("set_size") != null && self.getRuntimeProperty("set_size").equals("true")){
		ent.setWidth(self.getBounds().getWidth());
		ent.setHeight(self.getBounds().getHeight());
	}
	ent.setRuntimeProperties(self.getRuntimeProperties());
	game.getMap().spawn(game, ent);
}

self.registerCallbacks(onSpawn, null, null, null);
