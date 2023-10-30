package dev.wuffs.squatgrow.actions;

import net.minecraft.world.item.BoneMealItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.StemBlock;

import java.util.function.BooleanSupplier;

public class BoneMealAction implements Action {
    @Override
    public BooleanSupplier isAvailable() {
        return TRUE;
    }

    @Override
    public boolean canApply(ActionContext context) {
        Block block = context.state().getBlock();

        return (block instanceof BonemealableBlock && !(block instanceof StemBlock)) || (
                // StemBlocks can't be bone mealed when they're fully grown
                block instanceof StemBlock && context.state().getValue(StemBlock.AGE) != 7
        );
    }

    @Override
    public boolean execute(ActionContext context) {
        return BoneMealItem.growCrop(new ItemStack(Items.BONE_MEAL), context.level(), context.pos());
    }
}
