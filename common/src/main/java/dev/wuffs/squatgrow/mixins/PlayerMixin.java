package dev.wuffs.squatgrow.mixins;

import dev.wuffs.squatgrow.SquatAction;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Debug(export = true)
@Mixin(Player.class)
public class PlayerMixin {
    @Unique boolean squatgrow$wasCrouchingLastTick = false;

    @Inject(method = "tick", at = @At("TAIL"))
    public void sg$onTickEnd(CallbackInfo ci) {
        if (((Object) this) instanceof Player player) {
            boolean crouching = player.isCrouching();
            boolean onGround = player.onGround();

            if (!onGround) return;

            if (!squatgrow$wasCrouchingLastTick && crouching) {
                SquatAction.performAction(player.level(), player);
            }

            squatgrow$wasCrouchingLastTick = crouching;
        }
    }
}
