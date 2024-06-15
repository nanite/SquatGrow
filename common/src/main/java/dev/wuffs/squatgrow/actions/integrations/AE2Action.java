package dev.wuffs.squatgrow.actions.integrations;

import dev.architectury.platform.Platform;
import dev.wuffs.squatgrow.actions.ActionContext;
import dev.wuffs.squatgrow.actions.RandomTickableAction;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

import java.util.function.BooleanSupplier;

import static dev.wuffs.squatgrow.SquatGrow.config;

public class AE2Action extends RandomTickableAction {
    private static final TagKey<Block> AE2_TAG = TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("ae2", "growth_acceleratable"));

    @Override
    public BooleanSupplier isAvailable() {
        return () -> Platform.isModLoaded("ae2") && config.enableAE2Accelerator;
    }

    @Override
    public boolean canApply(ActionContext context) {
        return context.state().is(AE2_TAG);
    }

    @Override
    public int getMultiplier(ActionContext context) {
        return config.ae2Multiplier;
    }
}
