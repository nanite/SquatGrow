package dev.wuffs.squatgrow.fabric;

import dev.wuffs.squatgrow.SquatGrowPlatform;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

public class SquatGrowPlatformFabric implements SquatGrowPlatform {
    @Override
    public void setSquatGrowEnabled(Player player) {
        var isEnabled = isSquatGrowEnabled(player);

        player.setAttached(SquatGrowFabric.SQUAT_GROW_ENABLED, !isEnabled);
        player.sendOverlayMessage(Component.translatable(!isEnabled ? "squatgrow.enabled" : "squatgrow.disabled"));
    }

    @Override
    public boolean isSquatGrowEnabled(Player player) {
        Boolean isEnabled = player.getAttached(SquatGrowFabric.SQUAT_GROW_ENABLED);
        return isEnabled == null || isEnabled;
    }
}
