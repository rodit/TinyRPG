function onEquip(user){
	user.stats.setLuck(user.stats.getLuck() + self.level);
}
function onUnEquip(user){
	user.stats.setLuck(user.stats.getLuck() - self.level);
}

self.registerCallbacks(onEquip, onUnEquip);
