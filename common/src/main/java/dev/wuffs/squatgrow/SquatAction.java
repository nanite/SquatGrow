package dev.wuffs.squatgrow;

import dev.wuffs.squatgrow.actions.Action;
import dev.wuffs.squatgrow.actions.ActionContext;
import dev.wuffs.squatgrow.actions.Actions;
import dev.wuffs.squatgrow.config.ComputedConfigValues;
import dev.wuffs.squatgrow.config.SquatGrowConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Set;

public class SquatAction {
    public static void performAction(Level level, Player player) {
        if (level.isClientSide()) return;

        var serverPlayer = (ServerPlayer) player;
        if (!SquatGrowConfig.allowAdventureTwerking.get() && serverPlayer.gameMode.getGameModeForPlayer() == GameType.ADVENTURE) return;

        if (!SquatGrowPlatform.INSTANCE.isSquatGrowEnabled(serverPlayer)) {
            return;
        }

        ComputedConfigValues config = ComputedConfigValues.get();
        ItemStack itemToDamage = null;
        if (config.shouldEvaluate()) {
            var itemThatPasses = itemThatPasses(config, level, player);
            if (itemThatPasses == null) {
                return;
            }

            itemToDamage = itemThatPasses;
        }

        final ItemStack finalItemToDamage = itemToDamage;
        grow(level, (ServerPlayer) player, () -> {
            if (finalItemToDamage == null) {
                return;
            }

            if (SquatGrowConfig.durabilityCost.get() > 0) {
                var chance = SquatGrowConfig.durabilityChance.get();
                var random = level.getRandom();
                if (chance < random.nextDouble()) {
                    return;
                }

                finalItemToDamage.hurtAndBreak(SquatGrowConfig.durabilityCost.get(), player, player.getUsedItemHand() == InteractionHand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND);
            }
        });
    }

    @Nullable
    public static ItemStack itemThatPasses(ComputedConfigValues config, Level level, Player player) {
        List<ItemStack> items = List.of(player.getMainHandItem(), player.getOffhandItem());
        for (ItemStack item : items) {
            if (item.isEmpty()) continue;

            if (config.itemMatches(level, item)) {
                return item;
            }
        }

        return null;
    }

    public static void grow(Level level, ServerPlayer player, Runnable onGrowth) {
        BlockPos pos = player.blockPosition();

        var random = level.getRandom();

        // Actions
        Set<Action> actions = Actions.get().getActions();

        var range = SquatGrowConfig.range.get();
        for (int x = -range; x <= range; x++) {
            for (int z = -range; z <= range; z++) {
                for (int y = -1; y <= 1; y++) {
                    double randomValue = 0 + 1 * random.nextDouble();
                    if (SquatGrowConfig.chance.get() < randomValue) {
                        continue;
                    }

                    boolean didGrow = false;
                    BlockPos offsetLocation = pos.offset(x, y, z);
                    BlockState offsetState = level.getBlockState(offsetLocation);

                    if (offsetState.isAir() || !blockAllowsTwerk(offsetState)) {
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

                    if (didGrow) {
                        addGrowthParticles((ServerLevel) level, offsetLocation, player);
                        onGrowth.run();
                    }
                }
            }
        }
    }

    private static void addGrowthParticles(ServerLevel level, BlockPos pos, ServerPlayer player) {
        var random = level.getRandom();
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

    public static Boolean blockAllowsTwerk(BlockState state) {
        return SquatGrowConfig.useWhitelist.get() == isBlockInIgnoreList(state);
    }

    static boolean isBlockInIgnoreList(BlockState state) {
        return ComputedConfigValues.get().ignoreList().stream().anyMatch(e -> e.left()
                .map(state::is)
                .orElse(e.right().map(state::is).orElse(false)));
    }
}
