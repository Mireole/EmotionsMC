package fr.mireole.emotions.api;

import fr.mireole.emotions.api.action.Action;
import fr.mireole.emotions.api.trigger.Trigger;
import fr.mireole.emotions.api.trigger.Triggers;

import java.util.LinkedList;

/**
 * This class is used to pair multiple triggers and an action together in a practical way.
 */
public class TriggersActionPair {
    private final LinkedList<Trigger> triggers;
    private final Action action;

    public TriggersActionPair(LinkedList<Trigger> triggers, Action action) {
        this.triggers = new LinkedList<>(triggers);
        this.action = action;
    }

    public TriggersActionPair(Trigger trigger, Action action) {
        this.triggers = new LinkedList<>();
        this.triggers.add(trigger);
        this.action = action;
    }

    public LinkedList<Trigger> getTriggers() {
        return triggers;
    }

    public Action getAction() {
        return action;
    }

    public void removeTrigger(Trigger trigger) {
        triggers.remove(trigger);
        if (triggers.isEmpty()) {
            Triggers.disableTrigger(this);
        }
    }

    public void addTrigger(Trigger trigger) {
        triggers.add(trigger.copy());
    }

}
