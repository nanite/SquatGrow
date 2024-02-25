package dev.wuffs.squatgrow;

import dev.architectury.event.events.common.LifecycleEvent;
import dev.architectury.registry.ReloadListenerRegistry;
import dev.wuffs.squatgrow.actions.Actions;
import dev.wuffs.squatgrow.config.ComputedRequirements;
import dev.wuffs.squatgrow.config.SquatGrowConfig;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigHolder;
import me.shedaniel.autoconfig.serializer.YamlConfigSerializer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class SquatGrow {
    public static final String MOD_ID = "squatgrow";
    private static final Logger LOGGER = LoggerFactory.getLogger(SquatGrow.class);

    public static SquatGrowConfig config;
    public static ConfigHolder<SquatGrowConfig> configHolder;

    public static final Set<TagKey<Block>> tagCache = new HashSet<>();
    public static final Set<String> wildcardCache = new HashSet<>();

    public static ComputedRequirements computedRequirements = null;
    public static Enchantment computedEnchantment = null;

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

        tagCache.addAll(newConfig.ignoreList.stream()
                .filter(e -> e.contains("#"))
                .map(e -> TagKey.create(Registries.BLOCK, new ResourceLocation(e.replace("#", ""))))
                .collect(Collectors.toSet()));

        wildcardCache.addAll(newConfig.ignoreList.stream().filter(e -> e.contains("*")).map(e -> e.split(":")[0])
                .collect(Collectors.toSet()));

        List<String> heldItemRequirement = newConfig.requirements.heldItemRequirement;
        Map<EquipmentSlot, String> equipmentRequirement = newConfig.requirements.equipmentRequirement;

        Pair<List<ItemStack>, List<TagKey<Item>>> computedHeldEntries = computeItemsAndTagsFromStringList(heldItemRequirement);

        // This is kinda gross, but it does work so /shrug
        Map<EquipmentSlot, ItemStack> equipmentRequirementStacks = equipmentRequirement.entrySet().stream()
                .filter(e -> !e.getValue().contains("#"))
                .collect(Collectors.toMap(Map.Entry::getKey, e -> new ItemStack(BuiltInRegistries.ITEM.get(new ResourceLocation(e.getValue())))));

        Map<EquipmentSlot, TagKey<Item>> equipmentRequirementTags = equipmentRequirement.entrySet().stream()
                .filter(e -> e.getValue().contains("#"))
                .collect(Collectors.toMap(Map.Entry::getKey, e -> TagKey.create(Registries.ITEM, new ResourceLocation(e.getValue().replace("#", "")))));

        computedRequirements = new ComputedRequirements(
                computedHeldEntries.getLeft(),
                computedHeldEntries.getRight(),
                equipmentRequirementStacks,
                equipmentRequirementTags
        );

        // This makes me want to puke, defaulted registries suck
        if (!newConfig.requirements.requiredEnchantment.isEmpty()) {
            ResourceLocation enchantmentRl = new ResourceLocation(newConfig.requirements.requiredEnchantment);
            Enchantment enchantment = BuiltInRegistries.ENCHANTMENT.get(enchantmentRl);
            // Default for the registry is fortune, we need to make that if we get fortune it matches the enchantmentRl
            if (enchantment != Enchantments.BLOCK_FORTUNE || enchantmentRl.equals(new ResourceLocation("minecraft:fortune"))) {
                // If the enchantment is not fortune or it is but we want fortune, set it
                computedEnchantment = enchantment;
            }
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
                .map(e -> new ItemStack(BuiltInRegistries.ITEM.get(new ResourceLocation(e))))
                .toList();

        List<TagKey<Item>> tags = list.stream()
                .filter(e -> e.contains("#"))
                .map(e -> TagKey.create(Registries.ITEM, new ResourceLocation(e.replace("#", ""))))
                .toList();

        return Pair.of(stacks, tags);
    }
}
