function onSpawn(){
	var definition = self.getRuntimeProperty("definition");
	var tileEntity = new net.site40.rodit.tinyrpg.game.entity.world.AnimatedTile(definition);
	tileEntity.setX(self.getX());
	tileEntity.setY(self.getY());
	tileEntity.setWidth(self.getWidth());
	tileEntity.setHeight(self.getHeight());
	tileEntity.setName(self.getName());
	game.getMap().spawn(game, tileEntity);
	game.getMap().despawn(game, self);
}

self.registerCallbacks(onSpawn, null, null, null);