function onEquip(helper){
	helper.checkGlobal("test_dialog_index", "0");
	helper.runTalk("dialog/test_dialog.tlk");
}

self.registerCallbacks(onEquip, null);