package dev.wuffs.squatgrow.neoforge;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public class SquatPlatformImpl {
    public static void setSquatGrowEnabled(ServerPlayer player) {
        boolean isEnabled = isSquatGrowEnabled(player);
        player.setData(SquatGrowNeoForge.SQUAT_GROW_ENABLED.get(), !isEnabled);
        player.sendSystemMessage(Component.literal("Squat Grow " + (!isEnabled ? "Enabled" : "Disabled")), true);
    }

    public static boolean isSquatGrowEnabled(ServerPlayer player) {
        return player.getData(SquatGrowNeoForge.SQUAT_GROW_ENABLED.get());
    }
}
