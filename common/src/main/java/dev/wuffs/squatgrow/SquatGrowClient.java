package dev.wuffs.squatgrow;

import dev.architectury.event.EventResult;
import dev.architectury.event.events.client.ClientRawInputEvent;
import dev.architectury.networking.NetworkManager;
import dev.architectury.registry.client.keymappings.KeyMappingRegistry;
import dev.wuffs.squatgrow.network.SquatGrowEnabledPacket;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import org.lwjgl.glfw.GLFW;

public class SquatGrowClient {
    private static final KeyMapping TOGGLE_KEY = new KeyMapping(
            "key.squatgrow.toggle",
            GLFW.GLFW_KEY_U,
            "key.categories.squatgrow"
    );

    public static void init() {
        KeyMappingRegistry.register(TOGGLE_KEY);
        ClientRawInputEvent.KEY_PRESSED.register(SquatGrowClient::inputEvent);
    }

    private static EventResult inputEvent(Minecraft client, int keyCode, int scanCode, int action, int modifiers) {
        if (TOGGLE_KEY.consumeClick()) {
            NetworkManager.sendToServer(new SquatGrowEnabledPacket());
        }

        return EventResult.pass();
    }
}
