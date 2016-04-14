function onEquip(ent){
	ent.healMagika(self.getLevel() * 5);
}

function onUnEquip(ent){}

self.registerCallbacks(onEquip, onUnEquip);
