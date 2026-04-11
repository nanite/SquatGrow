package dev.wuffs.squatgrow.config;

import dev.nanite.library.core.config.Config;
import dev.nanite.library.core.config.ConfigValueGroup;
import dev.nanite.library.core.config.values.BooleanConfigValue;
import dev.nanite.library.core.config.values.FloatConfigValue;
import dev.nanite.library.core.config.values.IntConfigValue;
import dev.nanite.library.core.config.values.ListConfigValue;
import dev.wuffs.squatgrow.SquatGrow;
import net.minecraft.world.level.block.Blocks;

import java.util.List;
import java.util.stream.Stream;

public interface SquatGrowConfig {
    Config config = Config.commonConfig(SquatGrow.MOD_ID);

    BooleanConfigValue debug = config
            .booleanValue("debug", false)
            .comments("Enable debug logging");

    BooleanConfigValue useWhitelist = config
            .booleanValue("useWhitelist", false)
            .comments("Use whitelist instead of blacklist, default false");

    IntConfigValue range = config
            .intValue("range", 3)
            .comments("Range of effect, warning: this can cause lag if set too high")
            .min(0)
            .max(16);

    FloatConfigValue chance = config
            .floatValue("chance", 0.5f)
            .comments("Chance for a block to grow, between 0 and 1")
            .min(0f)
            .max(1f);

    IntConfigValue randomTickMultiplier = config
            .intValue("randomTickMultiplier", 4)
            .comments("Random tick multiplier, this is the amount of times the mod will call the randomTick method on the block for each block in the range")
            .min(1)
            .max(16);

    // TODO: Fix casting issue in Nanite Library
    ListConfigValue<String> ignoreList = (ListConfigValue<String>) config
            .stringListValue("ignoreList", Stream.of(
                    Blocks.GRASS_BLOCK,
                    Blocks.SHORT_GRASS,
                    Blocks.TALL_GRASS,
                    Blocks.NETHERRACK,
                    Blocks.WARPED_NYLIUM,
                    Blocks.CRIMSON_NYLIUM
            ).map(e -> e.builtInRegistryHolder().key().identifier().toString()).toList())
            .comments("List of blocks to blacklist/whitelist from twerking, Tags can be used by using #minecraft:<tag_name> or #modid:<tag_name>");

    BooleanConfigValue allowAdventureTwerking = config
            .booleanValue("allowAdventureTwerking", true)
            .comments("Allow twerking in adventure mode, default true");

    BooleanConfigValue enableMysticalCrops = config
            .booleanValue("enableMysticalCrops", true)
            .comments("Enable Mystical Crops growth support");

    BooleanConfigValue enableAE2Accelerator = config
            .booleanValue("enableAE2Accelerator", true)
            .comments("Enable AE2 crystal growth support, only available if AE2 is present");

    IntConfigValue ae2Multiplier = config
            .intValue("ae2Multiplier", 4)
            .comments("AE2 growth multiplier, only available if AE2 is present")
            .min(1)
            .max(16);

    BooleanConfigValue enableDirtToGrass = config
            .booleanValue("enableDirtToGrass", true)
            .comments("When the player is holding a grass block in their offhand, they will be able to randomly convert dirt into grass");

    ConfigValueGroup requirementsGroup = config
            .group("requirements")
            .comments("Requirements for growing");

    // TODO: Fix casting issue in Nanite Library
    ListConfigValue<String> requiredItem = (ListConfigValue<String>) requirementsGroup
            .stringListValue("items", List.of())
            .comments(
                "List of items required to grow, leave empty to disable, can be either item ids or tags (use #minecraft:<tag_name> or #modid:<tag_name>)"
            );

    // TODO: Fix casting issue in Nanite Library
    ListConfigValue<String> requiredItemEnchants = (ListConfigValue<String>) requirementsGroup
            .stringListValue("enchants", List.of())
            .comments("List of enchantments required on the item to grow, leave empty to disable, format is <enchantment_id>@<level>, for example minecraft:fortune@3 or, optionally, omit the level to just check for the presence of the enchantment minecraft:fortune");

    IntConfigValue durabilityCost = requirementsGroup
            .intValue("durabilityCost", 0)
            .comments("Whether the required item should take durability damage when used to grow a block. If the item is not durable, this will have no effect. Leave as 0 to disable durability damage.");

    FloatConfigValue durabilityChance = requirementsGroup
            .floatValue("durabilityChance", 1f)
            .min(0f)
            .max(1f)
            .comments("The chance for the required item to take durability damage when used to grow a block, between 0 and 1. This is only used if durabilityCost is greater than 0.");
}
