package dev.wuffs.squatgrow.config;

import dev.wuffs.squatgrow.SquatGrow;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Config(name = SquatGrow.MOD_ID + "-common")
public class SquatGrowConfig implements ConfigData {

    @Comment("Enable debug logging")
    public boolean debug = false;

    @Comment("Enable Mystical Crops")
    public boolean enableMysticalCrops = true;

    @Comment("Allow twerking in adventure mode, default true")
    public boolean allowAdventureTwerking = true;

    @Comment("Use whitelist instead of blacklist, default false")
    public boolean useWhitelist = false;

    @Comment("Range of effect")
    @ConfigEntry.BoundedDiscrete(min = 0, max = 16)
    public int range = 3;

    @Comment("Growth Chance")
    public double chance = 0.5;

    @Comment("List of blocks to blacklist/whitelist from twerking, Tags can be used by using #minecraft:<tag_name> or #modid:<tag_name>")
    public List<String> ignoreList = new ArrayList<>(Arrays.asList(
            "minecraft:grass_block",
            "minecraft:grass",
            "minecraft:tall_grass",
            "minecraft:netherrack",
            "minecraft:warped_nylium",
            "minecraft:crimson_nylium"
    ));
}
