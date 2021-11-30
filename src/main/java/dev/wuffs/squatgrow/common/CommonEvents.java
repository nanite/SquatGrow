package dev.wuffs.squatgrow.common;

import dev.wuffs.squatgrow.Config;
import dev.wuffs.squatgrow.SquatGrow;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BoneMealItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

public class CommonEvents {

    private static final ResourceLocation MYSTICAL_TAG = new ResourceLocation("mysticalagriculture", "crops");

    private static Map<UUID, Boolean> playerSneaking = new HashMap<>();
    public static boolean isMysticalLoaded = false;

    public static void init(final FMLCommonSetupEvent event) {
        isMysticalLoaded = ModList.get().isLoaded("mysticalagriculture");
        MinecraftForge.EVENT_BUS.addListener(CommonEvents::playerTickEvent);
    }

    public static void playerTickEvent(TickEvent.PlayerTickEvent event) {
        Player player = event.player;

        if (player.level.isClientSide) return;
        if (Config.opMode.get() && player.isCrouching()) {
            doBoneMeal(player);
        } else {
            if (playerSneaking.containsKey(player.getUUID())) {
                if (player.isCrouching() && !playerSneaking.get(player.getUUID())) {
                    playerSneaking.put(player.getUUID(), true);
                    doBoneMeal(player);
                } else if (playerSneaking.get(player.getUUID()) && !player.isCrouching()) {
                    playerSneaking.put(player.getUUID(), false);
                }
            } else {
                playerSneaking.put(player.getUUID(), false);
            }
        }


    }

    public static void doBoneMeal(Player player) {
        Level level = player.level;
        BlockPos pos = player.blockPosition();

        for (int x = -Config.range.get(); x <= Config.range.get(); x++) {
            for (int z = -Config.range.get(); z <= Config.range.get(); z++) {
                for (int y = -1; y <= 1; y++) {
                    BlockPos blockPos = new BlockPos(pos.getX() + x, pos.getY() + y, pos.getZ() + z);
                    Block block = level.getBlockState(blockPos).getBlock();
                    if ((block instanceof BonemealableBlock || block instanceof SugarCaneBlock) && SquatGrow.allowTwerk(block.getRegistryName().toString(), block.getTags())) {
                        Random r = new Random();
                        double randomValue = 0 + (1 - 0) * r.nextDouble();
                        if (Config.debug.get()) {
                            SquatGrow.getLogger().debug("Rand value:" + randomValue);
                        }
                        if (Config.chance.get() >= randomValue) {
                            if (block instanceof SugarCaneBlock) {
                                block.randomTick(level.getBlockState(blockPos), ((ServerLevel) level), blockPos, level.random);
                            } else {
                                if (isMysticalLoaded && block.getTags().contains(MYSTICAL_TAG) && Config.enableMysticalCrops.get()) {
                                    ((CropBlock) block).growCrops(level, blockPos, level.getBlockState(blockPos));
                                } else {
                                    BoneMealItem.applyBonemeal(new ItemStack(Items.BONE_MEAL), level, blockPos, player);
                                }
                            }
//                            ((ServerWorld) level).sendParticles((ServerPlayerEntity) player, ParticleTypes.HAPPY_VILLAGER, false, blockPos.getX() + 0.5D, blockPos.getY() + 0.5D, blockPos.getZ() + 0.1D, 10, 0.5, 0.5, 0.5, 3);
                            addGrowthParticles((ServerLevel) level, blockPos, (ServerPlayer) player);
                            if (Config.debug.get()) {
                                SquatGrow.getLogger().debug("====================================================");
                                SquatGrow.getLogger().debug("Block: " + block.getRegistryName().toString());
                                SquatGrow.getLogger().debug("Tags: " + block.getTags());
                                SquatGrow.getLogger().debug("Pos: " + blockPos);
                                SquatGrow.getLogger().debug("====================================================");
                            }
                        }
                    }
                }
            }
        }
    }

    private static void addGrowthParticles(ServerLevel level, BlockPos pos, ServerPlayer player) {
        Random random = new Random();
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

            for (int i = 0; i < numParticles; ++i) {
                double d2 = random.nextGaussian() * 0.02D;
                double d3 = random.nextGaussian() * 0.02D;
                double d4 = random.nextGaussian() * 0.02D;
                double d5 = 0.5D - d0;
                double x = pos.getX() + d5 + random.nextDouble() * d0 * 2.0D;
                double y = pos.getY() + random.nextDouble() * d1;
                double z = pos.getZ() + d5 + random.nextDouble() * d0 * 2.0D;
                if (!level.getBlockState((new BlockPos(x, y, z)).below()).isAir()) {
                    level.sendParticles(player, ParticleTypes.HAPPY_VILLAGER, false, x, y, z, numParticles, d2, d3, d4, 0.5);
                }
            }
        }
    }
}
