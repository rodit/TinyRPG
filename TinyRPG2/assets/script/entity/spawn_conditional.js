function onSpawn(){
	self.setNoclip(true);
	var origin = game.getPlayer().getRuntimeProperty("map_origin");
	var condition = self.getRuntimeProperty("condition");
	if(origin.equals(condition)){
		game.getPlayer().teleport(game, self.getBounds().getX(), self.getBounds().getY());
		if(game.getGlobalb("tile_movement"))
			game.getPlayer().getTileMovementProvider().alignToTile(game.getPlayer());
	}else
		helper.log("Player did not meet spawn condition for " + self.getName() + ".");
}

self.registerCallbacks(onSpawn, null, null, null);
