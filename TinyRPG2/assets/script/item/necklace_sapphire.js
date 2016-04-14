function onEquip(user){
	user.getStats().setLuck(user.getStats().getLuck() + self.getLevel());
}
function onUnEquip(user){
	user.getStats().setLuck(user.getStats().getLuck() - self.getLevel());
}

self.registerCallbacks(onEquip, onUnEquip);
