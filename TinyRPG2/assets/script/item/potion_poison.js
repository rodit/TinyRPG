function onEquip(user){
	user.addEffect("poison", self.level);
}

function onUnEquip(user){}

self.registerCallbacks(onEquip, onUnEquip);
