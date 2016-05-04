function onAction(actor){
	actor.setRuntimeProperty("map_origin", "door_" + self.getName());
	if(actor.isPlayer())
		helper.setMap(self.getRuntimeProperty("map"));
}

self.registerCallbacks(null, null, null, onAction);
