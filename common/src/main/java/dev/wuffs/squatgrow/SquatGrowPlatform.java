package dev.wuffs.squatgrow;

import net.minecraft.world.entity.player.Player;

import java.util.ServiceLoader;

public interface SquatGrowPlatform {
    SquatGrowPlatform INSTANCE = ServiceLoader.load(SquatGrowPlatform.class).findFirst().orElseThrow(() -> new RuntimeException("No platform implementation found"));

    void setSquatGrowEnabled(Player player);
    boolean isSquatGrowEnabled(Player player);
}
