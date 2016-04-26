function onEquip(user){
	user.stats.addDefenceMulti(self.level / 100);
	user.stats.addStrengthMulti(self.level / 100);
}
function onUnEquip(user){
	user.stats.addDefenceMulti(-(self.level / 100));
	user.stats.addStrengthMulti(-(self.level / 100));
}

self.registerCallbacks(onEquip, onUnEquip);
