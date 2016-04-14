function onEquip(user){}
function onUnEquip(user){}

function onHit(user, target){
	target.hurt(user, self);
}

self.registerCallbacks(onEquip, onUnEquip, onHit);
