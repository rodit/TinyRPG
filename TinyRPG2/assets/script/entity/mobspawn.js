function onSpawn(){
	game.getMap().despawn(game, self);
	var ent = helper.createEntity(self.getRuntimeProperty("config"));
	if(ent != null){
		game.getMap().spawn(game, ent);
		ent.setX(self.getX());
		ent.setY(self.getY());
		ent.setWidth(self.getWidth());
		ent.setHeight(self.getHeight());
	}else
		helper.log("Error while creating mob spawn.");
	//game.getMap().getMap().addMobSpawnArea(self.getRuntimeProperty("key"), self.getCollisionBounds());
}

self.registerCallbacks(onSpawn, null, null, null);