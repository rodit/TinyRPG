**Bug Fixes/Minor Additions**
- Menu GUI:
	- Play button position and size.
	- Saves button position and size.
	- Add continue from last save button.
	- Change play button to new game button.
	- Add background picture.

- Saves GUI:
	- Fix scrolling so entire page does not scroll.
	- Increase number of saves per screen to 15.
	- Fix size of box around save names to prevent overlapping adjacent buttons.
	- Show loading GUI when loading save.
	- Hide saves GUI and show ingame GUI after loading save.
	- Add working confirmation when deleting save (not dialog with no controls).
	- **Fix crash when scrolling with no saves.**

- Dialogues:
	- Fix option and selectd marker positioning.

- Set Name GUI:
	- Fix textbox text size.
	- Fix confirm button size.

- Inventory GUI:
	- Fix only text disappearing when unequipping item from equipped slot.
	- MORE BUGS - NEED TO BE FOUND.

- Ingame GUI:
	- Fix health/magika bar lengths/positions.
	- Add text to show gold.
	- Add text to show level.
	- Add XP bar (purple).

- World:
	- Fix entity retention/duplicate entity spawn when switching between maps.
	- Fix RenderOnTop layer bad positioning.
	- Scripts:
		- Fix barrels/predefined loot spawns to support all types of random spawning (food, items etc.).

**Major/Content Additions**
- NPCS:
	- Blacksmith Merek:
		- Add upgrade GUI.
		- Add infuse GUI.
		- Add shop GUI (purchase/sell items).

- General:
	- Containers:
		- Add container GUI (add/remove items).
	- Equipment:
		- Add support for ItemStack in inventory to allow multiple equipment of same item.

-Add more items/content...

**Misc**
- Pixel precise collision detection - ignoring transparency.
