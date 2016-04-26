function onAction(actor){
	actor.clearNegativeEffects();
	helper.dialog("You washed your hands.~All negative, active effects have been cleared.");
}

self.registerCallbacks(null, null, null, onAction);
