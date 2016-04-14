function onUse(user, target){
	target.hurt(user, self);
}

self.registerCallbacks(onUse);
