package dev.wuffs.squatgrow.actions.integrations;

import dev.nanite.library.platform.Platform;
import dev.wuffs.squatgrow.actions.ActionContext;
import dev.wuffs.squatgrow.actions.RandomTickableAction;
import dev.wuffs.squatgrow.config.SquatGrowConfig;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

import java.util.function.BooleanSupplier;

public class AE2Action extends RandomTickableAction {
    private static final TagKey<Block> AE2_TAG = TagKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath("ae2", "growth_acceleratable"));

    @Override
    public BooleanSupplier isAvailable() {
        return () -> Platform.INSTANCE.isModLoaded("ae2") && SquatGrowConfig.enableAE2Accelerator.get();
    }

    @Override
    public boolean canApply(ActionContext context) {
        return context.state().is(AE2_TAG);
    }

    @Override
    public int getMultiplier(ActionContext context) {
        return SquatGrowConfig.ae2Multiplier.get();
    }
}
