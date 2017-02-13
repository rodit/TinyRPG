function start(user){
	self.setCount(self.level * 2.5);
}

function poison(entity){
	entity.hit(game, helper.fakeEntity, helper.damage(helper.arrayo(entity, self), "EFFECT", self.level * 3);
}

function stop(user){}

self.registerCallbacks(start, stop, poison);
