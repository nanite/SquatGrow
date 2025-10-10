package dev.wuffs.squatgrow;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.server.level.ServerPlayer;

public class SquatPlatform {
    @ExpectPlatform
    public static void setSquatGrowEnabled(ServerPlayer player) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static boolean isSquatGrowEnabled(ServerPlayer player) {
        throw new AssertionError();
    }
}
