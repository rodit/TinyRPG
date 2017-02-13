function onSpawn(){
	self.setNoclip(true);
	game.getPlayer().teleport(game, self.getBounds().getX(), self.getBounds().getY());
	if(game.getGlobalb("tile_movement"))
		game.getPlayer().getTileMovementProvider().alignToTile(game.getPlayer());
}

self.registerCallbacks(onSpawn, null, null, null);
