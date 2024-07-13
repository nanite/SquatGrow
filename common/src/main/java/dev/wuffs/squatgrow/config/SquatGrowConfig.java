package dev.wuffs.squatgrow.config;

import dev.wuffs.squatgrow.SquatGrow;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.beans.Transient;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Config(name = SquatGrow.MOD_ID + "-common")
public class SquatGrowConfig implements ConfigData {
    @Comment("Enable debug logging")
    public boolean debug = false;

    @Comment("Require hoe to allow growth, LEGACY, PLEASE SWITCH TO THE NEW SYSTEM, SEE REQUIREMENTS")
    public boolean requireHoe = false;

    @Comment("Hoe takes damage on growth, LEGACY, PLEASE SWITCH TO THE NEW SYSTEM, SEE REQUIREMENTS")
    public boolean hoeTakesDamage = false;

    @Comment("Use whitelist instead of blacklist, default false")
    public boolean useWhitelist = false;

    @Comment("Range of effect, warning: this can cause lag if set too high")
    @ConfigEntry.BoundedDiscrete(min = 0, max = 16)
    public int range = 3;

    @Comment("Growth chance")
    public float chance = 0.5f;

    @Comment("Don't use! Use randomTickMultiplier instead, this is here for backwards compatibility")
    @ConfigEntry.BoundedDiscrete(min = 1, max = 16)
    public int sugarcaneMultiplier = 4;

    @Comment("Random tick multiplier, this is the amount of times the mod will call the randomTick method on the block for each block in the range")
    @ConfigEntry.BoundedDiscrete(min = 1, max = 16)
    public int randomTickMultiplier = 4;

    @Comment("List of blocks to blacklist/whitelist from twerking, Tags can be used by using #minecraft:<tag_name> or #modid:<tag_name>")
    public List<String> ignoreList = new ArrayList<>(Arrays.asList(
            "minecraft:grass_block",
            "minecraft:grass",
            "minecraft:short_grass",
            "minecraft:tall_grass",
            "minecraft:netherrack",
            "minecraft:warped_nylium",
            "minecraft:crimson_nylium"
    ));

    @Comment("Allow twerking in adventure mode, default true")
    public boolean allowAdventureTwerking = true;

    @Comment("Enable Mystical Crops")
    public boolean enableMysticalCrops = true;

    @Comment("Enable AE2 accelerator")
    public boolean enableAE2Accelerator = true;

    @Comment("AE2 growth multiplier, only available if AE2 is present")
    @ConfigEntry.BoundedDiscrete(min = 1, max = 16)
    public int ae2Multiplier = 4;

    @Comment("When the player is holding a grass block in their offhand, they will be able to randomly convert dirt into grass")
    public boolean enableDirtToGrass = true;

    @ConfigEntry.Category("requirements")
    @Comment("Requirements for growing")
    public Requirements requirements = new Requirements();

    public static class Requirements {
        @Comment("Enabled the new requirements system")
        public boolean enabled = true;

        @Comment("List of blocks that require a hoe to grow, leave empty to disable")
        public List<String> heldItemRequirement = new ArrayList<>();

        @Comment("Map of equipment slots to items required to grow, leave empty to disable")
        public Map<EquipmentSlot, String> equipmentRequirement = Map.of();

        @Comment("Durability based items take damage when used to grow")
        public boolean requiredItemTakesDamage = false;

        @Comment("Amount of damage to take when used to grow")
        public int durabilityDamage = 1;

        @Comment(
                "Enchantment required to grow, leave empty to disable\n"
        )
        public String requiredEnchantment = "";
    }
}
