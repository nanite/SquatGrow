package dev.wuffs.squatgrow;

import dev.architectury.event.events.client.ClientLifecycleEvent;
import dev.architectury.event.events.common.LifecycleEvent;
import dev.architectury.networking.NetworkManager;
import dev.architectury.networking.simple.SimpleNetworkManager;
import dev.architectury.platform.Platform;
import dev.architectury.registry.ReloadListenerRegistry;
import dev.wuffs.squatgrow.actions.Actions;
import dev.wuffs.squatgrow.config.ComputedRequirements;
import dev.wuffs.squatgrow.config.SquatGrowConfig;
import dev.wuffs.squatgrow.network.SquatGrowEnabledPacket;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigHolder;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import me.shedaniel.autoconfig.serializer.YamlConfigSerializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class SquatGrow {
    public static final String MOD_ID = "squatgrow";
    private static final Logger LOGGER = LoggerFactory.getLogger(SquatGrow.class);

    public static SquatGrowConfig config;
    public static ConfigHolder<SquatGrowConfig> configHolder;

    public static final Set<TagKey<Block>> tagCache = new HashSet<>();
    public static final Set<String> wildcardCache = new HashSet<>();

    public static ComputedRequirements computedRequirements = null;
    public static LazyLevelDependentValue<Enchantment> computedEnchantment = null;

    public static void init() {
        configHolder = AutoConfig.register(SquatGrowConfig.class, JanksonConfigSerializer::new);
        configHolder.registerLoadListener(SquatGrow::onConfigChanged);
        configHolder.registerSaveListener(SquatGrow::onConfigChanged);
        configHolder.load();
        config = configHolder.get();

        if (Platform.getEnv() == EnvType.CLIENT) {
            SquatGrowClient.init();
        }

        LifecycleEvent.SETUP.register(SquatGrow::onSetup);
        ReloadListenerRegistry.register(PackType.SERVER_DATA, new ReloadHandler());

        NetworkManager.registerReceiver(
            NetworkManager.Side.C2S,
            SquatGrowEnabledPacket.TYPE,
            SquatGrowEnabledPacket.CODEC,
            (packet, context) -> context.queue(() -> SquatPlatform.setSquatGrowEnabled((ServerPlayer) context.getPlayer()))
        );
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

        tagCache.addAll(newConfig.ignoreList.stream()
                .filter(e -> e.contains("#"))
                .map(e -> TagKey.create(Registries.BLOCK, ResourceLocation.tryParse(e.replace("#", ""))))
                .collect(Collectors.toSet()));

        wildcardCache.addAll(newConfig.ignoreList.stream().filter(e -> e.contains("*")).map(e -> e.split(":")[0])
                .collect(Collectors.toSet()));

        List<String> heldItemRequirement = newConfig.requirements.heldItemRequirement;
        Map<EquipmentSlot, String> equipmentRequirement = newConfig.requirements.equipmentRequirement;

        Pair<List<ItemStack>, List<TagKey<Item>>> computedHeldEntries = computeItemsAndTagsFromStringList(heldItemRequirement);

        // This is kinda gross, but it does work so /shrug
        Map<EquipmentSlot, ItemStack> equipmentRequirementStacks = equipmentRequirement.entrySet().stream()
                .filter(e -> !e.getValue().contains("#"))
                .collect(Collectors.toMap(Map.Entry::getKey, e -> new ItemStack(BuiltInRegistries.ITEM.get(ResourceLocation.tryParse(e.getValue())).orElseThrow())));

        Map<EquipmentSlot, TagKey<Item>> equipmentRequirementTags = equipmentRequirement.entrySet().stream()
                .filter(e -> e.getValue().contains("#"))
                .collect(Collectors.toMap(Map.Entry::getKey, e -> TagKey.create(Registries.ITEM, ResourceLocation.tryParse(e.getValue().replace("#", "")))));

        computedRequirements = new ComputedRequirements(
                computedHeldEntries.getLeft(),
                computedHeldEntries.getRight(),
                equipmentRequirementStacks,
                equipmentRequirementTags
        );

        // This makes me want to puke, defaulted registries suck
        if (!newConfig.requirements.requiredEnchantment.isEmpty()) {
            ResourceLocation enchantmentRl = ResourceLocation.tryParse(newConfig.requirements.requiredEnchantment);
            computedEnchantment = new LazyLevelDependentValue<>(accessor -> {
                var key = ResourceKey.create(Registries.ENCHANTMENT, enchantmentRl);

                try {
                    RegistryAccess registryAccess = accessor.registryAccess();
                    Holder.Reference<Enchantment> enchantmentHolder = registryAccess.lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(key);
                    return enchantmentHolder.value();
                } catch (Exception e) {
                    LOGGER.error("Enchantment {} not found, falling back to null", enchantmentRl);
                    computedEnchantment = null;
                    return null;
                }
            });
        } else {
            computedEnchantment = null;
        }

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

    private static Pair<List<ItemStack>, List<TagKey<Item>>> computeItemsAndTagsFromStringList(List<String> list) {
        List<ItemStack> stacks = list.stream()
                .filter(e -> !e.contains("#"))
                .map(e -> BuiltInRegistries.ITEM.get(ResourceLocation.tryParse(e)))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(ItemStack::new)
                .toList();

        List<TagKey<Item>> tags = list.stream()
                .filter(e -> e.contains("#"))
                .map(e -> TagKey.create(Registries.ITEM, ResourceLocation.tryParse(e.replace("#", ""))))
                .toList();

        return Pair.of(stacks, tags);
    }

    public static class LazyLevelDependentValue<T> {
        @Nullable
        private T value = null;
        private final Function<LevelAccessor, T> supplier;

        public LazyLevelDependentValue(Function<LevelAccessor, T> supplier) {
            this.supplier = supplier;
        }

        public T get(LevelAccessor accessor) {
            if (value == null) {
                value = supplier.apply(accessor);
            }

            if (value == null) {
                throw new IllegalStateException("Value not initialized");
            }

            return value;
        }
    }
}
