function onSpawn(){
	game.getMap().despawn(game, self);
	game.getMap().getMap().addMobSpawnArea(self.getRuntimeProperty("key"), self.getCollisionBounds());
}

self.registerCallbacks(onSpawn, null, null, null);
