package dev.wuffs.squatgrow;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;

import java.nio.file.Path;
import java.util.ServiceLoader;

public interface Platform {
    Platform INSTANCE = ServiceLoader.load(Platform.class).findFirst().orElseThrow(() -> new RuntimeException("No platform implementation found"));

    Path getConfigPath();

    boolean isModLoaded(String modId);

    void setSquatGrowEnabled(Player player);
    boolean isSquatGrowEnabled(Player player);

    void sendPacketToServer(CustomPacketPayload packet);
}
