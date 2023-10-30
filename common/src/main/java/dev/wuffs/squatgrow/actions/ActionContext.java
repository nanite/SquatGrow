package dev.wuffs.squatgrow.actions;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public record ActionContext(
   Level level,
   BlockPos pos,
   BlockState state,
   ItemStack mainHand,
   ItemStack offhand,
   Player player
) {}
