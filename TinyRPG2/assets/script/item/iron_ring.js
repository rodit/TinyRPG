function onEquip(user){
	user.stats.defenceMulti += self.level / 10;
}
function onUnEquip(user){
	user.stats.defenceMulti -= self.level / 10;
}

self.registerCallbacks(onEquip, onUnEquip);
