function onEquip(user){
	if(user.isPlayer()){
		game.setGlobal("player_equip_ruby_necklace_level", user.getStats().getLevel());
		user.setMaxHealth(user.getMaxHealth() * (1 + (self.getLevel() * 5 / 100)));
	}else
		user.setMaxHealth(user.getMaxHealth() * (1 + (self.getLevel() * 5 / 100)));
}
function onUnEquip(user){
	if(user.isPlayer()){
		var level = game.getGlobal("player_equip_ruby_necklace_level");
		var currentLevel = user.getStats().getLevel();
		user.getStats().setLevel(level);
		user.setMaxHealth(SuperCalc.getMaxHealth(user) / (1 + (self.getLevel() * 5 / 100)));
		user.getStats().setLevel(currentLevel);
	}else
		user.setMaxHealth(user.getMaxHealth() / (1 + (self.getLevel() * 5 / 100)));
}

self.registerCallbacks(onEquip, onUnEquip);
