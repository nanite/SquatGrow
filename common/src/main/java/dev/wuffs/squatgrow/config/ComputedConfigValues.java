package dev.wuffs.squatgrow.config;

import com.mojang.datafixers.util.Either;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.core.DefaultedRegistry;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import org.slf4j.Logger;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/// Represents config values after parsing and after transforming them into their in-game representations,
/// this is used to avoid having to parse the config values every time we want to check if an item matches the config
public record ComputedConfigValues(
        Set<Either<Block, TagKey<Block>>> ignoreList,
        Set<Either<Item, TagKey<Item>>> anyOfItems,
        Set<Pair<Identifier, Integer>> anyOfEnchantments
) {
    private static final Logger LOGGER = LogUtils.getLogger();

    public static ComputedConfigValues INSTANCE;

    public static ComputedConfigValues get() {
        if (INSTANCE == null) {
            INSTANCE = fromConfig();
        }

        return INSTANCE;
    }

    public static void invalidate() {
        INSTANCE = null;
    }

    public static ComputedConfigValues fromConfig() {
        var itemRequirements = resolveRegistryTagOrElement(SquatGrowConfig.requiredItem.get(), BuiltInRegistries.ITEM);
        var blockIgnoreList = resolveRegistryTagOrElement(SquatGrowConfig.ignoreList.get(), BuiltInRegistries.BLOCK);
        var resolvedEnchantments = resolveEnchantments(SquatGrowConfig.requiredItemEnchants.get());

        return new ComputedConfigValues(blockIgnoreList, itemRequirements, resolvedEnchantments);
    }

    private static Set<Pair<Identifier, Integer>> resolveEnchantments(List<String> strings) {
        Set<Pair<Identifier, Integer>> results = new HashSet<>();
        for (String string : strings) {
            var idAndLevel = string.split("@");
            var id = Identifier.tryParse(idAndLevel[0]);
            if (id == null) {
                LOGGER.warn("Invalid enchantment id: {}", idAndLevel[0]);
                continue;
            }

            var level = idAndLevel.length > 1 ? Integer.parseInt(idAndLevel[1]) : 0;
            results.add(Pair.of(id, level));
        }
        return results;
    }

    private static <T> Set<Either<T, TagKey<T>>> resolveRegistryTagOrElement(List<String> values, DefaultedRegistry<T> registry) {
        var result = new HashSet<Either<T, TagKey<T>>>();
        for (String value : values) {
            if (value.startsWith("#")) {
                // This is a tag
                String tagId = value.substring(1);
                var id = Identifier.tryParse(tagId);
                if (id == null) {
                    LOGGER.warn("Invalid tag id: {}", tagId);
                    continue;
                }

                var tagKey = TagKey.create(registry.key(), id);
                result.add(Either.right(tagKey));
            } else {
                var id = Identifier.tryParse(value);
                if (id == null) {
                    LOGGER.warn("Invalid item/block id: {}", value);
                    continue;
                }

                var item = registry.get(id);
                if (item.isPresent()) {
                    result.add(Either.left(item.get().value()));
                } else {
                    LOGGER.warn("Could not find item/block with id: {}", value);
                }
            }
        }

        return result;
    }

    /// We should only evaluate the item requirements if there are any, if the list is empty then we should just skip the checks since they would always pass
    public boolean shouldEvaluate() {
        return !anyOfItems.isEmpty();
    }

    public boolean itemMatches(LevelAccessor level, ItemStack stack) {
        for (Either<Item, TagKey<Item>> anyOfItem : anyOfItems) {
            // If the item doesn't match, continue.
            if (!anyOfItem.left().map(item -> stack.getItem() == item).orElseGet(() -> {
                var tagKey = anyOfItem.right().orElseThrow();
                return stack.is(tagKey);
            })) {
                continue;
            }

            // If we've matched the item and we have no enchantment requirements, we can just return true.
            if (anyOfEnchantments.isEmpty()) {
                return true;
            }

            // Otherwise, ensure they have all the needed enchantments
            int matchedEnchantments = 0;
            var enchantments = level.registryAccess().lookup(Registries.ENCHANTMENT).orElseThrow();

            for (Pair<Identifier, Integer> enchantment : anyOfEnchantments) {
                var enchantmentId = enchantment.left();
                var enchantmentLevel = enchantment.right();

                // No clue how this would ever fail.
                var resolvedEnchantment = enchantments.get(enchantmentId);
                if (resolvedEnchantment.isEmpty()) {
                    LOGGER.warn("Could not find enchantment with id: {}", enchantmentId);
                    continue;
                }

                for (Object2IntMap.Entry<Holder<Enchantment>> itemEnchantments : stack.getEnchantments().entrySet()) {
                    if (resolvedEnchantment.get().is(itemEnchantments.getKey())) {
                        if (enchantmentLevel == 0 || itemEnchantments.getIntValue() >= enchantmentLevel) {
                            matchedEnchantments++;
                        }
                    }
                }
            }

            // If this item didn't match it, maybe another item will.
            if (matchedEnchantments == anyOfEnchantments.size()) {
                return true;
            }
        }

        return false;
    }
}
