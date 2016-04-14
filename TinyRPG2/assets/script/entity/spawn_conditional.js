function onSpawn(){
	var origin = game.getPlayer().getRuntimeProperty("map_origin");
	var condition = self.getRuntimeProperty("condition");
	if(origin.equals(condition))
		game.getPlayer().teleport(game, self.getX(), self.getY());
}

self.registerCallbacks(onSpawn, null, null, null);
