function onEquip(user){
	user.stats.luck += self.level;
}
function onUnEquip(user){
	user.stats.luck -= self.level;
}

self.registerCallbacks(onEquip, onUnEquip);
