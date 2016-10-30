function onAction(actor){
	helper.showWindow("WindowQuest", quests.get("test"));
}

self.registerCallbacks(null, null, null, onAction);
