package dev.wuffs.squatgrow.config;

import com.mojang.datafixers.util.Either;
import dev.nanite.library.core.config.Config;
import dev.nanite.library.core.config.ConfigValue;
import dev.nanite.library.core.config.ConfigValueGroup;
import dev.nanite.library.core.config.values.BooleanConfigValue;
import dev.nanite.library.core.config.values.FloatConfigValue;
import dev.nanite.library.core.config.values.IntConfigValue;
import dev.wuffs.squatgrow.SquatGrow;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
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

    ConfigValue<List<Either<Identifier, TagKey<Block>>>> ignoreList = config
            .idOrTagListValue("ignoreList", Registries.BLOCK, Stream.of(
                    Blocks.GRASS_BLOCK,
                    Blocks.SHORT_GRASS,
                    Blocks.TALL_GRASS,
                    Blocks.NETHERRACK,
                    Blocks.WARPED_NYLIUM,
                    Blocks.CRIMSON_NYLIUM
            ).map(e -> Either.<Identifier, TagKey<Block>>left(e.builtInRegistryHolder().key().identifier())).toList())
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

//    interface Requirements {
//        BooleanConfigValue enabled = requirementsGroup
//                .booleanValue("enabled", false)
//                .comments("Enable requirement checking for when a block can grow via squatting.");
//
//        BooleanConfigValue itemTakesDamage = requirementsGroup
//                .booleanValue("itemTakesDamage", false)
//                .comments("Whether the required item should take damage when used to grow a block.");
//
//        FloatConfigValue durabilityDamage = requirementsGroup
//                .floatValue("durabilityDamage", 1f)
//                .comments("The amount of durability damage the required item takes when used to grow a block.");
//
//
//    }
//
////    @Comment("List of blocks that require a hoe to grow, leave empty to disable")
////    public List<String> heldItemRequirement = new ArrayList<>();
//
//    @Comment("Map of equipment slots to items required to grow, leave empty to disable")
//    public Map<EquipmentSlot, String> equipmentRequirement = Map.of();
//
//    @Comment("Durability based items take damage when used to grow")
//    public boolean requiredItemTakesDamage = false;
//
//    @Comment("Amount of damage to take when used to grow")
//    public int durabilityDamage = 1;
//
//    @Comment(
//            "Enchantment required to grow, leave empty to disable\n"
//    )
//    public String requiredEnchantment = "";
}
