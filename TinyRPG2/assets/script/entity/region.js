function onSpawn(){
	game.getMap().getMap().addRegionLocation(helper.getRegion(self.getRuntimeProperty("region")), self.getCollisionBounds());
    game.getMap().despawn(self);
}

self.registerCallbacks(onSpawn, null, null, null);
