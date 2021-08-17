package dev.wuffs.squatgrow;

import net.minecraftforge.common.ForgeConfigSpec;

import java.util.ArrayList;
import java.util.List;

public class Config {
    public static final String CATEGORY_GENERAL = "general";

    public static ForgeConfigSpec CONFIG;

    public static ForgeConfigSpec.BooleanValue opMode;
    public static ForgeConfigSpec.BooleanValue useWhitelist;
    public static ForgeConfigSpec.IntValue range;
    public static ForgeConfigSpec.DoubleValue chance;
    public static ForgeConfigSpec.ConfigValue<List<String>> blacklist;
    public static ForgeConfigSpec.ConfigValue<List<String>> whitelist;


    static {
        ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
        List<String> defaultBlacklist = new ArrayList<>();
        defaultBlacklist.add("minecraft:grass_block");
        defaultBlacklist.add("minecraft:grass");
        defaultBlacklist.add("minecraft:tall_grass");
        defaultBlacklist.add("minecraft:netherrack");
        defaultBlacklist.add("minecraft:warped_nylium");
        defaultBlacklist.add("minecraft:crimson_nylium");

        BUILDER.comment("General settings").push(CATEGORY_GENERAL);
        opMode = BUILDER.comment("Enables OP mode!", "Enable at your own risk")
                .define("opMode", false);
        useWhitelist = BUILDER.comment("Use whitelist instead of blacklist")
                .define("useWhitelist", false);
        range = BUILDER.comment("Range for bonemeal effect")
                .defineInRange("range", 3, 0, 16);
        chance = BUILDER.comment("Chance of bonemeal effect", "0 being never and 1.0 being most of the time")
                .defineInRange("chance", 0.5, 0, 1.0);
        blacklist = BUILDER.comment("List of blocks to blacklist from twerking", "Tags can be used by using #minecraft:<tag_name> or #modid:<tag_name>")
                        .define("blacklist", defaultBlacklist);
        whitelist = BUILDER.comment("If useWhitelist is true use, only allow twerking the list below","Tags can be used by using #minecraft:<tag_name> or #modid:<tag_name>")
                .define("whitelist", new ArrayList<>());

        BUILDER.pop();

        CONFIG = BUILDER.build();
    }
}
