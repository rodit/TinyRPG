function onSpawn(){
	var config = self.getRuntimeProperty("config");
	var ent = helper.createEntity(config);
	if(ent != null)
		map.spawn(ent);
	ent.setX(self.getX());
	ent.setY(self.getY());
	map.despawn(self);
}

self.registerCallbacks(onSpawn, null, null, null);
