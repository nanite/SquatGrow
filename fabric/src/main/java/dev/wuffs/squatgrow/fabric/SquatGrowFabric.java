package dev.wuffs.squatgrow.fabric;

import com.mojang.serialization.Codec;
import dev.wuffs.squatgrow.Platform;
import dev.wuffs.squatgrow.SquatGrow;
import dev.wuffs.squatgrow.network.SquatGrowEnabledPacket;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.resources.Identifier;

import static dev.wuffs.squatgrow.SquatGrow.MOD_ID;

public class SquatGrowFabric implements ModInitializer {
    @SuppressWarnings("UnstableApiUsage")
    public static final AttachmentType<Boolean> SQUAT_GROW_ENABLED = AttachmentRegistry.<Boolean>builder()
        .initializer(() -> true)
        .persistent(Codec.BOOL)
        .copyOnDeath()
        .buildAndRegister(Identifier.fromNamespaceAndPath(MOD_ID, "squat_grow_enabled"));

    @Override
    public void onInitialize() {
        SquatGrow.init();

        PayloadTypeRegistry.serverboundPlay().register(SquatGrowEnabledPacket.TYPE, SquatGrowEnabledPacket.CODEC);
        ServerPlayNetworking.registerGlobalReceiver(SquatGrowEnabledPacket.TYPE, (payload, context) -> {
            Platform.INSTANCE.setSquatGrowEnabled(context.player());
        });
    }
}
