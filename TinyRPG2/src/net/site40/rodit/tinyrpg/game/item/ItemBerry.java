package net.site40.rodit.tinyrpg.game.item;

import net.site40.rodit.util.Util;

import org.w3c.dom.Element;

public class ItemBerry extends Item{

	private long growInterval;
	private String plantResource;
	
	public ItemBerry(){
		super();
	}
	
	public long getGrowInterval(){
		return growInterval;
	}
	
	public String getPlantResource(){
		return plantResource;
	}
	
	@Override
	public void deserializeXmlElement(Element e){
		super.deserializeXmlElement(e);
		this.growInterval = Util.tryGetLong(e.getAttribute("growInterval"), growInterval);
		this.plantResource = e.getAttribute("plantResource");
	}
}
