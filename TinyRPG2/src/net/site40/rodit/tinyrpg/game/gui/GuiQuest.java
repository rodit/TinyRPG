package net.site40.rodit.tinyrpg.game.gui;
import java.util.regex.Pattern;

import net.site40.rodit.tinyrpg.game.Dialog;
import net.site40.rodit.tinyrpg.game.Dialog.DialogCallback;
import net.site40.rodit.tinyrpg.game.Game;
import net.site40.rodit.tinyrpg.game.Input;
import net.site40.rodit.tinyrpg.game.Values;
import net.site40.rodit.tinyrpg.game.item.ItemStack;
import net.site40.rodit.tinyrpg.game.quest.Quest;
import net.site40.rodit.tinyrpg.game.quest.Quest.QuestImportance;
import net.site40.rodit.util.RenderUtil;
import android.graphics.Canvas;
import android.graphics.Paint.Align;
import android.graphics.RectF;

public class GuiQuest extends Gui{

	private Game gameObj;

	private static final RectF BOUNDS = new RectF(32f, 32f, 32f + 512f, 32f + 512f);

	private final DialogCallback disbandConfirmed = new DialogCallback(){
		@Override
		public void onSelected(int selected){
			if(selected == 0){
				gameObj.getQuests().setStage(getQuest(gameObj), -1);
				gameObj.getGuis().hide(GuiQuest.class);
				gameObj.getGuis().show(gameObj.getHelper().getGui((String)gameObj.getGlobal("gui_quest_parent")));
			}
		}
	};

	private final DialogCallback optionSelected = new DialogCallback(){
		@Override
		public void onSelected(int selected){
			if(selected == 0)
				gameObj.addObject(disbandDialog);
		}
	};

	private Dialog disbandDialog = new Dialog("Are you sure you would like to disband this quest?\nYou will loose all progress in the quest and this cannot be undone!", new String[] { "Yes", "No" }, disbandConfirmed);
	private Dialog optionsDialog = new Dialog("What would you like to do?", new String[] { "Disband Quest", "Nothing" }, optionSelected);

	public GuiQuest(){
		super("");
	}

	private Quest getQuest(Game game){
		Quest q = (Quest)game.getGlobal("gui_quest");
		if(q == null)
			return new Quest("null", "null", "null", QuestImportance.OPTIONAL, 0, 0, 0);
		return q;
	}
	
	@Override
	public void init(){
		Component bg = new Component("bg"){
			@Override
			public void update(Game game){
				GuiQuest.this.gameObj = game;
				super.update(game);

				Input input = game.getInput();
				input.allowMovement(false);
				if(input.isUp(Input.KEY_MENU)){
					game.removeObject(disbandDialog);
					game.removeObject(optionsDialog);
					game.getInput().allowMovement(true);
					game.getGuis().hide(GuiQuest.class);
					game.getGuis().show(game.getHelper().getGui((String)game.getGlobal("gui_quest_parent")));
					input.allowMovement(true);
				}
				if(input.isUp(Input.KEY_ACTION) && !game.getObjects().contains(optionsDialog) && !game.getObjects().contains(disbandDialog))
					game.addObject(optionsDialog);
			}
			@Override
			public void draw(Game game, Canvas canvas){
				RenderUtil.drawBitmapBox(canvas, game, BOUNDS, getPaint());
			}
		};
		add(bg);

		Component txtName = new Component("txtName");
		txtName.addListener(new ComponentListener(){
			public void update(Component component, Game game){
				component.setText(getQuest(game).getShowName());
			}
			public void touchDown(Component component, Game game){}
			public void touchUp(Component component, Game game){}
		});
		txtName.getPaint().setTextSize(Values.FONT_SIZE_MEDIUM);
		txtName.getPaint().setTextAlign(Align.LEFT);
		txtName.setX(BOUNDS.left + 32f);
		txtName.setY(BOUNDS.top + 48f);
		add(txtName);

		Component txtDesc = new Component("txtDesc"){
			@Override
			public void draw(Game game, Canvas canvas){
				canvas.translate(BOUNDS.left + 32f, BOUNDS.top + 72f);
				RenderUtil.drawWrappedText(game, getQuest(game).getDescription(), (int)(BOUNDS.width() - 64f), getPaint(), canvas);
				canvas.translate(-(BOUNDS.left + 32f), -(BOUNDS.top + 72f));
			}
		};
		txtDesc.getPaint().setTextAlign(Align.LEFT);
		txtDesc.getPaint().setTextSize(Values.FONT_SIZE_SMALL);
		add(txtDesc);

		final Component txtRewards = new Component("txtRewards", "Rewards");
		txtRewards.setX(BOUNDS.left + 32f);
		txtRewards.getPaint().setTextAlign(Align.LEFT);
		txtRewards.getPaint().setTextSize(32f);
		txtRewards.addListener(new ComponentListener(){
			public void update(Component component, Game game){
				Quest q = getQuest(game);
				int lines = q.getDescription().split(Pattern.quote("\n")).length;
				component.setY(BOUNDS.top + 72f + Values.FONT_SIZE_SMALL + (float)lines * Values.FONT_SIZE_SMALL + 32f);
			}
			public void touchDown(Component component, Game game){}
			public void touchUp(Component component, Game game){}
		});
		add(txtRewards);

		final Component txtRewardsXpGold = new Component("txtRewardsXpGold", "XP: 0\nGold: 0");
		txtRewardsXpGold.setX(BOUNDS.left + 32f);
		txtRewardsXpGold.getPaint().setTextSize(Values.FONT_SIZE_SMALL);
		txtRewardsXpGold.getPaint().setTextAlign(Align.LEFT);
		txtRewardsXpGold.addListener(new ComponentListener(){
			public void update(Component component, Game game){
				component.setY(txtRewards.getY() + txtRewards.getPaint().getTextSize() + 16f);
				Quest q = getQuest(game);
				component.setText("XP: " + q.getRewardXp() + "\nGold: " + q.getRewardGold());
			}
			public void touchDown(Component component, Game game){}
			public void touchUp(Component component, Game game){}
		});
		add(txtRewardsXpGold);
		
		Component txtRewardItem = new Component("txtRewardItem", "");
		txtRewardItem.setX(BOUNDS.left + 32f);
		txtRewardItem.getPaint().setTextSize(Values.FONT_SIZE_SMALL);
		txtRewardItem.getPaint().setTextAlign(Align.LEFT);
		txtRewardItem.addListener(new ComponentListener(){
			public void update(Component component, Game game){
				component.setY(txtRewardsXpGold.getY() + txtRewardsXpGold.getPaint().getTextSize() * 2f + 16f);
				Quest q = getQuest(game);
				if(!q.getRewardItems().isEmpty()){
					component.setText("Items:\n");
					for(ItemStack stack : q.getRewardItems().getItems())
						component.setText(component.getText() + stack.getItem().getShowName() + "x" + stack.getAmount() + "\n");
				}
			}
			public void touchDown(Component component, Game game){}
			public void touchUp(Component component, Game game){}
		});
		add(txtRewardItem);
	}
}
