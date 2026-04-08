package dev.wuffs.squatgrow.actions;

import com.google.common.collect.ImmutableSet;
import dev.wuffs.squatgrow.actions.integrations.AE2Action;
import dev.wuffs.squatgrow.actions.integrations.MysticalAction;
import dev.wuffs.squatgrow.actions.special.DirtToGrassAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

public enum Actions {
    INSTANCE;

    private static final Logger LOGGER = LoggerFactory.getLogger(Actions.class);
    private final Set<Action> actions = Collections.synchronizedSet(new HashSet<>());

    Actions() {
    }

    public static void init() {
        Actions.get().register(RandomTickableAction::new);
        Actions.get().register(BoneMealAction::new);

        // Special actions
        Actions.get().register(DirtToGrassAction::new);

        // Register integrations
        Actions.get().register(MysticalAction::new);
        Actions.get().register(AE2Action::new);
    }

    public void register(Action action) {
        this.actions.add(action);
        LOGGER.info("Registered action: {}", action.getClass().getSimpleName());
    }

    public void register(Supplier<Action> action) {
        this.register(action.get());
    }

    public static Actions get() {
        return INSTANCE;
    }

    public ImmutableSet<Action> getActions() {
        return ImmutableSet.copyOf(this.actions);
    }
}
