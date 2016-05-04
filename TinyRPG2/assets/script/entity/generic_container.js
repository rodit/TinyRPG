function dialogCallback0(option){
	game.setGlobal("current_container", self);
	if(option == 0)
		helper.showGui("GuiContainer");
}

function onAction(actor){
	helper.dialog("What would you like to do?", helper.array("Access Container", "Cancel"), dialogCallback0);
}

self.registerCallbacks(null, null, null, onAction);
