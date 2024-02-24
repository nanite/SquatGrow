package dev.wuffs.squatgrow;

import dev.wuffs.squatgrow.actions.Action;
import dev.wuffs.squatgrow.actions.ActionContext;
import dev.wuffs.squatgrow.actions.Actions;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Set;

import static dev.wuffs.squatgrow.SquatGrow.config;

public class SquatAction {
    public static void performAction(Level level, Player player) {
        if (level.isClientSide) return;
        if (!config.allowAdventureTwerking && ((ServerPlayer) player).gameMode.getGameModeForPlayer() == GameType.ADVENTURE) return;

        boolean handContainsHoe = (player.getMainHandItem().is(ItemTags.HOES) | player.getOffhandItem().is(ItemTags.HOES));
        if (config.requireHoe && !handContainsHoe) return;

        grow(level, (ServerPlayer) player);
    }

    public static void grow(Level level, ServerPlayer player) {
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

                    if (didGrow) {
                        if (config.requireHoe && config.hoeTakesDamage) {
                            ItemStack hoe = player.getMainHandItem();
                            if (!hoe.is(ItemTags.HOES)) {
                                hoe = player.getOffhandItem();
                            }

                            hoe.hurtAndBreak(1, player, (playerEntity) -> {
                                playerEntity.broadcastBreakEvent(player.getUsedItemHand());
                            });
                        }
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
            } else if (blockstate.isSolidRender(level, pos)) {
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
                    level.sendParticles(player, ParticleTypes.HAPPY_VILLAGER, false, x, y, z, numParticles, d2, d3, d4, 0.5);
                }
            }

            level.playSound(null, immutablePos, SoundEvents.BONE_MEAL_USE, SoundSource.MASTER, 0.5F, 1.0F);
        }
    }
}
