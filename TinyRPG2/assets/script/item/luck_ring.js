function onEquip(user){
	user.stats.addLuckMulti(self.level / 10);
}
function onUnEquip(user){
	user.stats.addLuckMulti(-(self.level / 10));
}

self.registerCallbacks(onEquip, onUnEquip);
