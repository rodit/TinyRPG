helper.hideGui("GuiMenu");
helper.showGui("GuiIngame");

if(game.getGlobal("intro_done") == "false"){
	var bgSprite = helper.createObject("background/intro.png", 0, 0, 1280, 720, false);
	bgSprite.ignoreScroll = true;
	game.setGlobal("bg_sprite_obj_name", bgSprite.getName());
	game.addObject(bgSprite);

	function dialogCallback1(option){
		helper.hideGui("GuiIngame");
		helper.showGui("GuiSetname");
	}

	function dialogCallback0(option){
		if(option == 0 || option == 1 || option == 2)
			helper.dialog("Excellent! I guess your ready for the next stage! What is your name?", helper.empty(), dialogCallback1);
		else
			helper.dialog("Calm down! We'll get started soon enough. Now I'm going to ask you again. How are you feeling?", helper.array("Amazing", "Great", "Okay"), dialogCallback0);
	}

	helper.dialog("Hello adventurer! Welcome to the world of TinyRPG. How are you feeling?", helper.array("Amazing", "Great", "Okay", "Get on with it..."), dialogCallback0);
}else{
	helper.setMap(game.getGlobal("start_map"));
}
