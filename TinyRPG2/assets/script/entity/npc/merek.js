function onSpawn(){}
function onDespawn(){}
function onCollide(collision){}

function dialog_callback(option){
	if(option == 0)
		helper.dialog("Sorry friend. I cannot upgrade any items at the moment.");
	else if(option == 1)
		helper.dialog("I do not have the required resources to infuse items right now.");
	else if(option == 2)
		helper.dialog("I have no items to sell you at the moment. Please come back later when I have restocked.");
	else if(option == 3)
		helper.dialog("I have too little money to purchase any items from you.");
	else if(option == 4){
		if(!game.getGlobalb("merek_home_given_item") && game.getGlobalb("merek_home_give_item")){
			helper.dialog("Here, have this Steel Dagger. It's forged from some of the finest steel around. Wield it with care.~You received a Steel Dagger.~This will help you greatly in the beginning of your travels. Good luck friend.");
			game.getPlayer().getInventory().add("dagger_steel", 1);
			game.setGlobalb("merek_home_given_item", true);
		}else
			helper.runTalk("dialog/merek_home.tlk");
	}else{
		if(helper.should(50))
			helper.dialog("Well I'll be seeing you then. Be careful out there.");
		else
			helper.dialog("Try not to get yourself killed. Neither of us want to see that!");
	}
}

function onAction(actor){
	var count = game.getGlobali("merek_speak_count");
	if(count == 0)
		helper.dialog("Hello there. You must be a new here. I'm Merek, the blacksmith of Saker Keep.~If you require smithing, then speak to me.");
	else{
		var greeting = "Well, hello again. You seem to be doing alright.~Need anything forged?"
		if(helper.should(35))
			greeting = "Well, hello again. Glad to see your still alive.~Need a hand with some forge work?";
		else if(helper.should(35))
			greeting = "Well, hello again. Your survival capabilities are impressive.~Got a forging job for me?";
		helper.dialog(greeting, helper.array("Upgrade Item", "Infuse Item", "Purchase Items", "Sell Items", "Talk", "Cancel"), dialog_callback);
	}
	game.incGlobal("merek_speak_count", 1);
}

self.registerCallbacks(onSpawn, onDespawn, onCollide, onAction);
