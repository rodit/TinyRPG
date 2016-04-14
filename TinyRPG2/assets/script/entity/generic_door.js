function onSpawn(){}
function onDespawn(){}
function onAction(actor){}
function onCollide(collide){
	if(collide.isPlayer()){
		collide.setRuntimeProperty("map_origin", "door_" + self.getName());
		helper.setMap(self.getRuntimeProperty("map"));
	}
}

self.registerCallbacks(onSpawn, onDespawn, onCollide, onAction);
