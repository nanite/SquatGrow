package dev.wuffs.squatgrow.neoforge;

import dev.wuffs.squatgrow.Platform;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLPaths;

import java.nio.file.Path;

public class PlatformNeoForge implements Platform {
    @Override
    public Path getConfigPath() {
        return FMLPaths.CONFIGDIR.get();
    }

    @Override
    public boolean isModLoaded(String modId) {
        return ModList.get().isLoaded(modId);
    }

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

    @Override
    public void sendPacketToServer(CustomPacketPayload packet) {
        Payload
    }
}
