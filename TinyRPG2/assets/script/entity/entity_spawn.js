function onSpawn(){
	game.getMap().despawn(game, self);
	var config = self.getRuntimeProperty("config");
	var ent = helper.createEntity(config);
	if(ent != null)
		game.getMap().spawn(game, ent);
	helper.log("Spawned entity name=" + ent.getName() + " showName=" + ent.getDisplayName() + ".");
	ent.setX(self.getX());
	ent.setY(self.getY());
}

self.registerCallbacks(onSpawn, null, null, null);
