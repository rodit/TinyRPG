function onSpawn(){
	game.getMap().despawn(game, self);
	game.getMap().setProperties(self.getRuntimeProperties());
}

self.registerCallbacks(onSpawn, null, null, null);