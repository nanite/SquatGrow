package dev.wuffs.squatgrow.neoforge;

import com.mojang.serialization.Codec;
import dev.wuffs.squatgrow.SquatGrow;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

@Mod(SquatGrow.MOD_ID)
public class SquatGrowNeoForge {
    private static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, SquatGrow.MOD_ID);
    public static final Supplier<AttachmentType<Boolean>> SQUAT_GROW_ENABLED = ATTACHMENT_TYPES.register(
            "squat_grow_enabled", () -> AttachmentType.builder(() -> true).serialize(Codec.BOOL).build()
    );

    public SquatGrowNeoForge(IEventBus modBus) {
        SquatGrow.init();
        ATTACHMENT_TYPES.register(modBus);
    }
}
