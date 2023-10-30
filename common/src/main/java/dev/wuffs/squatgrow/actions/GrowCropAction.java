package dev.wuffs.squatgrow.actions;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;

import java.util.function.BooleanSupplier;

public class GrowCropAction implements Action {
    /**
     * Not used by our mod but is likely helpful for others
     */
    @Override
    public BooleanSupplier isAvailable() {
        return FALSE;
    }

    @Override
    public boolean canApply(ActionContext context) {
        return context.state().getBlock() instanceof CropBlock;
    }

    @Override
    public boolean execute(ActionContext context) {
        Block block = context.state().getBlock();
        CropBlock crop = (CropBlock) block;

        crop.growCrops(context.level(), context.pos(), context.state());
        return true;
    }
}
