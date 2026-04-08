package dev.wuffs.squatgrow;

import dev.nanite.library.client.platform.PlatformClient;
import dev.wuffs.squatgrow.network.SquatGrowEnabledPacket;
import net.minecraft.client.KeyMapping;
import net.minecraft.resources.Identifier;
import org.lwjgl.glfw.GLFW;

public class SquatGrowClient {
    public static final KeyMapping.Category SQUATGROW_CATEGORY = KeyMapping.Category.register(Identifier.fromNamespaceAndPath(SquatGrow.MOD_ID, "keys"));

    public static final KeyMapping TOGGLE_KEY = new KeyMapping(
            "key.squatgrow.toggle",
            GLFW.GLFW_KEY_U,
            SQUATGROW_CATEGORY
    );

    public static void onToggleBtnPressed() {
        PlatformClient.INSTANCE.sendPacketToServer(new SquatGrowEnabledPacket());
    }
}
