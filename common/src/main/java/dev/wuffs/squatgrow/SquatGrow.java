package dev.wuffs.squatgrow;

import dev.architectury.event.events.common.LifecycleEvent;
import dev.architectury.event.events.common.TickEvent;
import dev.architectury.platform.Platform;
import dev.architectury.registry.ReloadListenerRegistry;
import dev.wuffs.squatgrow.config.SquatGrowConfig;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigHolder;
import me.shedaniel.autoconfig.serializer.YamlConfigSerializer;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class SquatGrow {
    public static final String MOD_ID = "squatgrow";
    private static final Logger LOGGER = LogManager.getLogger();
    public static Logger getLogger() {
        return LOGGER;
    }

    public static SquatGrowConfig config;
    public static ConfigHolder<SquatGrowConfig> configHolder;

    public static final Set<TagKey<Block>> tagCache = new HashSet<>();
    public static final Set<String> wildcardCache = new HashSet<>();

    public static void init() {
        configHolder = AutoConfig.register(SquatGrowConfig.class, YamlConfigSerializer::new);
        configHolder.registerLoadListener(SquatGrow::onConfigChanged);
        configHolder.registerSaveListener(SquatGrow::onConfigChanged);
        configHolder.load();
        config = configHolder.get();

        TickEvent.PLAYER_POST.register(CommonEvents::onPlayerTick);
        LifecycleEvent.SETUP.register(SquatGrow::onSetup);

        ReloadListenerRegistry.register(PackType.SERVER_DATA, new ReloadHandler());
    }

    static class ReloadHandler implements ResourceManagerReloadListener {
        @Override
        public void onResourceManagerReload(ResourceManager manager) {
            configHolder.load();
        }
    }

    private static void onSetup() {
        CommonEvents.isMysticalLoaded = Platform.isModLoaded("mysticalagriculture");
    }

    /**
     * Create caches of the tags and wildcards when the config reloads
     */
    private static InteractionResult onConfigChanged(ConfigHolder<SquatGrowConfig> holder, SquatGrowConfig newConfig) {
        tagCache.clear();
        wildcardCache.clear();

        LOGGER.info("Config loading");

        tagCache.addAll(newConfig.ignoreList.stream()
                .filter(e -> e.contains("#"))
                .map(e -> TagKey.create(Registries.BLOCK, new ResourceLocation(e.replace("#", ""))))
                .collect(Collectors.toSet()));

        wildcardCache.addAll(newConfig.ignoreList.stream().filter(e -> e.contains("*")).map(e -> e.split(":")[0])
                .collect(Collectors.toSet()));

        LOGGER.info("Tags: " + tagCache);
        LOGGER.info("Wildcards: " + wildcardCache);

        return InteractionResult.SUCCESS;
    }

    public static Boolean allowTwerk(BlockState state) {
        return config.useWhitelist == isBlockInIgnoreList(state);
//        ResourceLocation resourceLocation = state.getBlock().arch$registryName();
//        if (wildcardCache.contains(resourceLocation.getNamespace())) {
//
//        }
//        var regName = Registry.BLOCK.getKey(state.getBlock()).toString();
//        var rawTags = state.getBlock().builtInRegistryHolder().tags().toList();
//        if (config.useWhitelist) {
//            if (config.ignorelist.contains(regName)) {
//                if (config.debug) {
//                    LOGGER.debug("Whitelist contains reg: " + regName);
//                }
//                return true;
//            }
//            if (rawTags.size() > 0) {
//                for (TagKey<Block> tag : rawTags) {
//                    if (config.ignorelist.contains("#" + tag.location().toString())) {
//                        if (config.debug) {
//                            LOGGER.debug("Whitelist contains tag: " + "#" + tag.location());
//                        }
//                        return true;
//                    }
//                }
//            }
//            if (config.debug) {
//                LOGGER.debug("Whitelist does NOT contain: " + regName);
//            }
//            return false;
//        }
//
//        if (config.ignorelist.contains(regName)) {
//            if (config.debug) {
//                LOGGER.debug("Blacklist contains reg: " + regName);
//            }
//            return false;
//        }
//
//        if (rawTags.size() > 0) {
//            for (TagKey<Block> tag : rawTags) {
//                if (config.ignorelist.contains("#" + tag.location().toString())) {
//                    return false;
//                }
//                if (config.debug) {
//                    LOGGER.debug("Blacklist does NOT contain tag: " + "#" + tag.location());
//                }
//            }
//        }
//        if (config.debug) {
//            LOGGER.debug("Nothing contains: " + regName);
//        }
//        return true;
    }

    private static boolean isBlockInIgnoreList(BlockState state) {
        ResourceLocation resourceLocation = state.getBlock().arch$registryName();
        if (resourceLocation == null) {
            return false;
        }

        if (config.ignoreList.contains(resourceLocation.toString()) || wildcardCache.contains(resourceLocation.getNamespace())) {
            return true;
        }

        return tagCache.stream().anyMatch(state::is);
    }
}
