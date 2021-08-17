package dev.wuffs.squatgrow;

import dev.wuffs.squatgrow.common.CommonEvents;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Set;

@Mod(SquatGrow.MOD_ID)
public class SquatGrow {
    public static final String MOD_ID = "squatgrow";
    private static final Logger LOGGER = LogManager.getLogger();

    public static Logger getLogger() {
        return LOGGER;
    }

    public SquatGrow() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.CONFIG);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(CommonEvents::init);
    }

    public static Boolean allowTwerk(String regName, Set<ResourceLocation> rawTags) {

        if (Config.useWhitelist.get()) {
            if(Config.whitelist.get().contains(regName)){
                LOGGER.debug("Whitelist contains reg: " + regName);
                return true;
            }
            if(rawTags.size() > 0){
                for (ResourceLocation tag : rawTags){
                    if(Config.whitelist.get().contains("#" + tag.toString())){
                        LOGGER.debug("Whitelist contains tag: " + "#" + regName);
                        return true;
                    }
                }
            }
            LOGGER.debug("Whitelist does NOT contain: " + regName);
            return false;
        }

        if(Config.blacklist.get().contains(regName)){
            LOGGER.debug("Blacklist contains reg: " + regName);
            return false;
        }

        if(rawTags.size() > 0){
            for (ResourceLocation tag : rawTags){
                if(Config.blacklist.get().contains("#" + tag.toString())){
                    return false;
                }
                LOGGER.debug("Blacklist does NOT contain tag: " + "#" + tag);
            }
        }
        LOGGER.debug("Nothing contains: " + regName);
        return true;
    }
}
