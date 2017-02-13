function onAction(actor){
	var map = self.getRuntimeProperty("map");
	var requiredItem = helper.getItem(self.getRuntimeProperty("key"));
	if(requiredItem == null || !actor.getInventory().hasItem(requiredItem))
		helper.dialog("The door is locked and cannot be opened.");
	else
		helper.dialog("You used the " + requiredItem.getShowName() + ".~The door is now unlocked.", helper.array(), callback0, helper.arrayo(map));
}

function callback0(option, map){
	helper.setMap(map);
}

self.registerCallbacks(null, null, null, onAction);