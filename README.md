# SquatGrow

SquatGrow is a mod that allows you to grow crops by squatting on them in a highly configurable way.

[![CurseForge](https://cf.way2muchnoise.eu/515698.svg)](https://www.curseforge.com/minecraft/mc-mods/squat-grow)
[![CurseForge](https://cf.way2muchnoise.eu/versions/515698.svg)](https://www.curseforge.com/minecraft/mc-mods/squat-grow)
[![Modrinth](https://img.shields.io/modrinth/dt/b5JMdB5V)](https://modrinth.com/mod/squat-grow)
[![Modrinth](https://img.shields.io/modrinth/game-versions/b5JMdB5V)](https://modrinth.com/mod/squat-grow)

> Feeling weak in Minecraft and not getting enough gains? Well throw away your bonemeal and start squatting!
> 
> With Squat Grow your plant will grow twice as fast and feel the gains!
> 
> All you need to do is press your sneak key until you are happy with the growth of your plant.
> 
> You can configure the plants affected using the registry name of the plant or using tags, there is also an option to use a blacklist or a whitelist along with changing the effect range

Get the mod on [CurseForge](https://www.curseforge.com/minecraft/mc-mods/squat-grow) or [Modrinth](https://modrinth.com/mod/squat-grow)

## Features

- Grow crops by squatting on them
- Configurable growth chances
- Configurable growth multipliers
- Configurable requirements for growth
- Sort of API for other mods to add custom squat actions
  - You can see an example of this via the `actions.special.DirtToGrassAction` class
- Special actions for certain blocks
  - Grass block in offhand to quickly convert dirt to grass
- Mod integration
  - AE2 Crystals
  - Mystical Agriculture Crops

## Dependencies

- [Architectury](https://www.curseforge.com/minecraft/mc-mods/architectury-api)
- [Cloth Config](https://www.curseforge.com/minecraft/mc-mods/cloth-config)

## Configuration

The configuration file is located at `config/squatgrow-common.yaml` and is generated when you first run the game with the mod installed.

### Options

#### Example Configuration

```yaml
ae2Multiplier: 4
allowAdventureTwerking: true
chance: 1.0
debug: false
enableAE2Accelerator: true
enableDirtToGrass: true
enableMysticalCrops: true
hoeTakesDamage: false
ignoreList: ['minecraft:grass_block', 'minecraft:grass', 'minecraft:tall_grass', 'minecraft:netherrack', 'minecraft:warped_nylium', 'minecraft:crimson_nylium']
randomTickMultiplier: 4
range: 16
requireHoe: false
requirements:
    durabilityDamage: 1
    enabled: false
    equipmentRequirement:
      HEAD: 'minecraft:iron_helmet'
    heldItemRequirement: ["minecraft:diamond_hoe", "minecraft:golden_hoe"]
    requiredEnchantment: ''
    requiredItemTakesDamage: false
sugarcaneMultiplier: 4
useWhitelist: false
```

#### Options

| Option | Description                                                                                              | Default   |
| ------ |----------------------------------------------------------------------------------------------------------|-----------|
| `chance` | The chance for growth to occur                                                                           | `1.0`     |
| `allowAdventureTwerking` | Whether or not to allow adventure twerking                                                               | `true`    |
| `enableDirtToGrass` | Whether or not to enable dirt to grass conversion when a grass block is in your offhand                  | `true`    |
| `enableMysticalCrops` | Whether or not to enable mystical agriculture crop growth                                                | `true`    |
| `sugarcaneMultiplier` | The multiplier for sugarcane growth                                                                      | `4`       |
| `randomTickMultiplier` | The multiplier for random ticks for blocks that grow via random ticks                                    | `4`       |
| `range` | The range in which to check for blocks to grow                                                           | `16`      |
| `debug` | Whether or not to enable debug mode                                                                      | `false`   |
| `requireHoe` | Whether or not to require a hoe to be held to grow crops (Depercated, please do not use)                 | `false`   |
| `hoeTakesDamage` | Whether or not to take damage when using a hoe to grow crops (Depercated, please do not use)             | `false`   |
| `useWhitelist` | Whether or not to use a whitelist or blacklist for blocks to grow                                        | `false`   |
| `ignoreList` | A list of blocks to ignore when growing crops (When whitelist is enabled, this is used as the whitelist) | See above |

### Requirements

The requirements system is a new system that allows you to require certain items to be held or equipment to be worn to grow crops.

| Option | Description                                                                                              | Default   |
| ------ |----------------------------------------------------------------------------------------------------------|-----------|
| `requirements` | The requirements system for growing crops                                                               | See above |
| `requirements.enabled` | Whether or not to enable the requirements system                                                        | `false`   |
| `requirements.heldItemRequirement` | A list of items that are required to be held to grow crops                                           | `[]`      |
| `requirements.equipmentRequirement` | A map of equipment slots to items that are required to be worn to grow crops                        | `{}`      |
| `requirements.requiredEnchantment` | The enchantment that is required to grow crops                                                         | `''`      |
| `requirements.requiredItemTakesDamage` | Whether or not the required item takes damage when growing crops                                    | `false`   |
| `requirements.durabilityDamage` | The amount of damage to take when growing crops                                                        | `1`       |

##### Integration Options

| Option | Description                                                                            | Default |
| ------ |----------------------------------------------------------------------------------------| ------- |
| `enableAE2Accelerator` | Whether or not to enable AE2 growth acceleration                                       | `true` |
| `ae2Multiplier` | The multiplier for AE2 growth                                                          | `4` |
| `enableMysticalCrops` | Whether or not to enable mystical agriculture crop growth                              | `true` |

### Item special handling

For any config value that supports items, you can use the following format:

- `#<modid>:<item>` - This will be converted to an item tag upon loading the config
- `<modid>:<item>` - This will be converted to an item upon loading the config

## License

This mod is licensed under All Rights Reserved.

## Modpacks

You are free to use this mod in any modpack you want via **_CURSEFORGE_**. No need to ask for permission.

Distribution permission is given to Feed The Beast and Curseforge. You may not distribute this mod on any other platform without my permission.
