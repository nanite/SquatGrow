package dev.wuffs.squatgrow;

import dev.nanite.library.core.config.ConfigManager;
import dev.nanite.library.platform.Platform;
import dev.wuffs.squatgrow.actions.Actions;
import dev.wuffs.squatgrow.config.SquatGrowConfig;
import dev.wuffs.squatgrow.network.SquatGrowEnabledPacket;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.function.Function;

public class SquatGrow {
    public static final String MOD_ID = "squatgrow";
    private static final Logger LOGGER = LoggerFactory.getLogger(SquatGrow.class);

    public static LazyLevelDependentValue<Enchantment> computedEnchantment = null;

    public static void init() {
        Actions.init();
        Platform.INSTANCE.network().play2Server(SquatGrowEnabledPacket.TYPE, SquatGrowEnabledPacket.CODEC, ((payload, context) -> {
            SquatGrowPlatform.INSTANCE.setSquatGrowEnabled(context.player());
        }));

        ConfigManager.register(SquatGrowConfig.config);
        Platform.INSTANCE.registerDataPackReloadListener(Map.of(
                Identifier.fromNamespaceAndPath(MOD_ID, "squatgrow_config_updater"), new ReloadHandler()
        ));
    }

    static class ReloadHandler implements ResourceManagerReloadListener {
        @Override
        public void onResourceManagerReload(ResourceManager manager) {
            SquatGrowConfig.config.load();
        }
    }

//
//    /**
//     * Create caches of the tags and wildcards when the config reloads
//     */
//    private static InteractionResult onConfigChanged(ConfigHolder<SquatGrowConfig> holder, SquatGrowConfig newConfig) {
//        tagCache.clear();
//        wildcardCache.clear();
//
//        tagCache.addAll(newConfig.ignoreList.stream()
//                .filter(e -> e.contains("#"))
//                .map(e -> TagKey.create(Registries.BLOCK, Identifier.tryParse(e.replace("#", ""))))
//                .collect(Collectors.toSet()));
//
//        wildcardCache.addAll(newConfig.ignoreList.stream().filter(e -> e.contains("*")).map(e -> e.split(":")[0])
//                .collect(Collectors.toSet()));
//
//        List<String> heldItemRequirement = newConfig.requirements.heldItemRequirement;
//        Map<EquipmentSlot, String> equipmentRequirement = newConfig.requirements.equipmentRequirement;
//
//        Pair<List<ItemStack>, List<TagKey<Item>>> computedHeldEntries = computeItemsAndTagsFromStringList(heldItemRequirement);
//
//        // This is kinda gross, but it does work so /shrug
//        Map<EquipmentSlot, ItemStack> equipmentRequirementStacks = equipmentRequirement.entrySet().stream()
//                .filter(e -> !e.getValue().contains("#"))
//                .collect(Collectors.toMap(Map.Entry::getKey, e -> new ItemStack(BuiltInRegistries.ITEM.get(Identifier.tryParse(e.getValue())).orElseThrow())));
//
//        Map<EquipmentSlot, TagKey<Item>> equipmentRequirementTags = equipmentRequirement.entrySet().stream()
//                .filter(e -> e.getValue().contains("#"))
//                .collect(Collectors.toMap(Map.Entry::getKey, e -> TagKey.create(Registries.ITEM, Identifier.tryParse(e.getValue().replace("#", "")))));
//
//        computedRequirements = new ComputedRequirements(
//                computedHeldEntries.getLeft(),
//                computedHeldEntries.getRight(),
//                equipmentRequirementStacks,
//                equipmentRequirementTags
//        );
//
//        // This makes me want to puke, defaulted registries suck
//        if (!newConfig.requirements.requiredEnchantment.isEmpty()) {
//            Identifier enchantmentRl = Identifier.tryParse(newConfig.requirements.requiredEnchantment);
//            computedEnchantment = new LazyLevelDependentValue<>(accessor -> {
//                var key = ResourceKey.create(Registries.ENCHANTMENT, enchantmentRl);
//
//                try {
//                    RegistryAccess registryAccess = accessor.registryAccess();
//                    Holder.Reference<Enchantment> enchantmentHolder = registryAccess.lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(key);
//                    return enchantmentHolder.value();
//                } catch (Exception e) {
//                    LOGGER.error("Enchantment {} not found, falling back to null", enchantmentRl);
//                    computedEnchantment = null;
//                    return null;
//                }
//            });
//        } else {
//            computedEnchantment = null;
//        }
//
//        return InteractionResult.SUCCESS;
//    }

    public static Boolean allowTwerk(BlockState state) {
        return SquatGrowConfig.useWhitelist.get() == isBlockInIgnoreList(state);
    }

    private static boolean isBlockInIgnoreList(BlockState state) {
        return SquatGrowConfig.ignoreList.get().stream().anyMatch(e -> e.left()
                .map(id -> id.equals(state.getBlock().builtInRegistryHolder().key().identifier()))
                .orElse(e.right().map(state::is).orElse(false)));
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
