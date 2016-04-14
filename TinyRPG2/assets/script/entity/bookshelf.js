function onAction(actor){
	var difference = game.getTime() - helper.parseLong(game.getGlobal("last_bookshelf_skill"));
	var times = helper.parseInt(game.getGlobal("bookshelf_skill_count")) + 1;
	var xp = times * 100;
	if(difference >= 60000){
		game.getPlayer().getStats().addXp(xp);
		game.setGlobal("last_bookshelf_skill", game.getTime() + "");
		game.setGlobal("bookshelf_skill_count", times + "");
		helper.dialog("You read a book and learnt some stuff. You gained " + xp + "xp!");
	}else
		helper.dialog("You had a good read!");
}

self.registerCallbacks(null, null, null, onAction);
