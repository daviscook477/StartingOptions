# Starting Options

## General Information
**Starting Options** is a mod for **Slay the Spire** that lets you customizer your character's loadout before setting out on your adventure each time.
You can customize exactly which cards and relics you will start out with as either the ironclad or the silent.

## Dependencies
* ModTheSpire v2.1.0+ [https://github.com/kiooeht/ModTheSpire/releases](https://github.com/kiooeht/ModTheSpire/releases)
* BaseMod v1.5.0+ [https://github.com/t-larson/BaseMod/releases](https://github.com/t-larson/BaseMod/releases)

## Installation
1. First follow the install instructions provided by **ModTheSpire** to get the modloader set up [link](https://github.com/kiooeht/ModTheSpire).
2. Next follow the install instructions provided by **BaseMod** to get the mod API set up [link](https://github.com/t-larson/BaseMod)
3. Finally just go ahead and copy the released jar for this modfile (which can be downloaded from the releases tab) into your **ModTheSpire** mods directory and you are all setup.
4. Take a look at the making loadouts section of the README next to get started Slaying the Spire with your own personalized gear setup.

## Making Loadouts
As of right now, loadouts have to be made outside of the game in the form of configuration files that you will put in your **Slay the Spire** directory.
**Slay the Spire** is probably installed at something like *SteamApps/common/SlayTheSpire/* on your computer. All your loadouts will need to be put in the
`starts/` directory. It doesn't exist yet so create it in your **Slay the Spire** folder so it now looks like *SteamApps/common/SlayTheSpire/starts*.
In the `starts/` directory you can now make loadouts using some really simple `xml` config files. If you name a loadout something like "ironclad_1.xml" it will show
up in game as "ironclad_1" when choosing between loadouts.

## Example Loadouts

### Default Ironclad
```
<ironclad>
	<card>Strike_R</card>
	<card>Strike_R</card>
	<card>Strike_R</card>
	<card>Strike_R</card>
	<card>Strike_R</card>
	<card>Defend_R</card>
	<card>Defend_R</card>
	<card>Defend_R</card>
	<card>Defend_R</card>
	<card>Bash</card>
	<relic>Burning Blood</relic>
</ironclad>
```
### Default Silent
```
<silent>
	<card>Defend_G</card>
	<card>Defend_G</card>
	<card>Defend_G</card>
	<card>Defend_G</card>
	<card>Defend_G</card>
	<card>Strike_G</card>
	<card>Strike_G</card>
	<card>Strike_G</card>
	<card>Strike_G</card>
	<card>Strike_G</card>
	<card>Survivor</card>
	<card>Neutralize</card>
	<relic>Ring of the Snake</relic>
</silent>
```