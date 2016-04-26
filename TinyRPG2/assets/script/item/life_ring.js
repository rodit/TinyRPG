function onEquip(user){
	user.stats.addHpMulti(self.level * 2 / 100);
}
function onUnEquip(user){
	user.stats.addHpMulti(-(self.level * 2 / 100));
}

self.registerCallbacks(onEquip, onUnEquip);
