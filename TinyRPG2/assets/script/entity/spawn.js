function onSpawn(){
	game.getPlayer().teleport(game, self.getX(), self.getY());
}

self.registerCallbacks(onSpawn, null, null, null);
