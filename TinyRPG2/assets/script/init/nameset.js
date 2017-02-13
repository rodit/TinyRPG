function dialogCallback0(option){
	game.removeObject(game.getObject(game.getGlobal("bg_sprite_obj_name")));
	helper.setMap(game.getGlobal("start_map"), true);
	game.getPlayer().addMoney(100);
	game.getResources().putObject("background/intro.png", null);
}

helper.dialog("Hello " + game.getPlayer().getUsername() + "! You are now ready to begin!", helper.empty(), dialogCallback0);
