package dev.wuffs.squatgrow.fabric;

import com.mojang.serialization.Codec;
import dev.wuffs.squatgrow.SquatGrow;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentSyncPredicate;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.resources.ResourceLocation;

import static dev.wuffs.squatgrow.SquatGrow.MOD_ID;

public class SquatGrowFabric implements ModInitializer {
    @SuppressWarnings("UnstableApiUsage")
    public static final AttachmentType<Boolean> SQUAT_GROW_ENABLED = AttachmentRegistry.create(
            ResourceLocation.fromNamespaceAndPath(MOD_ID, "squat_grow_enabled"),
            builder -> builder
                    .initializer(() -> true)
                    .persistent(Codec.BOOL)
                    .copyOnDeath()
                    .syncWith(ByteBufCodecs.BOOL, AttachmentSyncPredicate.targetOnly())
    );

    @Override
    public void onInitialize() {
        SquatGrow.init();
    }
}
