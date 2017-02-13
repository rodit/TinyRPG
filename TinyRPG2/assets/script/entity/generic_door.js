function transport(actor){
	actor.setRuntimeProperty("map_origin", "door_" + self.getName());
	if(actor.isPlayer() && !game.getGlobalb("transitioning"))
		helper.setMap(self.getRuntimeProperty("map"));
}

var mustActivate = self.getRuntimeProperty("activate");
if(mustActivate == null || !mustActivate)
	self.registerCallbacks(null, null, transport, null);
else
	self.registerCallbacks(null, null, null, transport);
