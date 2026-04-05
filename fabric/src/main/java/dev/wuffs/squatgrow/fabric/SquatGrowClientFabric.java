package dev.wuffs.squatgrow.fabric;

import dev.wuffs.squatgrow.SquatGrowClient;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

public class SquatGrowClientFabric implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (SquatGrowClient.TOGGLE_KEY.consumeClick()) {
                SquatGrowClient.onToggleBtnPressed();
            }
        });
    }
}
