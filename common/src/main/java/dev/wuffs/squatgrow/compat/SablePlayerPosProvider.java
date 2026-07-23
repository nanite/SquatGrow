package dev.wuffs.squatgrow.compat;

import dev.ryanhcode.sable.companion.SableCompanion;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;

public class SablePlayerPosProvider implements PlayerPosProvider {
    @Override
    public BlockPos forPlayer(Player player) {
        SableCompanion sable = SableCompanion.INSTANCE;

        var subLevel = sable.getTrackingOrVehicleSubLevel(player);
        if (subLevel == null) {
            return player.blockPosition();
        }

        return BlockPos.containing(subLevel.logicalPose().transformPositionInverse(player.position()));
    }
}
