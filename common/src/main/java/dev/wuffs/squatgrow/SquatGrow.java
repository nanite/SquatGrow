package dev.wuffs.squatgrow;

import dev.nanite.library.core.config.ConfigManager;
import dev.nanite.library.platform.Platform;
import dev.wuffs.squatgrow.actions.Actions;
import dev.wuffs.squatgrow.config.ComputedConfigValues;
import dev.wuffs.squatgrow.config.SquatGrowConfig;
import dev.wuffs.squatgrow.network.SquatGrowEnabledPacket;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;

import java.util.Map;

public class SquatGrow {
    public static final String MOD_ID = "squatgrow";

    public static void init() {
        Actions.init();
        Platform.INSTANCE.network().play2Server(SquatGrowEnabledPacket.TYPE, SquatGrowEnabledPacket.CODEC, ((payload, context) -> {
            SquatGrowPlatform.INSTANCE.setSquatGrowEnabled(context.player());
        }));

        ConfigManager.register(SquatGrowConfig.config);
        Platform.INSTANCE.registerDataPackReloadListener(Map.of(
                Identifier.fromNamespaceAndPath(MOD_ID, "squatgrow_config_updater"), new ReloadHandler()
        ));
    }

    static class ReloadHandler implements ResourceManagerReloadListener {
        @Override
        public void onResourceManagerReload(ResourceManager manager) {
            SquatGrowConfig.config.load();
            ComputedConfigValues.invalidate();
        }
    }
}
