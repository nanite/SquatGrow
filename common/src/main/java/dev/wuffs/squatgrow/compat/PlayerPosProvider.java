package dev.wuffs.squatgrow.compat;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;

public interface PlayerPosProvider {
    BlockPos forPlayer(Player player);
}
