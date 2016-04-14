function onEquip(user){
	user.stats.hpMulti += self.level * 2 / 10;
}
function onUnEquip(user){
	user.stats.hpMulti -= self.level * 2 / 10;
}

self.registerCallbacks(onEquip, onUnEquip);
