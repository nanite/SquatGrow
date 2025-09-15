package dev.wuffs.squatgrow.fabric;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public class SquatPlatformImpl {
    public static void setSquatGrowEnabled(ServerPlayer player) {
        var isEnabled = isSquatGrowEnabled(player);

        player.setAttached(SquatGrowFabric.SQUAT_GROW_ENABLED, !isEnabled);
        player.sendSystemMessage(Component.literal("Squat Grow " + (!isEnabled ? "Enabled" : "Disabled")), true);
    }

    public static boolean isSquatGrowEnabled(ServerPlayer player) {
        Boolean isEnabled = player.getAttached(SquatGrowFabric.SQUAT_GROW_ENABLED);
        return isEnabled == null || isEnabled;
    }
}
