package dev.wuffs.squatgrow.actions;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CactusBlock;
import net.minecraft.world.level.block.StemBlock;
import net.minecraft.world.level.block.SugarCaneBlock;

import java.util.function.BooleanSupplier;

import static dev.wuffs.squatgrow.SquatGrow.config;

public class RandomTickableAction implements Action {
    @Override
    public BooleanSupplier isAvailable() {
        return TRUE;
    }

    @Override
    public boolean canApply(ActionContext context) {
        Block block = context.state().getBlock();
        return block instanceof SugarCaneBlock || block instanceof CactusBlock || (
            // StemBlocks can't be random ticked when they're not fully grown but can be when they are
            block instanceof StemBlock && context.state().getValue(StemBlock.AGE) == 7
        );
    }

    @Override
    public boolean execute(ActionContext context) {
        for (int i = 0; i < this.getMultiplier(context); i++) {
            context.state()
                    .randomTick(((ServerLevel) context.level()), context.pos(), context.level().getRandom());
        }

        return true;
    }

    public int getMultiplier(ActionContext context) {
        // If the old legacy config is using the default value of 4, use the new randomTickMultiplier
        return config.sugarcaneMultiplier == 4 ? config.randomTickMultiplier : config.sugarcaneMultiplier;
    }
}
