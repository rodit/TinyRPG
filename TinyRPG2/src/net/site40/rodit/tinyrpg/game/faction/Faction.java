package net.site40.rodit.tinyrpg.game.faction;

import java.util.ArrayList;

import net.site40.rodit.tinyrpg.game.Game;
import net.site40.rodit.tinyrpg.game.entity.EntityLiving;
import net.site40.rodit.tinyrpg.game.entity.EntityStats;
import net.site40.rodit.tinyrpg.game.faction.FactionOffering.FactionReward;
import net.site40.rodit.tinyrpg.game.faction.FactionOffering.FactionReward.RewardType;
import net.site40.rodit.tinyrpg.game.item.Item;
import net.site40.rodit.tinyrpg.game.item.ItemStack;

public class Faction {

	private static ArrayList<Faction> registry = new ArrayList<Faction>();

	public static void register(Faction faction){
		if(!registry.contains(faction))
			registry.add(faction);
	}
	
	public static void unregister(Faction faction){
		registry.remove(faction);
	}
	
	public static Faction get(String name){
		for(Faction faction : registry)
			if(faction.getName().equals(name))
				return faction;
		return null;
	}
	
	public static final FactionNone NONE = new FactionNone();
	
	static{
		register(NONE);
		register(new FactionLight());
		register(new FactionDark());
		register(new FactionMagicLight());
		register(new FactionMagicDark());
		register(new FactionHunters());
		register(new FactionThieves());
		register(new FactionChampions());
		register(new FactionGrass());
	}

	protected String name;
	protected String displayName;
	protected String description;
	protected String resource;
	protected EntityStats minStats;
	protected ArrayList<FactionOffering> offerings;

	public Faction(){
		this("", "", "", "");
	}

	public Faction(String name, String displayName, String description, String resource){
		this(name, displayName, description, resource, new EntityStats());
	}

	public Faction(String name, String displayName, String description, String resource, EntityStats minStats){
		this.name = name;
		this.displayName = displayName;
		this.description = description;
		this.resource = resource;
		this.minStats = minStats;
		this.offerings = new ArrayList<FactionOffering>();
	}

	public String getName(){
		return name;
	}

	public void setName(String name){
		this.name = name;
	}

	public String getDisplayName(){
		return displayName;
	}

	public void setDisplayName(String displayName){
		this.displayName = displayName;
	}

	public String getDescription(){
		return description;
	}

	public void setDescription(String description){
		this.description = description;
	}

	public String getResource(){
		return resource;
	}

	public void setResource(String resource){
		this.resource = resource;
	}

	public EntityStats getMinStats(){
		return minStats;
	}

	public void setMinStats(EntityStats minStats){
		this.minStats = minStats;
	}
	
	public ArrayList<FactionOffering> getOfferings(){
		return offerings;
	}
	
	public ArrayList<FactionOffering> getAvailableOfferings(EntityLiving entity){
		ArrayList<FactionOffering> available = new ArrayList<FactionOffering>();
		for(FactionOffering offering : offerings)
			if(offering.canOffer(entity))
				available.add(offering);
		return available;
	}
	
	public void submitOffering(Game game, FactionOffering offering, EntityLiving entity){
		entity.getInventory().remove(offering.getOffering().getItem(), offering.getOffering().getAmount());
		for(FactionReward reward : offering.getRewards())
			reward.give(entity, game);
	}
	
	public void addOffering(FactionOffering offering){
		offerings.add(offering);
	}

	public boolean canJoin(EntityLiving entity){
		return entity.getStats().compare(minStats) >= 0;
	}
	
	public static class FactionNone extends Faction{
		
		public FactionNone(){
			super("none", "No faction.", "", "");
		}
		
		@Override
		public boolean canJoin(EntityLiving entity){
			return true;
		}
	}

	public static class FactionLight extends Faction{

		public static final int MIN_HUMANITY = 20;

		public FactionLight(){
			super("light", "Defenders of Light", "No description.", "faction/light.png");
			this.minStats.setLevel(30);
			this.minStats.setDefence(3f);
			this.minStats.setStrength(2.5f);
			this.minStats.setForge(2f);
			addOffering(FactionOffering.builder().setOffering(new ItemStack(Item.get("humanity_box_light"), 1))
					.addReward(RewardType.GOLD, 10000)
					.setMinFactionLevel(5)
					.setMaxFactionLevel(100)
					.build());
			addOffering(FactionOffering.builder().setOffering(new ItemStack(Item.get("gold_crest"), 1))
					.addReward(RewardType.FACTION_LEVEL, 1)
					.setMinFactionLevel(0)
					.setMaxFactionLevel(19)
					.build());
			addOffering(FactionOffering.builder().setOffering(new ItemStack(Item.get("gold_crest"), 1))
					.addReward(RewardType.FACTION_LEVEL, 1)
					.addReward(RewardType.ITEM, new ItemStack(Item.get("scroll_light_weapon"), 1))
					.setMinFactionLevel(19)
					.setMaxFactionLevel(19)
					.build());
		}

		@Override
		public boolean canJoin(EntityLiving entity){
			return super.canJoin(entity) && entity.getHumanity() >= MIN_HUMANITY;
		}
	}

	public static class FactionDark extends Faction{

		public static final int MAX_HUMANITY = -20;

		public FactionDark(){
			super("dark", "Mercaneries of Darkness", "No description.", "faction/dark.png");
			this.minStats.setLevel(30);
			this.minStats.setDefence(2.5f);
			this.minStats.setStrength(3f);
			this.minStats.setForge(2f);
			addOffering(FactionOffering.builder().setOffering(new ItemStack(Item.get("humanity_box_dark"), 1))
					.addReward(RewardType.GOLD, 10000)
					.setMinFactionLevel(5)
					.setMaxFactionLevel(100)
					.build());
			addOffering(FactionOffering.builder().setOffering(new ItemStack(Item.get("dark_crest"), 1))
					.addReward(RewardType.FACTION_LEVEL, 1)
					.setMinFactionLevel(0)
					.setMaxFactionLevel(19)
					.build());
			addOffering(FactionOffering.builder().setOffering(new ItemStack(Item.get("dark_crest"), 1))
					.addReward(RewardType.FACTION_LEVEL, 1)
					.addReward(RewardType.ITEM, new ItemStack(Item.get("scroll_dark_weapon"), 1))
					.setMinFactionLevel(19)
					.setMaxFactionLevel(19)
					.build());
		}

		@Override
		public boolean canJoin(EntityLiving entity){
			return super.canJoin(entity) && entity.getHumanity() <= MAX_HUMANITY;
		}
	}

	public static class FactionMagicLight extends Faction{

		public static final int MIN_HUMANITY = 5;

		public FactionMagicLight(){
			super("magic_light", "Guild of Wizards", "No description.", "faction/magic_light.png");
			this.minStats.setLevel(10);
			this.minStats.setMagika(3f);
		}

		@Override
		public boolean canJoin(EntityLiving entity){
			return super.canJoin(entity) && entity.getHumanity() >= MIN_HUMANITY;
		}
	}

	public static class FactionMagicDark extends Faction{

		public static final int MAX_HUMANITY = -5;

		public FactionMagicDark(){
			super("magic_dark", "Cult of Warlocks", "No description.", "faction/magic_dark.png");
			this.minStats.setLevel(10);
			this.minStats.setMagika(3f);
		}

		@Override
		public boolean canJoin(EntityLiving entity){
			return super.canJoin(entity) && entity.getHumanity() <= MAX_HUMANITY;
		}
	}

	public static class FactionHunters extends Faction{

		public FactionHunters(){
			super("hunters", "Old Hunters", "No description.", "faction/hunters.png");
			this.minStats.setLevel(20);
			this.minStats.setSpeed(3f);
			this.minStats.setLuck(2.7f);
		}
	}

	public static class FactionThieves extends Faction{

		public static final int MAX_HUMANITY = -50;

		public FactionThieves(){
			super("thieves_dark", "Brotherhood of Thieves", "No description.", "faction/thieves_dark.png");
			this.minStats.setLevel(60);
			this.minStats.setSpeed(3.5f);
			this.minStats.setLuck(4f);
		}

		@Override
		public boolean canJoin(EntityLiving entity){
			return super.canJoin(entity) && entity.getHumanity() <= MAX_HUMANITY;
		}
	}

	public static class FactionChampions extends Faction{

		public FactionChampions(){
			super("champions", "Company of Champions", "No description.", "faction/champions.png");
			this.minStats.setLevel(15);
			this.minStats.setSpeed(2f);
			this.minStats.setStrength(2f);
			this.minStats.setDefence(2f);
			this.minStats.setLuck(1f);
			this.minStats.setForge(1.8f);
		}
	}
	
	public static class FactionGrass extends Faction{
		
		public FactionGrass(){
			super("grass", "Protectors of Grass", "No description.", "faction/grass.png");
			this.minStats.setLevel(5);
			this.minStats.setSpeed(1f);
			this.minStats.setStrength(0.5f);
			this.minStats.setDefence(0.8f);
			this.minStats.setLuck(0.9f);
			this.minStats.setMagika(0.5f);
		}
	}
}
