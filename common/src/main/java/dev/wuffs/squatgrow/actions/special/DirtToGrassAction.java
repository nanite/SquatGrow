package dev.wuffs.squatgrow.actions.special;

import dev.wuffs.squatgrow.actions.Action;
import dev.wuffs.squatgrow.actions.ActionContext;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;

import static dev.wuffs.squatgrow.SquatGrow.config;
import java.util.function.BooleanSupplier;

/**
 * Special action that will allow you to grow grass when standing on dirt
 * if the player has a grass block in their offhand.
 */
public class DirtToGrassAction implements Action {
    @Override
    public BooleanSupplier isAvailable() {
        return () -> config.enableDirtToGrass;
    }

    @Override
    public boolean canApply(ActionContext context) {
        return context.offhand().is(Items.GRASS_BLOCK) && context.state().is(Blocks.DIRT);
    }

    @Override
    public boolean execute(ActionContext context) {
        context.level().setBlockAndUpdate(context.pos(), Blocks.GRASS_BLOCK.defaultBlockState());
        return true;
    }
}
