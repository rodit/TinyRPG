#TinyRPG TODO

#####VITAL
- Identify and fix memory leaks!
  - These lead to crashes after a few minutes of the game being open.

#####Forge System
- Fix forge registry so upgrade window shows possible upgrades.
- Add infusion system.
- Change individual forge window to be bigger/appropriate size.

#####Shops
- ~~Fix shop system:~~
  - ~~Fix money checks for client and vendor.~~
  - ~~Actually change money when buying/selling goods.~~
  - ~~Disallow dropping of items in shop window.~~
- ~~Add entity info to shop windows.~~

#####Inventory
- ~~Disallow dropping of items which are equipped.~~
- ~~Fix item dupe glitch where player unequips and drops item.~~
- ~~Fix bug where items do not merge into one bag when dropped on same tile (trace bounds vs collision bounds).~~
- ~~Fix bug where second ring replaces first when there is an open slot.~~

#####Battles
- Fix bug where multiple dialogs appear when returning from inventory window without taking action.
- Fix poor performance (low fps) in battles.
- Fix position of health units over participants.
- Fix player drawing size.
- Introduce battle with dialog before it starts.
- Make enemies fade rather than disappear.
- Make enemies flash when hit.

#####Menu
- Fix crash when returning to title screen from in game menu.
- Ensure game does not reset (return to menu) when activity is paused/resumed.

#####Features
- Implement world map and local map.

#####Sound
- Add sounds for:
  - Battles (weapons/special attacks/hits etc)
  - GUIs/windows.