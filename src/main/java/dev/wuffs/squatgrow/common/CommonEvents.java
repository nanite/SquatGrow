package dev.wuffs.squatgrow.common;

import dev.wuffs.squatgrow.Config;
import dev.wuffs.squatgrow.SquatGrow;
import net.minecraft.block.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.BoneMealItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoader;
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
        PlayerEntity player = event.player;

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

    public static void doBoneMeal(PlayerEntity player) {
        World level = player.level;
        BlockPos pos = player.blockPosition();

        for (int x = -Config.range.get(); x <= Config.range.get(); x++) {
            for (int z = -Config.range.get(); z <= Config.range.get(); z++) {
                for (int y = -1; y <= 1; y++) {
                    BlockPos blockPos = new BlockPos(pos.getX() + x, pos.getY() + y, pos.getZ() + z);
                    Block block = level.getBlockState(blockPos).getBlock();
                    if ((block instanceof IGrowable || block instanceof SugarCaneBlock) && SquatGrow.allowTwerk(block.getRegistryName().toString(), block.getTags())) {
                        Random r = new Random();
                        double randomValue = 0 + (1 - 0) * r.nextDouble();
                        if (Config.debug.get()) {
                            SquatGrow.getLogger().debug("Rand value:" + randomValue);
                        }
                        if (Config.chance.get() >= randomValue) {
                            if (block instanceof SugarCaneBlock) {
                                block.randomTick(level.getBlockState(blockPos), ((ServerWorld) level), blockPos, level.random);
                            } else {
                                if (isMysticalLoaded && block.getTags().contains(MYSTICAL_TAG)) {
                                    ((CropsBlock) block).growCrops(level, blockPos, level.getBlockState(blockPos));
                                } else {
                                    BoneMealItem.applyBonemeal(new ItemStack(Items.BONE_MEAL), level, blockPos, player);
                                }
                            }
                            ((ServerWorld) level).sendParticles((ServerPlayerEntity) player, ParticleTypes.HAPPY_VILLAGER, false, blockPos.getX() + 0.05D, blockPos.getY() + 0.05D, blockPos.getZ(), 10, 0.5, 0.5, 0.5, 3);
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
}
