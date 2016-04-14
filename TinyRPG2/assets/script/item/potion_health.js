function onEquip(ent){
	ent.heal(self.getLevel() * 5);
}

function onUnEquip(ent){}

self.registerCallbacks(onEquip, onUnEquip);
