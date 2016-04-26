function onEquip(user){
	user.setNoclip(true);
}
function onUnEquip(user){
	user.setNoclip(false);
}

self.registerCallbacks(onEquip, onUnEquip);
