function onEquip(user){
	user.stats.addMagikaMulti(self.level / 10);
}
function onUnEquip(user){
	user.stats.addMagikaMulti(-(self.level / 10));
}

self.registerCallbacks(onEquip, onUnEquip);