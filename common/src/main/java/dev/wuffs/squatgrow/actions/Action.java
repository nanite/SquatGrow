package dev.wuffs.squatgrow.actions;

import java.util.function.BooleanSupplier;

public interface Action {
    BooleanSupplier TRUE = () -> true;
    BooleanSupplier FALSE = () -> false;

    /**
     * Only called once upon initial creation of the available actions list.
     * This is loaded after game setup and thus is safe to use mod loaded checking.
     * <p>
     * Running modded code here is not recommended unless you register your action only
     * when your mod is present.
     */
    BooleanSupplier isAvailable();

    /**
     * Called upon the action being available and the block is about to be actioned upon.
     *
     * @return if we should continue to apply the action.
     */
    boolean canApply(ActionContext context);

    boolean execute(ActionContext context);
}
