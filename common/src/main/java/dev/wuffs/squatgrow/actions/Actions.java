package dev.wuffs.squatgrow.actions;

import com.google.common.collect.ImmutableSet;
import dev.wuffs.squatgrow.actions.integrations.MysticalAction;
import dev.wuffs.squatgrow.actions.integrations.AE2Action;
import dev.wuffs.squatgrow.actions.special.DirtToGrassAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

public enum Actions {
    INSTANCE;

    private static final Logger LOGGER = LoggerFactory.getLogger(Actions.class);

    ImmutableSet<Action> actions;
    final Set<Supplier<Action>> possibleActions = new HashSet<>();

    Actions() {
        register(RandomTickableAction::new);
        register(BoneMealAction::new);

        // Special actions
        register(DirtToGrassAction::new);

        // Register integrations
        register(MysticalAction::new);
        register(AE2Action::new);
    }

    public void register(Supplier<Action> action) {
        this.possibleActions.add(action);
    }

    public void setup() {
        Set<Action> actions = new HashSet<>();
        for (Supplier<Action> action : this.possibleActions) {
            // Lazy load the action
            Action actualAction = action.get();
            if (actualAction.isAvailable().getAsBoolean()) {
                actions.add(actualAction);
                LOGGER.info("Registered action: {}", action.getClass().getSimpleName());
            }
        }

        // Create an immutable set
        this.actions = ImmutableSet.copyOf(actions);
    }

    public static Actions get() {
        return INSTANCE;
    }

    public ImmutableSet<Action> getActions() {
        return actions;
    }
}
