package net.site40.rodit.tinyrpg.game.gui.windows;

import android.graphics.Canvas;
import android.graphics.Paint.Align;
import net.site40.rodit.tinyrpg.game.Game;
import net.site40.rodit.tinyrpg.game.Values;
import net.site40.rodit.tinyrpg.game.item.ItemStack;
import net.site40.rodit.tinyrpg.game.quest.Quest;
import net.site40.rodit.tinyrpg.game.quest.Quest.QuestImportance;
import net.site40.rodit.util.RenderUtil;

public class WindowQuest extends Window{
	
	private WindowComponent txtTitle;
	private WindowComponent txtDescription;
	private WindowComponent txtRewards;
	private WindowComponent btnBack;
	private WindowComponent btnAccept;
	private WindowComponent btnAbandon;
	
	private Quest quest;
	private Window source;
	
	public WindowQuest(Game game, Quest quest){
		this(game, quest, null);
	}
	
	public WindowQuest(Game game, Quest quest, Window source){
		super(game);
		this.quest = quest;
		this.source = source;
		initialize(game);
	}
	
	public Window getSource(){
		return source;
	}
	
	@Override
	public void initialize(Game game){
		if(quest == null)
			return;

		this.setBounds(320, 32, 576, 656);
		
		this.txtTitle = new WindowComponent("txtTitle");
		txtTitle.setX(32f);
		txtTitle.setY(92f);
		txtTitle.getPaint().setTextSize(Values.FONT_SIZE_BIG);
		txtTitle.getPaint().setTextAlign(Align.LEFT);
		txtTitle.setText(quest.getShowName());
		add(txtTitle);
		
		this.txtDescription = new WindowComponent("txtDescription"){
			@Override
			public void draw(Game game, Canvas canvas){
				canvas.save();
				canvas.translate(getScreenX(), getScreenY());
				RenderUtil.drawWrappedText(game, getText(), (int)(WindowQuest.this.getBounds().getWidth() - txtTitle.getBounds().getX()), paint, canvas);
				canvas.restore();
			}
		};
		txtDescription.setX(txtTitle.getBounds().getX());
		txtDescription.setY(txtTitle.getBounds().getY() + txtTitle.getPaint().getTextSize() + 24f);
		txtDescription.getPaint().setTextSize(Values.FONT_SIZE_SMALL);
		add(txtDescription);
		
		this.txtRewards = new WindowComponent("txtRewards");
		txtRewards.setText(getRewardsText(quest));
		txtRewards.setX(32f);
		txtRewards.setY(bounds.getHeight() - 32f - txtRewards.getText().split("\n").length - 128f);
		txtRewards.getPaint().setTextSize(Values.FONT_SIZE_SMALL);
		txtRewards.getPaint().setTextAlign(Align.LEFT);
		txtRewards.setFlag(WindowComponent.FLAG_MULTILINE_TEXT, true);
		add(txtRewards);
		
		this.btnBack = new WindowComponent("btnBack");
		btnBack.setX(32f);
		btnBack.setY(bounds.getHeight() - 16f - 72f);
		btnBack.setWidth(164f);
		btnBack.setHeight(72f);
		btnBack.getPaint().setTextSize(Values.FONT_SIZE_MEDIUM - 8f);
		btnBack.setBackground(WindowComponent.STATE_IDLE, "gui/button.png");
		btnBack.setBackground(WindowComponent.STATE_DOWN, "gui/button_selected.png");
		btnBack.setText("Back");
		btnBack.addListener(new WindowListener(){
			public void touchUp(Game game, WindowComponent component){
				WindowQuest.this.close();
			}
		});
		add(btnBack);
		
		this.btnAccept = new WindowComponent("btnAccept");
		btnAccept.setX(btnBack.getBounds().getX() + btnBack.getBounds().getWidth() + 32f);
		btnAccept.setY(btnBack.getBounds().getY());
		btnAccept.setWidth(btnBack.getBounds().getWidth());
		btnAccept.setHeight(btnBack.getBounds().getHeight());
		btnAccept.getPaint().setTextSize(Values.FONT_SIZE_MEDIUM - 8f);
		btnAccept.setBackground(WindowComponent.STATE_IDLE, "gui/button.png");
		btnAccept.setBackground(WindowComponent.STATE_DOWN, "gui/button_selected.png");
		btnAccept.setText("Accept");
		btnAccept.addListener(new WindowListener(){
			public void update(Game game, WindowComponent component){
				if(game.getQuests().hasQuest(quest) || game.getQuests().isTurnedIn(quest))
					component.setFlag(WindowComponent.FLAG_INVISIBLE, true);
				else
					component.setFlag(WindowComponent.FLAG_INVISIBLE, false);
			}
			
			public void touchUp(Game game, WindowComponent component){
				game.getQuests().setStage(quest, 0);
				WindowQuest.this.close();
			}
		});
		add(btnAccept);
		
		this.btnAbandon = new WindowComponent("btnAbandon");
		btnAbandon.setX(btnAccept.getBounds().getX() + btnAccept.getBounds().getWidth() + 32f);
		btnAbandon.setY(btnBack.getBounds().getY());
		btnAbandon.setWidth(btnBack.getBounds().getWidth());
		btnAbandon.setHeight(btnBack.getBounds().getHeight());
		btnAbandon.getPaint().setTextSize(Values.FONT_SIZE_MEDIUM - 8f);
		btnAbandon.setBackground(WindowComponent.STATE_IDLE, "gui/button.png");
		btnAbandon.setBackground(WindowComponent.STATE_DOWN, "gui/button_selected.png");
		btnAbandon.setText("Abandon");
		btnAbandon.addListener(new WindowListener(){
			public void update(Game game, WindowComponent component){
				if(!game.getQuests().hasQuest(quest) || game.getQuests().isTurnedIn(quest) || quest.getImportance() == QuestImportance.STORY)
					component.setFlag(WindowComponent.FLAG_INVISIBLE, true);
				else
					component.setFlag(WindowComponent.FLAG_INVISIBLE, false);
			}
			
			public void touchUp(Game game, WindowComponent component){
				game.getQuests().setStage(quest, -1);
				WindowQuest.this.close();
			}
		});
		add(btnAbandon);
	}
	
	private String getRewardsText(Quest quest){
		String rewTxt = "Gold: " + quest.getRewardGold() + "\nXP: " + quest.getRewardXp();
		if(!quest.getRewardItems().isEmptySize()){
			for(ItemStack stack : quest.getRewardItems().getItems())
				if(stack.getAmount() > 0)
					rewTxt += "\n" + stack.getItem().getShowName() + " x " + stack.getAmount();
		}
		return rewTxt;
	}
}
