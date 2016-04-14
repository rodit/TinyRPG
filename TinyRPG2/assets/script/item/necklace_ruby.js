function onEquip(user){
	user.stats.hpMulti += self.level * 5 / 10;
}
function onUnEquip(user){
	user.stats.hpMulti -= self.level * 5 / 10;
}

self.registerCallbacks(onEquip, onUnEquip);
