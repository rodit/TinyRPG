package net.site40.rodit.tinyrpg.game.gui.windows;

import net.site40.rodit.tinyrpg.game.Dialog.DialogCallback;
import net.site40.rodit.tinyrpg.game.Game;
import net.site40.rodit.tinyrpg.game.SuperCalc;
import net.site40.rodit.tinyrpg.game.Values;
import net.site40.rodit.tinyrpg.game.entity.EntityPlayer;
import net.site40.rodit.tinyrpg.game.entity.EntityStats;
import net.site40.rodit.tinyrpg.game.item.Hair;
import net.site40.rodit.tinyrpg.game.item.Item;
import net.site40.rodit.tinyrpg.game.item.ItemEquippable;
import net.site40.rodit.tinyrpg.game.item.ItemStack;
import net.site40.rodit.tinyrpg.game.object.Bounds;
import net.site40.rodit.tinyrpg.game.render.SpriteSheet.MovementState;
import net.site40.rodit.tinyrpg.game.render.Strings;
import net.site40.rodit.tinyrpg.game.render.Strings.GameData;
import net.site40.rodit.tinyrpg.game.start.StartClass;
import net.site40.rodit.util.GrammarUtil;
import net.site40.rodit.util.Util;
import android.graphics.Canvas;
import android.graphics.Paint.Align;
import android.text.TextUtils;

public class WindowPlayerEditor extends Window{

	public static final String[] skinColours = new String[] { "white", "black", "blue", "red", "orange", "green", "purple", "yellow" };
	public static final String[] hairColours = new String[] { "lbrown", "dbrown", "black", "white", "red", "yellow", "blue", "green", "pink", "purple" };
	public static final Hair nullHair;
	
	static{
		nullHair = new Hair();
		nullHair.setId(0);
		nullHair.setColor("black");
	}
	
	private StartClass startClass;
	private int skinIndex;
	private int hairId;
	private int hairColourIndex;
	
	private EntityPlayer previewPlayer;
	private Hair previewHair;
	private Item[] previewEquipped;
	private boolean drawHair;
	private boolean drawItems;
	
	private WindowComponent txtTitle;
	private WindowComponent txtNameLabel;
	private WindowTextBoxComponent txtName;
	private WindowComponent txtStartClass;
	private WindowComponent btnNextStartClass;
	private WindowComponent btnPrevStartClass;
	private WindowComponent txtStartClassDesc;
	private WindowComponent txtPlayerColour;
	private WindowComponent btnNextPlayerColour;
	private WindowComponent btnPrevPlayerColour;
	private WindowComponent txtPlayerHairLabel;
	private WindowComponent btnNextHair;
	private WindowComponent btnPrevHair;
	private WindowComponent txtPlayerHairColour;
	private WindowComponent btnNextHairColour;
	private WindowComponent btnPrevHairColour;
	private WindowComponent playerPreview;
	private WindowComponent txtStats;
	private WindowComponent txtStatsValues;
	private WindowComponent btnConfirm;

	public WindowPlayerEditor(Game game){
		super(game);
	}
	
	@Override
	public void initialize(Game game){
		this.setBounds(0, 0, 1280, 720);
		this.canClose = false;

		previewPlayer = new EntityPlayer(){
			@Override
			public Bounds getBounds(){
				return bounds;
			}
		};
		previewHair = new Hair();
		previewHair.setId(hairId);
		previewHair.setColor(hairColours[hairColourIndex]);
		drawHair = drawItems = true;

		startClass = StartClass.getClasses().get(0);

		txtTitle = new WindowComponent("txtTitle");
		txtTitle.setText("Character Editor");
		txtTitle.getPaint().setTextSize(Values.FONT_SIZE_LARGE);
		txtTitle.setX(640);
		txtTitle.setY(96);
		add(txtTitle);

		txtNameLabel = new WindowComponent("txtNameLabel");
		txtNameLabel.setText("Name:");
		txtNameLabel.getPaint().setTextSize(Values.FONT_SIZE_SMALL);
		txtNameLabel.getPaint().setTextAlign(Align.RIGHT);
		txtNameLabel.setX(128f);
		txtNameLabel.setY(txtTitle.getBounds().getY() + txtTitle.getPaint().getTextSize() + 16f);
		add(txtNameLabel);

		txtName = new WindowTextBoxComponent();
		txtName.setName("txtName");
		txtName.setX(txtNameLabel.getBounds().getX() + 16f);
		txtName.setY(txtNameLabel.getBounds().getY() - 32f);
		txtName.setWidth(300f);
		txtName.setHeight(48f);
		txtName.getPaint().setTextSize(Values.FONT_SIZE_SMALL);
		add(txtName);

		btnPrevStartClass = new WindowComponent("btnPrevStartClass");
		btnPrevStartClass.setBackground(WindowComponent.STATE_IDLE, "gui/scroll_left.png");
		btnPrevStartClass.setBackground(WindowComponent.STATE_DOWN, "gui/scroll_left_selected.png");
		btnPrevStartClass.setBounds(txtNameLabel.getBounds().getX() - 88f, txtName.getBounds().getY() + txtName.getPaint().getTextSize() + 64f - 32f, 64, 64);
		btnPrevStartClass.addListener(new WindowListener(){
			@Override
			public void touchUp(Game game, WindowComponent component){
				int cIndex = StartClass.getClasses().indexOf(startClass);
				if(cIndex == 0)
					cIndex = StartClass.getClasses().size() - 1;
				else
					cIndex--;
				startClass = StartClass.getClasses().get(cIndex);
				onStartClassChanged();
			}
		});

		txtStartClass = new WindowComponent("txtStartClass");
		txtStartClass.getPaint().setTextSize(Values.FONT_SIZE_SMALL);
		txtStartClass.getPaint().setTextAlign(Align.CENTER);
		txtStartClass.setX(btnPrevStartClass.getBounds().getX() + 144f + 64f);
		txtStartClass.setY(btnPrevStartClass.getBounds().getY() + 32f);
		add(txtStartClass);
		add(btnPrevStartClass);

		btnNextStartClass = new WindowComponent("btnNextStartClass");
		btnNextStartClass.setBackground(WindowComponent.STATE_IDLE, "gui/scroll_right.png");
		btnNextStartClass.setBackground(WindowComponent.STATE_DOWN, "gui/scroll_right_selected.png");
		btnNextStartClass.setBounds(txtStartClass.getBounds().getX() + 144f, btnPrevStartClass.getBounds().getY(), 64, 64);
		btnNextStartClass.addListener(new WindowListener(){
			@Override
			public void touchUp(Game game, WindowComponent component){
				int cIndex = StartClass.getClasses().indexOf(startClass);
				if(cIndex + 1 >= StartClass.getClasses().size())
					cIndex = 0;
				else
					cIndex++;
				startClass = StartClass.getClasses().get(cIndex);
				onStartClassChanged();
			}
		});
		add(btnNextStartClass);

		txtStartClassDesc = new WindowComponent("txtStartClassDesc");
		txtStartClassDesc.setWidth(420);
		txtStartClassDesc.getPaint().setTextSize(Values.FONT_SIZE_TINY);
		txtStartClassDesc.getPaint().setTextAlign(Align.LEFT);
		txtStartClassDesc.setFlag(WindowComponent.FLAG_WRAPPED_TEXT, true);
		txtStartClassDesc.setX(btnPrevStartClass.getBounds().getX() + 4f);
		txtStartClassDesc.setY(btnNextStartClass.getBounds().getY() + btnNextStartClass.getBounds().getHeight() + 16f);
		add(txtStartClassDesc);

		btnPrevPlayerColour = new WindowComponent("btnPrevPlayerColour");
		btnPrevPlayerColour.setBackground(WindowComponent.STATE_IDLE, "gui/scroll_left.png");
		btnPrevPlayerColour.setBackground(WindowComponent.STATE_DOWN, "gui/scroll_left_selected.png");
		btnPrevPlayerColour.setBounds(txtName.getBounds().getX() + txtName.getBounds().getWidth() + 32f, txtName.getBounds().getY() - 8f, 64, 64);
		btnPrevPlayerColour.addListener(new WindowListener(){
			@Override
			public void touchUp(Game game, WindowComponent component){
				if(skinIndex == 0)
					skinIndex = skinColours.length - 1;
				else
					skinIndex--;
				onSkinChanged();
			}
		});
		add(btnPrevPlayerColour);

		txtPlayerColour = new WindowComponent("txtPlayerColour");
		txtPlayerColour.getPaint().setTextSize(Values.FONT_SIZE_SMALL);
		txtPlayerColour.getPaint().setTextAlign(Align.CENTER);
		txtPlayerColour.setX(btnPrevPlayerColour.getBounds().getX() + 144f + 64f);
		txtPlayerColour.setY(txtNameLabel.getBounds().getY());
		add(txtPlayerColour);

		btnNextPlayerColour = new WindowComponent("btnNextPlayerColour");
		btnNextPlayerColour.setBackground(WindowComponent.STATE_IDLE, "gui/scroll_right.png");
		btnNextPlayerColour.setBackground(WindowComponent.STATE_DOWN, "gui/scroll_right_selected.png");
		btnNextPlayerColour.setBounds(txtPlayerColour.getBounds().getX() + 144f, btnPrevPlayerColour.getBounds().getY(), 64, 64);
		btnNextPlayerColour.addListener(new WindowListener(){
			@Override
			public void touchUp(Game game, WindowComponent component){
				if(skinIndex + 1 >= skinColours.length)
					skinIndex = 0;
				else
					skinIndex++;
				onSkinChanged();
			}
		});
		add(btnNextPlayerColour);

		btnPrevHair = new WindowComponent("btnPrevHair");
		btnPrevHair.setBackground(WindowComponent.STATE_IDLE, "gui/scroll_left.png");
		btnPrevHair.setBackground(WindowComponent.STATE_DOWN, "gui/scroll_left_selected.png");
		btnPrevHair.setBounds(btnPrevPlayerColour.getBounds().getX(), btnPrevStartClass.getBounds().getY(), 64, 64);
		btnPrevHair.addListener(new WindowListener(){
			@Override
			public void touchUp(Game game, WindowComponent component){
				if(hairId == 0)
					hairId = Hair.MAX_ID;
				else
					hairId--;
				onHairChanged();
			}
		});
		add(btnPrevHair);

		txtPlayerHairLabel = new WindowComponent("txtPlayerHairLabel");
		txtPlayerHairLabel.getPaint().setTextSize(Values.FONT_SIZE_SMALL);
		txtPlayerHairLabel.getPaint().setTextAlign(Align.CENTER);
		txtPlayerHairLabel.setX(txtPlayerColour.getBounds().getX());
		txtPlayerHairLabel.setY(txtStartClass.getBounds().getY());
		add(txtPlayerHairLabel);

		btnNextHair = new WindowComponent("btnNextHair");
		btnNextHair.setBackground(WindowComponent.STATE_IDLE, "gui/scroll_right.png");
		btnNextHair.setBackground(WindowComponent.STATE_DOWN, "gui/scroll_right_selected.png");
		btnNextHair.setBounds(txtPlayerHairLabel.getBounds().getX() + 144f, btnPrevHair.getBounds().getY(), 64, 64);
		btnNextHair.addListener(new WindowListener(){
			@Override
			public void touchUp(Game game, WindowComponent component){
				if(hairId == Hair.MAX_ID)
					hairId = 0;
				else
					hairId++;
				onHairChanged();
			}
		});
		add(btnNextHair);

		btnPrevHairColour = new WindowComponent("btnPrevHairColour");
		btnPrevHairColour.setBackground(WindowComponent.STATE_IDLE, "gui/scroll_left.png");
		btnPrevHairColour.setBackground(WindowComponent.STATE_DOWN, "gui/scroll_left_selected.png");
		btnPrevHairColour.setBounds(btnPrevPlayerColour.getBounds().getX(), btnPrevHair.getBounds().getY() + btnPrevHair.getBounds().getHeight() + 8f, 64, 64);
		btnPrevHairColour.addListener(new WindowListener(){
			@Override
			public void touchUp(Game game, WindowComponent component){
				if(hairColourIndex == 0)
					hairColourIndex = hairColours.length - 1;
				else
					hairColourIndex--;
				onHairChanged();
			}
		});
		add(btnPrevHairColour);

		txtPlayerHairColour = new WindowComponent("txtPlayerHairColour");
		txtPlayerHairColour.getPaint().setTextSize(Values.FONT_SIZE_SMALL);
		txtPlayerHairColour.getPaint().setTextAlign(Align.CENTER);
		txtPlayerHairColour.setX(txtPlayerColour.getBounds().getX());
		txtPlayerHairColour.setY(txtPlayerHairLabel.getBounds().getY() + txtPlayerHairLabel.getPaint().getTextSize() + 32f);
		add(txtPlayerHairColour);

		btnNextHairColour = new WindowComponent("btnNextHairColour");
		btnNextHairColour.setBackground(WindowComponent.STATE_IDLE, "gui/scroll_right.png");
		btnNextHairColour.setBackground(WindowComponent.STATE_DOWN, "gui/scroll_right_selected.png");
		btnNextHairColour.setBounds(btnNextPlayerColour.getBounds().getX(), btnPrevHairColour.getBounds().getY(), 64, 64);
		btnNextHairColour.addListener(new WindowListener(){
			@Override
			public void touchUp(Game game, WindowComponent component){
				if(hairColourIndex + 1 == hairColours.length)
					hairColourIndex = 0;
				else
					hairColourIndex++;
				onHairChanged();
			}
		});
		add(btnNextHairColour);

		playerPreview = new WindowComponent("playerPreview"){
			@Override
			public void draw(Game game, Canvas canvas){
				super.draw(game, canvas);
				previewPlayer.drawEquipmentOverlay = drawItems;
				if(drawHair)
					previewPlayer.setEquipped(ItemEquippable.SLOT_HAIR, new ItemStack(previewHair, 1));
				else
					previewPlayer.setEquipped(ItemEquippable.SLOT_HAIR, new ItemStack(nullHair, 1));
				previewPlayer.setX(this.getScreenX() + 64f);
				previewPlayer.setY(this.getScreenY() + 64f);
				previewPlayer.setWidth(128f);
				previewPlayer.setHeight(128f);
				previewPlayer.draw(game, canvas);
			}
		};
		playerPreview.setBackgroundDefault("gui/generic_black.png");
		playerPreview.setBounds(txtPlayerHairColour.getBounds().getX() - 128f, btnPrevHairColour.getBounds().getY() + btnPrevHairColour.getBounds().getHeight() + 16f, 256f, 256f);
		add(playerPreview);
		
		txtStats = new WindowComponent("txtStats");
		txtStats.setX(btnNextPlayerColour.getBounds().getX() + btnNextPlayerColour.getBounds().getWidth() + 32f);
		txtStats.setY(btnNextPlayerColour.getBounds().getY());
		txtStats.getPaint().setTextSize(Values.FONT_SIZE_SMALL + 8f);
		txtStats.getPaint().setTextAlign(Align.LEFT);
		txtStats.setFlag(WindowComponent.FLAG_MULTILINE_TEXT, true);
		add(txtStats);

		txtStatsValues = new WindowComponent("txtStatsValues");
		txtStatsValues.setX(btnNextPlayerColour.getBounds().getX() + btnNextPlayerColour.getBounds().getWidth() + 320f);
		txtStatsValues.setY(btnNextPlayerColour.getBounds().getY());
		txtStatsValues.getPaint().setTextSize(Values.FONT_SIZE_SMALL + 8f);
		txtStatsValues.getPaint().setTextAlign(Align.RIGHT);
		txtStatsValues.setFlag(WindowComponent.FLAG_MULTILINE_TEXT, true);
		add(txtStatsValues);
		
		btnConfirm = new WindowComponent("btnConfirm");
		btnConfirm.setBackground(WindowComponent.STATE_IDLE, "gui/button.png");
		btnConfirm.setBackground(WindowComponent.STATE_DOWN, "gui/button_selected.png");
		btnConfirm.setWidth(300f);
		btnConfirm.setHeight(72f);
		btnConfirm.setX(txtStats.getBounds().getX());
		btnConfirm.setY(getBounds().getHeight() - btnConfirm.getBounds().getHeight() - 32f);
		btnConfirm.setText("Confirm");
		btnConfirm.getPaint().setTextSize(Values.FONT_SIZE_SMALL + 4f);
		btnConfirm.addListener(new WindowListener(){
			@Override
			public void touchUp(final Game game, WindowComponent component){
				WindowPlayerEditor.this.hide();
				if(TextUtils.isEmpty(txtName.getText())){
					game.getHelper().dialog("Please enter your name.", GameData.EMPTY_STRING_ARRAY, new DialogCallback(){
						@Override
						public void onSelected(int option){
							WindowPlayerEditor.this.show();
						}
					});
					return;
				}
				game.getHelper().dialog("Are you sure this is how you want your character?", Strings.Dialog.YES_NO, new DialogCallback(){
					@Override
					public void onSelected(int option){
						if(option == 0){
							EntityPlayer player = new EntityPlayer();
							startClass.apply(player);
							player.setEquipped(ItemEquippable.SLOT_HAIR, new ItemStack(previewHair, 1));
							player.setResource(previewPlayer.getResource());
							player.setUsername(txtName.getText());
							game.setPlayer(player);
							WindowPlayerEditor.this.close();
							game.getScript().runScript(game, "script/init/nameset.js");
						}else
							WindowPlayerEditor.this.show();
					}
				});
			}
		});
		add(btnConfirm);

		onStartClassChanged();
		onSkinChanged();
		onHairChanged();
	}

	public void onStartClassChanged(){
		txtStartClass.setText(startClass.getShowName());
		txtStartClassDesc.setText(startClass.getDescritpion() + "\n\nAbility: " + startClass.getAbilityDescription());
		previewEquipped = startClass.getStartGearEquip();
		startClass.apply(previewPlayer);
		previewPlayer.setMoveState(MovementState.WALK);
		EntityStats stats = startClass.getStats();
		txtStats.setText("Player Stats\n"
				+ "LVL\n"
				+ "HP\n"
				+ "MP\n"
				+ "\n"
				+ "SPD\n"
				+ "STR\n"
				+ "DEF\n"
				+ "LCK\n"
				+ "MGK\n"
				+ "FRG\n");
		txtStatsValues.setText("\n"
				+ stats.getLevel() + "\n"
				+ SuperCalc.getMaxHealth(previewPlayer) + "\n"
				+ SuperCalc.getMaxMagika(previewPlayer) + "\n"
				+ "\n"
				+ Util.getStatInt(stats.getSpeed()) + "\n"
				+ Util.getStatInt(stats.getStrength()) + "\n"
				+ Util.getStatInt(stats.getDefence()) + "\n"
				+ Util.getStatInt(stats.getLuck()) + "\n"
				+ Util.getStatInt(stats.getMagika()) + "\n"
				+ Util.getStatInt(stats.getForge()));
	}

	public void onSkinChanged(){
		txtPlayerColour.setText("Skin: " + GrammarUtil.capitalise(skinColours[skinIndex]));
		previewPlayer.setResource("character/m/base/" + skinColours[skinIndex] + ".spr");
	}

	public void onHairChanged(){
		txtPlayerHairLabel.setText("Hair: " + hairId);
		txtPlayerHairColour.setText("Hair Colour: " + GrammarUtil.capitalise(hairColours[hairColourIndex]));
		previewHair = new Hair();
		previewHair.setId(hairId);
		previewHair.setColor(hairColours[hairColourIndex]);
	}
}
