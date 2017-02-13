package net.site40.rodit.tinyrpg.game.gui.windows;

import java.util.ArrayList;

import net.site40.rodit.tinyrpg.game.Game;
import net.site40.rodit.tinyrpg.game.Values;
import net.site40.rodit.tinyrpg.game.gui.windows.WindowListboxComponent.ItemSelectedListener;
import net.site40.rodit.tinyrpg.game.gui.windows.WindowListboxComponent.ListboxComponentRenderer.StringRenderer;
import net.site40.rodit.tinyrpg.game.quest.Quest;
import net.site40.rodit.tinyrpg.game.render.Strings.UI;
import android.graphics.Paint.Align;

public class WindowQuests extends Window{
	
	private WindowComponent txtTitle;
	private WindowListboxComponent<String> lbQuests;

	public WindowQuests(Game game){
		super(game);
	}
	
	@Override
	public void initialize(Game game){
		this.setBounds(384, 32, 512, 656);
		
		this.zIndex = 10;
		
		this.txtTitle = new WindowComponent("txtTitle");
		txtTitle.setX(32f);
		txtTitle.setY(92f);
		txtTitle.getPaint().setTextSize(Values.FONT_SIZE_BIG);
		txtTitle.getPaint().setTextAlign(Align.LEFT);
		txtTitle.setText("Quests");
		add(txtTitle);
		
		this.lbQuests = new WindowListboxComponent<String>();
		lbQuests.setX(32f);
		lbQuests.setY(txtTitle.getBounds().getY() + txtTitle.getPaint().getTextSize() + 16f);
		lbQuests.setWidth(bounds.getWidth() - 64f);
		lbQuests.setHeight(bounds.getHeight() - bounds.getY() - 128f);
		lbQuests.getPaint().setTextAlign(Align.LEFT);
		lbQuests.getPaint().setTextSize(Values.FONT_SIZE_BIG);
		lbQuests.addListener(new WindowListener(){
			int lastSize = -1;
			private String uText;
			private ArrayList<Quest> uAccepted = new ArrayList<Quest>();
			public void update(Game game, WindowComponent component){
				uAccepted.clear();
				game.getQuests().getAccepted(uAccepted);
				if(lastSize < 0 || lastSize < uAccepted.size()){
					lbQuests.clear();
					for(Quest q : uAccepted){
						uText = q.getShowName() + (game.getQuests().isCompleted(q) ? UI.QUEST_COMPLETED : "");
						lbQuests.add(uText);
					}
					lastSize = uAccepted.size();
				}
			}
		});
		lbQuests.addListener(new ItemSelectedListener<String>(){
			private ArrayList<Quest> uAccepted = new ArrayList<Quest>();
			public void selected(Game game, WindowListboxComponent<String> listbox){
				int selectedIndex = listbox.getSelectedIndex();
				if(selectedIndex < 0)
					return;
				uAccepted.clear();
				game.getQuests().getAccepted(uAccepted);
				if(selectedIndex >= uAccepted.size())
					return;
				Quest selected = uAccepted.get(selectedIndex);
				WindowQuest questWindow = new WindowQuest(game, selected, WindowQuests.this);
				game.getWindows().register(questWindow);
				questWindow.zIndex = 0;
				questWindow.show();
			}
		});
		lbQuests.setRenderer(new StringRenderer());
		add(lbQuests);
	}
}
