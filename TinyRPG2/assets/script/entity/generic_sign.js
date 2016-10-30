function onAction(actor){
	var msg = self.getRuntimeProperty("msg");
	msg = helper.replace(msg, "%player%", game.getPlayer().getUsername());
	msg = helper.replace(msg, "%gold%", game.getPlayer().getMoney() + "");
	helper.dialog(helper.fixNewLines(msg));
}

self.registerCallbacks(null, null, null, onAction);
