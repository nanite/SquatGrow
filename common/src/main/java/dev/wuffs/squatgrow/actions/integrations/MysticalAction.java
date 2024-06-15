package dev.wuffs.squatgrow.actions.integrations;

import dev.architectury.platform.Platform;
import dev.wuffs.squatgrow.actions.ActionContext;
import dev.wuffs.squatgrow.actions.GrowCropAction;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

import java.util.function.BooleanSupplier;

import static dev.wuffs.squatgrow.SquatGrow.config;

public class MysticalAction extends GrowCropAction {
    private static final TagKey<Block> MYSTICAL_TAG = TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("mysticalagriculture", "crops"));

    @Override
    public BooleanSupplier isAvailable() {
        return () -> Platform.isModLoaded("mysticalagriculture") && config.enableMysticalCrops;
    }

    @Override
    public boolean canApply(ActionContext context) {
        return super.canApply(context) && context.state().is(MYSTICAL_TAG);
    }
}
