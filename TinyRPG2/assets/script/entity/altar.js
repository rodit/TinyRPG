function onAction(actor){
	var factionName = self.getRuntimeProperty("faction");
	var faction = helper.getFaction(factionName);
	if(faction == null)
		helper.dialog("This altar does not belong to any faction.");
	else{
		if(actor.getFaction().getFaction() == faction)
			helper.dialog("What would you like to do?", helper.array("Make Offering", "Leave Faction", "Back"), dialog_callback_member, helper.arrayo(actor, faction));
		else
			helper.dialog("What would you like to do?", helper.array("Join Faction", "Back"), dialog_callback_other, helper.arrayo(actor, faction));
	}
}

function dialog_callback_member(selected, actor, faction){
	if(selected == 0){
		var offerings = faction.getAvailableOfferings(actor);
		var opts = helper.genArray(offerings.length() + 1);
		helper.writeArray(opts, opts.length - 1, "Back");
		for(var i = 0; i < offerings.length; i++){
			var offering = offerings.get(i);
			helper.writeArray(opts, i, "Offer " + offering.getOffering().getItem().getShowName() + "x" + offering.getOffering().getAmount());
		}
		helper.dialog("What would you like to offer?", opts, dialog_callback_offer, helper.arrayo(actor, faction, offerings));
	}else if(selected == 1)
		helper.dialog("If you leave your current faction, your faction level will be reset.~Are you sure you would like to leave?", helper.array("Yes", "No"), dialog_callback_leave_faction, helper.arrayo(actor, faction));
}

function dialog_callback_offer(selected, actor, faction, offerings){
	if(selected < offerings.length()){
		var selectedOffering = offerings.get(selected);
		faction.submitOffering(game, selectedOffering, actor);
	}
}

function dialog_callback_leave_faction(selected, actor, faction){
	if(selected == 0){
		actor.setFaction(helper.getFaction("none"));
		helper.dialog("You have left the faction " + faction.getDisplayName() + ".");
	}
}

function dialog_callback_other(selected, actor, faction){
	if(selected == 0){
		if(faction.canJoin(actor)){
			if(actor.getFaction() == null)
				join_faction(0, actor, faction);
			else
				helper.dialog("You must leave your current faction before joining a new one.~If you leave your current faction, your faction level will be reset.~Would you like to leave your current faction and join the faction " + faction.getDisplayName() + "?", helper.array("Yes", "No"), join_faction, helper.arrayo(actor, faction));
		}else
			helper.dialog("You do not meet the requirements to join this faction.");
	}
}

function join_faction(selected, actor, faction){
	actor.setFaction(faction);
	helper.dialog("You are now part of the faction " + faction.getDisplayName() + ".");
}

self.registerCallbacks(null, null, null, onAction);