function onEquip(user){
	user.stats.addHpMulti(self.level * 5 / 100);
}
function onUnEquip(user){
	user.stats.addHpMulti(-(self.level * 5 / 100));
}

self.registerCallbacks(onEquip, onUnEquip);
