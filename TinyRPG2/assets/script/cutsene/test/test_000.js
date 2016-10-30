function run(actor){
	helper.dialog("This is a test cutsene!", helper.empty(), baseCallback, helper.arrayo(actor, 0));
}

function baseCallback(notUsed, actor, id){
	switch(id){
		case 0:
			helper.dialog("You have completed the first stage of the cutsene!", helper.empty(), baseCallback, helper.arrayo(actor, 1));
			break;
		case 1:
			cutsene.moveEntity(game, actor, "down", 200, baseCallback, helper.arrayo(0, actor, 2));
			break;
	}
}
