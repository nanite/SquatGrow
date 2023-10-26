package dev.wuffs.squatgrow;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BoneMealItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Random;

import static dev.wuffs.squatgrow.SquatGrow.config;

public class SquatAction {
    private static final TagKey<Block> MYSTICAL_TAG = TagKey.create(Registries.BLOCK, new ResourceLocation("mysticalagriculture", "crops"));
    private static final TagKey<Block> AE2_TAG = TagKey.create(Registries.BLOCK, new ResourceLocation("ae2", "growth_acceleratable"));

    public static boolean isMysticalLoaded = false;
    public static boolean isAE2Loaded = false;

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

        for (int x = -config.range; x <= config.range; x++) {
            for (int z = -config.range; z <= config.range; z++) {
                for (int y = -1; y <= 1; y++) {
                    double randomValue = 0 + (1 - 0) * r.nextDouble();
                    if (config.debug) {
                        SquatGrow.getLogger().debug("Rand value:" + randomValue);
                    }
                    if (config.chance >= randomValue) {
                        boolean didGrow = false;
                        BlockPos blockPos = new BlockPos(pos.getX() + x, pos.getY() + y, pos.getZ() + z);
                        BlockState blockState = level.getBlockState(blockPos);
                        Block block = blockState.getBlock();
                        if (isMysticalLoaded && block.builtInRegistryHolder().tags().toList().contains(MYSTICAL_TAG) && config.enableMysticalCrops) {
                            ((CropBlock) block).growCrops(level, blockPos, level.getBlockState(blockPos));
                            didGrow = true;
                        } else if (isAE2Loaded && block.builtInRegistryHolder().tags().toList().contains(AE2_TAG) && config.enableMysticalCrops) {
                            SquatGrow.getLogger().debug("Ticking ae2 block: " + level.getBlockState(blockPos).getBlock().arch$registryName().toString() + " " + config.ae2Multiplier + " times");
                            for (int i = 0; i < config.sugarcaneMultiplier; i++) {
                                block.randomTick(level.getBlockState(blockPos), ((ServerLevel) level), blockPos, level.random);
                            }
                            didGrow = true;
                        }
                        if ((block instanceof BonemealableBlock || block instanceof SugarCaneBlock) && SquatGrow.allowTwerk(blockState)) {
                            if (block instanceof SugarCaneBlock) {
                                // Todo make this a loop with a config option for the multiplier
                                SquatGrow.getLogger().debug("Ticking sugarcane block: " + config.sugarcaneMultiplier + " times");
                                for (int i = 0; i < config.sugarcaneMultiplier; i++) {
                                    block.randomTick(blockState, ((ServerLevel) level), blockPos, level.random);
                                }
                                didGrow = true;
                            } else {

                                BoneMealItem.growCrop(new ItemStack(Items.BONE_MEAL), level, blockPos);
                                didGrow = true;
                            }
//                            ((ServerWorld) level).sendParticles((ServerPlayerEntity) player, ParticleTypes.HAPPY_VILLAGER, false, blockPos.getX() + 0.5D, blockPos.getY() + 0.5D, blockPos.getZ() + 0.1D, 10, 0.5, 0.5, 0.5, 3);
                        }
//
                        if (didGrow) {
                            addGrowthParticles((ServerLevel) level, blockPos, player);
                        }

                        if (config.debug) {
                            SquatGrow.getLogger().debug("====================================================");
//                                SquatGrow.getLogger().debug("Block: " + Registry.BLOCK.getKey(block).toString());
                            SquatGrow.getLogger().debug("Tags: " + block.builtInRegistryHolder().tags().toList().toString());
                            SquatGrow.getLogger().debug("Pos: " + blockPos);
                            SquatGrow.getLogger().debug("====================================================");
                        }
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
            double d0 = 0.5D;
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
            level.sendParticles(player, ParticleTypes.HAPPY_VILLAGER, false, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, numParticles, 0.0D, 0.0D, 0.5, 0.5);

            // Something can cause this to mutate
            BlockPos immutablePos = pos.immutable();
            for (int i = 0; i < numParticles; ++i) {
                double d2 = random.nextGaussian() * 0.02D;
                double d3 = random.nextGaussian() * 0.02D;
                double d4 = random.nextGaussian() * 0.02D;
                double d5 = 0.5D - d0;
                double x = immutablePos.getX() + d5 + random.nextInt() * d0 * 2;
                double y = immutablePos.getY() + random.nextInt() * d1;
                double z = immutablePos.getZ() + d5 + random.nextInt() * d0 * 2;

                BlockState state = level.getBlockState(immutablePos);
                if (!state.isAir()) {
                    level.sendParticles(player, ParticleTypes.HAPPY_VILLAGER, false, x, y, z, numParticles, d2, d3, d4, 0.5);
                    level.playSound(null, immutablePos, SoundEvents.BONE_MEAL_USE, SoundSource.MASTER, 0.5F, 1.0F);
                }
            }
        }
    }
}
