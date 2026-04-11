package dev.wuffs.squatgrow.actions.integrations;

import dev.nanite.library.platform.Platform;
import dev.wuffs.squatgrow.actions.ActionContext;
import dev.wuffs.squatgrow.actions.GrowCropAction;
import dev.wuffs.squatgrow.config.SquatGrowConfig;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

import java.util.function.BooleanSupplier;

public class MysticalAction extends GrowCropAction {
    private static final TagKey<Block> MYSTICAL_TAG = TagKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath("mysticalagriculture", "crops"));

    @Override
    public BooleanSupplier isAvailable() {
        return () -> Platform.INSTANCE.isModLoaded("mysticalagriculture") && SquatGrowConfig.enableMysticalCrops.get();
    }

    @Override
    public boolean canApply(ActionContext context) {
        return super.canApply(context) && context.state().is(MYSTICAL_TAG);
    }
}
