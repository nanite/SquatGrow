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

The configuration file is located at `config/squatgrow-common.json5` and is generated when you first run the game with the mod installed.

### Options

#### Example Configuration

```json5
{
  /*
   * AE2 growth multiplier, only available if AE2 is present
   * Value must be between 1 and 16
   */
  ae2Multiplier: 4,
  // Requirements for growing
  requirements: {
    /*
     * The chance for the required item to take durability damage when used to grow a block, between 0 and 1. This is only used if durabilityCost is greater than 0.
     * Value must be between 0.0 and 1.0
     */
    durabilityChance: 0.01,
    // List of enchantments required on the item to grow, leave empty to disable, format is <enchantment_id>@<level>, for example minecraft:fortune@3 or, optionally, omit the level to just check for the presence of the enchantment minecraft:fortune
    enchants: [
      "minecraft:fortune@3",
    ],
    // Whether the required item should take durability damage when used to grow a block. If the item is not durable, this will have no effect. Leave as 0 to disable durability damage.

    durabilityCost: 1,
    // List of items required to grow, leave empty to disable, can be either item ids or tags (use #minecraft:<tag_name> or #modid:<tag_name>)
    items: [
      "minecraft:diamond_hoe",
    ],
  },
  // Enable debug logging
  debug: false,
  /*
   * Chance for a block to grow, between 0 and 1
   * Value must be between 0.0 and 1.0
   */
  chance: 1.0,
  // Use whitelist instead of blacklist, default false
  useWhitelist: false,
  // Allow twerking in adventure mode, default true
  allowAdventureTwerking: true,
  // List of blocks to blacklist/whitelist from twerking, Tags can be used by using #minecraft:<tag_name> or #modid:<tag_name>
  ignoreList: [
    "minecraft:grass_block",
    "minecraft:short_grass",
    "minecraft:tall_grass",
    "minecraft:netherrack",
    "minecraft:warped_nylium",
    "minecraft:crimson_nylium",
  ],
  /*
   * Range of effect, warning: this can cause lag if set too high
   * Value must be between 0 and 16
   */
  range: 3,
  // Enable Mystical Crops growth support
  enableMysticalCrops: true,
  // When the player is holding a grass block in their offhand, they will be able to randomly convert dirt into grass
  enableDirtToGrass: true,
  /*
   * Random tick multiplier, this is the amount of times the mod will call the randomTick method on the block for each block in the range
   * Value must be between 1 and 16
   */
  randomTickMultiplier: 4,
  // Enable AE2 crystal growth support, only available if AE2 is present
  enableAE2Accelerator: true,
}
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
| `useWhitelist` | Whether or not to use a whitelist or blacklist for blocks to grow                                        | `false`   |
| `ignoreList` | A list of blocks to ignore when growing crops (When whitelist is enabled, this is used as the whitelist) | See above |

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
