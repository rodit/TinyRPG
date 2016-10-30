package net.site40.rodit.tinyrpg.game.gui.windows;

import java.util.ArrayList;

import android.graphics.Paint.Align;
import net.site40.rodit.tinyrpg.game.Game;
import net.site40.rodit.tinyrpg.game.Values;
import net.site40.rodit.tinyrpg.game.gui.windows.WindowListboxComponent.ItemSelectedListener;
import net.site40.rodit.tinyrpg.game.gui.windows.WindowListboxComponent.ListboxComponentRenderer.StringRenderer;
import net.site40.rodit.tinyrpg.game.quest.Quest;

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
		lbQuests.setY(txtTitle.getY() + txtTitle.getPaint().getTextSize() + 16f);
		lbQuests.setWidth(getWidth() - 64f);
		lbQuests.setHeight(getHeight() - getY() - 128f);
		lbQuests.getPaint().setTextAlign(Align.LEFT);
		lbQuests.getPaint().setTextSize(Values.FONT_SIZE_BIG);
		lbQuests.addListener(new WindowListener(){
			int lastSize = -1;
			public void update(Game game, WindowComponent component){
				ArrayList<Quest> accepted = game.getQuests().getAccepted();
				if(lastSize < 0 || lastSize < accepted.size()){
					lbQuests.clear();
					for(Quest q : accepted){
						String text = q.getShowName() + (game.getQuests().isCompleted(q) ? " (Completed)" : "");
						lbQuests.add(text);
					}
					lastSize = accepted.size();
				}
			}
		});
		lbQuests.addListener(new ItemSelectedListener<String>(){
			public void selected(Game game, WindowListboxComponent<String> listbox){
				int selectedIndex = listbox.getSelectedIndex();
				if(selectedIndex < 0)
					return;
				ArrayList<Quest> accepted = game.getQuests().getAccepted();
				if(selectedIndex >= accepted.size())
					return;
				Quest selected = accepted.get(selectedIndex);
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
