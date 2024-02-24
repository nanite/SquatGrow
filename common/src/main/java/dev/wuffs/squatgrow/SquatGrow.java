package dev.wuffs.squatgrow;

import dev.architectury.event.events.common.LifecycleEvent;
import dev.architectury.registry.ReloadListenerRegistry;
import dev.wuffs.squatgrow.actions.Actions;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class SquatGrow {
    public static final String MOD_ID = "squatgrow";
    private static final Logger LOGGER = LoggerFactory.getLogger(SquatGrow.class);

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

        LifecycleEvent.SETUP.register(SquatGrow::onSetup);

        ReloadListenerRegistry.register(PackType.SERVER_DATA, new ReloadHandler());
    }

    static class ReloadHandler implements ResourceManagerReloadListener {
        @Override
        public void onResourceManagerReload(ResourceManager manager) {
            configHolder.load();
            config = configHolder.get();
        }
    }

    private static void onSetup() {
        LOGGER.debug("Starting setup");
        Actions.get().setup();
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
