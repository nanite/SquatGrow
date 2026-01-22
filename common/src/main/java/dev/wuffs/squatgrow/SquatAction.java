package dev.wuffs.squatgrow;

import dev.wuffs.squatgrow.actions.Action;
import dev.wuffs.squatgrow.actions.ActionContext;
import dev.wuffs.squatgrow.actions.Actions;
import dev.wuffs.squatgrow.config.SquatGrowConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;

import static dev.wuffs.squatgrow.SquatGrow.*;

public class SquatAction {
    public static void performAction(Level level, Player player) {
        if (level.isClientSide) return;

        var serverPlayer = (ServerPlayer) player;
        if (!config.allowAdventureTwerking && serverPlayer.gameMode.getGameModeForPlayer() == GameType.ADVENTURE) return;

        if (!SquatPlatform.isSquatGrowEnabled(serverPlayer)) {
            return;
        }

        Pair<Boolean, List<ItemStack>> requirementsTest = passesRequirements(player);
        if (!requirementsTest.getKey()) {
            return;
        }

        grow(level, (ServerPlayer) player, requirementsTest.getValue());
    }

    public static Pair<Boolean, List<ItemStack>> passesRequirements(Player player) {
        List<ItemStack> itemsThatHandleDamage = new ArrayList<>();
        // Legacy support, if this is enabled, the requirements system is disabled.
        if (config.requireHoe) {
            // Meh, lists aren't free but emptylist is a constant so it's fine
            var matchedItem = getMatchingHeldItem(player, Collections.emptyList(), List.of(ItemTags.HOES));
            if (!matchedItem.isEmpty()) {
                itemsThatHandleDamage.add(matchedItem);
                return Pair.of(true, itemsThatHandleDamage);
            }

            return Pair.of(false, itemsThatHandleDamage);
        }

        SquatGrowConfig.Requirements requirements = config.requirements;
        if (requirements.enabled && SquatGrow.computedRequirements != null) {
            // Easier to compare the originals than it is to compare the computed ones
            if (requirements.heldItemRequirement.isEmpty() && requirements.equipmentRequirement.isEmpty()) {
                return Pair.of(true, itemsThatHandleDamage);
            }

            // Let's check the correct things. First, the lighter of the two checks
            boolean passesEquipment = false;
            if (!requirements.equipmentRequirement.isEmpty()) {
                var matchingEquipment = matchingEquipmentItem(player.level(), player, computedRequirements.equipmentRequirementStacks(), computedRequirements.equipmentRequirementTags());

                // This is safe to do as it will only increment if the equipment is found, and you can only have one item per slot
                if (matchingEquipment.size() == requirements.equipmentRequirement.size()) {
                    itemsThatHandleDamage.addAll(matchingEquipment);
                    passesEquipment = true;
                }
            }

            if (!requirements.equipmentRequirement.isEmpty() && !passesEquipment) {
                return Pair.of(false, itemsThatHandleDamage); // If the equipment check is required and failed, we can return false
            }

            // Now, the heavier of the two checks
            // We can only have gotten here if heldItemRequirement is not empty so no need to check again
            boolean passedHeldItem = false;
            var matchingHeldItem = getMatchingHeldItem(player, computedRequirements.heldItemRequirementStacks(), computedRequirements.heldItemRequirementTags());
            if (!matchingHeldItem.isEmpty()) {
                itemsThatHandleDamage.add(matchingHeldItem);
                passedHeldItem = true;
            }

            if (!requirements.heldItemRequirement.isEmpty() && !passedHeldItem) {
                return Pair.of(false, itemsThatHandleDamage); // If the held item check is required and failed, we can return false
            }

            return Pair.of(true, itemsThatHandleDamage); // If we got here, we passed both checks
        }

        // Nothing is required, so we can return true
        return Pair.of(true, Collections.emptyList());
    }

    public static void grow(Level level, ServerPlayer player, List<ItemStack> itemsToDamage) {
        BlockPos pos = player.blockPosition();

        var r = level.random;

        // Actions
        Set<Action> actions = Actions.get().getActions();

        for (int x = -config.range; x <= config.range; x++) {
            for (int z = -config.range; z <= config.range; z++) {
                for (int y = -1; y <= 1; y++) {
                    double randomValue = 0 + 1 * r.nextDouble();
                    if (config.chance < randomValue) {
                        continue;
                    }

                    boolean didGrow = false;
                    BlockPos offsetLocation = pos.offset(x, y, z);
                    BlockState offsetState = level.getBlockState(offsetLocation);

                    if (offsetState.isAir() || !SquatGrow.allowTwerk(offsetState)) {
                        continue;
                    }

                    ActionContext context = new ActionContext(
                            level,
                            offsetLocation,
                            offsetState,
                            player.getMainHandItem(),
                            player.getOffhandItem(),
                            player
                    );

                    for (Action action : actions) {
                        if (!action.canApply(context)) {
                            continue;
                        }

                        didGrow = action.execute(context);
                    }

                    if ((config.hoeTakesDamage || config.requirements.requiredItemTakesDamage) && didGrow && !itemsToDamage.isEmpty()) {
                        var durabilityToApply = config.hoeTakesDamage ? 1 : config.requirements.durabilityDamage;
                        for (ItemStack item : itemsToDamage) {
                            item.hurtAndBreak(durabilityToApply, player, player.getUsedItemHand() == InteractionHand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND);
                        }
                    }

                    if (didGrow) {
                        addGrowthParticles((ServerLevel) level, offsetLocation, player);
                    }
                }
            }
        }
    }

    private static void addGrowthParticles(ServerLevel level, BlockPos pos, ServerPlayer player) {
        var random = level.random;
        int numParticles = 2;

        BlockState blockstate = level.getBlockState(pos);
        if (!blockstate.isAir()) {
            double d0 = 0.5D; // Gaz what was this for?
            double d1;
            if (blockstate.is(Blocks.WATER)) {
                numParticles *= 3;
                d1 = 1.0D;
                d0 = 3.0D;
            } else if (blockstate.isSolidRender()) {
                pos = pos.above();
                numParticles *= 3;
                d0 = 3.0D;
                d1 = 1.0D;
            } else {
                d1 = blockstate.getShape(level, pos).max(Direction.Axis.Y);
            }

            var randomPartialCount = random.nextInt(1, numParticles);

            // Something can cause this to mutate
            BlockPos immutablePos = pos.immutable();
            for (int i = 0; i < randomPartialCount; ++i) {
                double d2 = random.nextGaussian() * 0.2D;
                double d3 = random.nextGaussian() * 0.2D;
                double d4 = random.nextGaussian() * 0.2D;

                var randomY = Mth.clamp(random.nextDouble(), 0.1, 0.5);

                // Randomly place a particle somewhere within the blocks x and z
                double x = immutablePos.getX() + Mth.clamp(random.nextDouble(), -1D, 1D);
                double y = (immutablePos.getY() - .95D) + (d1 + randomY);
                double z = immutablePos.getZ() + Mth.clamp(random.nextDouble(), -1D, 1D);

                BlockState state = level.getBlockState(immutablePos);
                if (!state.isAir()) {
                    level.sendParticles(player, ParticleTypes.HAPPY_VILLAGER, false, true, x, y, z, numParticles, d2, d3, d4, 0.5);
                }
            }

            level.playSound(null, immutablePos, SoundEvents.BONE_MEAL_USE, SoundSource.MASTER, 0.5F, 1.0F);
        }
    }

    private static ItemStack getMatchingHeldItem(Player player, List<ItemStack> itemStacks, List<TagKey<Item>> itemTags) {
        var mainHand = player.getMainHandItem();
        var offHand = player.getOffhandItem();

        // Check the main hand first
        var matchingItem = compareItemToLists(player.level(), mainHand, itemStacks, itemTags);
        if (!matchingItem.isEmpty()) {
            return matchingItem;
        }

        // Check the offhand next
        return compareItemToLists(player.level(), offHand, itemStacks, itemTags);
    }

    private static ItemStack compareItemToLists(Level level, ItemStack stack, List<ItemStack> itemStacks, List<TagKey<Item>> itemTags) {
        for (ItemStack item : itemStacks) {
            if (itemStackMatches(level, stack, item)) {
                return stack;
            }
        }

        for (TagKey<Item> tag : itemTags) {
            if (itemStackMatches(level, stack, tag)) {
                return stack;
            }
        }

        return ItemStack.EMPTY;
    }

    private static List<ItemStack> matchingEquipmentItem(Level level, Player player, Map<EquipmentSlot, ItemStack> equipmentStacks, Map<EquipmentSlot, TagKey<Item>> equipmentTags) {
        List<ItemStack> matchedItems = new ArrayList<>();

        for (Map.Entry<EquipmentSlot, ItemStack> entry : equipmentStacks.entrySet()) {
            ItemStack itemBySlot = player.getItemBySlot(entry.getKey());
            if (itemStackMatches(level, itemBySlot, entry.getValue())) {
                matchedItems.add(itemBySlot);
            }
        }

        for (Map.Entry<EquipmentSlot, TagKey<Item>> entry : equipmentTags.entrySet()) {
            ItemStack itemBySlot = player.getItemBySlot(entry.getKey());
            if (itemStackMatches(level, itemBySlot, entry.getValue())) {
                matchedItems.add(itemBySlot);
            }
        }

        return matchedItems;
    }

    private static boolean itemStackMatches(Level level, ItemStack stack, TagKey<Item> tag) {
        if (computedEnchantment != null && stack.isEnchantable()) {
            var enchantmentValue = computedEnchantment.get(level);
            if (enchantmentValue != null && stack.is(tag)) {
                ItemEnchantments itemEnchantments = stack.get(DataComponents.ENCHANTMENTS);
                if (itemEnchantments != null) {
                    return stack.is(tag) && itemEnchantments.getLevel(Holder.direct(enchantmentValue)) > 0;
                }
            }
        }

        return stack.is(tag);
    }

    private static boolean itemStackMatches(Level level, ItemStack stack, ItemStack item) {
        if (computedEnchantment != null && stack.isEnchantable()) {
            var enchantmentValue = computedEnchantment.get(level);
            if (enchantmentValue != null) {
                if (stack.is(item.getItem())) {
                    ItemEnchantments itemEnchantments = stack.get(DataComponents.ENCHANTMENTS);
                    if (itemEnchantments != null) {
                        return itemEnchantments.getLevel(Holder.direct(enchantmentValue)) > 0;
                    }
                }
            }
        }

        return stack.is(item.getItem());
    }
}
