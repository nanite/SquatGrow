package dev.wuffs.squatgrow.common;

import dev.wuffs.squatgrow.Config;
import dev.wuffs.squatgrow.SquatGrow;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BoneMealItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

public class CommonEvents {

    private static Map<UUID, Boolean> playerSneaking = new HashMap<>();

    public static void init(final FMLCommonSetupEvent event) {
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
                    if (block instanceof BonemealableBlock && SquatGrow.allowTwerk(block.getRegistryName().toString(), block.getTags())) {
                        Random r = new Random();
                        double randomValue = 0 + (1 - 0) * r.nextDouble();
                        if (Config.debug.get()) {
                            SquatGrow.getLogger().debug("Rand value:" + randomValue);
                        }
                        if (Config.chance.get() >= randomValue) {
                            BoneMealItem.applyBonemeal(new ItemStack(Items.BONE_MEAL), level, blockPos, player);
                            ((ServerLevel) level).sendParticles((ServerPlayer) player, ParticleTypes.HAPPY_VILLAGER, false, blockPos.getX() + 0.05D, blockPos.getY() + 0.05D, blockPos.getZ(), 10, 0.5, 0.5, 0.5, 3);
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
