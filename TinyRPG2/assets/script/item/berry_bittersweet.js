function onUse(user){
	user.hit(game, helper.fakeEntity, helper.damage(helper.arrayo(user, self), "ITEM", self.level * 5));
	if(user.isPlayer())
		helper.dialog("The bittersweet berry's toxicity has hurt you.");
}

self.registerCallbacks(onUse, null);