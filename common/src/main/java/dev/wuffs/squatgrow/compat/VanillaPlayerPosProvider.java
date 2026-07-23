package dev.wuffs.squatgrow.compat;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;

public class VanillaPlayerPosProvider implements PlayerPosProvider {
    @Override
    public BlockPos forPlayer(Player player) {
        return player.blockPosition();
    }
}
