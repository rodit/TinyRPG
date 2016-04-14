function dialogCallback0(option){
	if(option == 0)
		game.getPlayer().heal(1);
	else if(option == 1)
		game.getPlayer().removeHealth(1);
	else if(option == 2)
		game.getPlayer().healMagika(1);
	else if(option == 3)
		game.getPlayer().useMagika(1);
	else if(option == 4)
		helper.battle("grass", helper.createEntity("test_ent_0", "gui/menu_corner.png", "", 10), game.getPlayer());
	else if(option == 5){
		game.setGlobal("gui_quest", quests.get("test"));
		game.setGlobal("gui_quest_parent", "");
		helper.showGui("GuiQuest");
	}else if(option == 6)
		helper.setMap("map/test.tmx");
	else if(option == 7)
		helper.setMap("map/test_2.tmx");
}

function onSpawn(){}
function onDespawn(){}
function onCollide(collide){}
function onAction(actor){
	if(actor.isPlayer())
		helper.dialog("Test functions available...", helper.array("+1 HP", "-1 HP", "+1 Magika", "-1 Magika", "Test Battle", "Test Quest", "Go Back Home", "Go To Forest", "Cancel"), dialogCallback0);
}

self.registerCallbacks(onSpawn, onDespawn, onCollide, onAction);
