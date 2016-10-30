function onSpawn(){
	game.getPlayer().teleport(game, self.getX(), self.getY());
	if(game.getGlobalb("tile_movement"))
		game.getPlayer().getTileMovementProvider().alignToTile(game.getPlayer());
}

self.registerCallbacks(onSpawn, null, null, null);
