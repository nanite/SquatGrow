package dev.wuffs.squatgrow.fabric;

import dev.wuffs.squatgrow.Platform;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;

import java.nio.file.Path;

public class PlatformFabric implements Platform {
    @Override
    public Path getConfigPath() {
        return FabricLoader.getInstance().getConfigDir();
    }

    @Override
    public boolean isModLoaded(String modId) {
        return FabricLoader.getInstance().isModLoaded(modId);
    }

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

    @Override
    public void sendPacketToServer(CustomPacketPayload packet) {
        ClientPlayNetworking.send(packet);
    }
}
