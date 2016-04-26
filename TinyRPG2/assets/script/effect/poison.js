function start(user){
	self.setCount(self.level * 2.5);
}

function poison(entity){
	entity.hurt(self.level * 3);
}

function stop(user){}

self.registerCallbacks(start, stop, poison);
