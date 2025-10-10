package dev.wuffs.squatgrow.network;

import dev.wuffs.squatgrow.SquatGrow;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record SquatGrowEnabledPacket() implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<SquatGrowEnabledPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(SquatGrow.MOD_ID, "squat_grow_enabled"));

    public static StreamCodec<ByteBuf, SquatGrowEnabledPacket> CODEC = StreamCodec.unit(new SquatGrowEnabledPacket());

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
