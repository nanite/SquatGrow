package dev.wuffs.squatgrow.neoforge;

import dev.wuffs.squatgrow.SquatGrowPlatform;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

public class SquatGrowPlatformNeoForge implements SquatGrowPlatform {
    @Override
    public void setSquatGrowEnabled(Player player) {
        boolean isEnabled = isSquatGrowEnabled(player);
        player.setData(SquatGrowNeoForge.SQUAT_GROW_ENABLED.get(), !isEnabled);
        player.sendOverlayMessage(Component.translatable(!isEnabled ? "squatgrow.enabled" : "squatgrow.disabled"));
    }

    @Override
    public boolean isSquatGrowEnabled(Player player) {
        return player.getData(SquatGrowNeoForge.SQUAT_GROW_ENABLED.get());
    }
}
