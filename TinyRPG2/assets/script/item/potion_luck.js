function onWornOff(user){
	user.getStats().setLuck(user.getStats().getLuck() - helper.pow(2, self.getLevel()));
}

function onEquip(user){
	user.getStats().setLuck(user.getStats().getLuck() + helper.pow(2, self.getLevel()));
	helper.scheduleFunction(onWornOff, self, 60000, helper.arrayo(user));
}

function onUnEquip(user){}

self.registerCallbacks(onEquip, onUnEquip);
