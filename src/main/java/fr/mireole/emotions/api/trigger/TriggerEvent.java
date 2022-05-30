package fr.mireole.emotions.api.trigger;


import net.minecraftforge.eventbus.api.Event;

public class TriggerEvent extends Event {

    public static class TriggerFiredEvent extends TriggerEvent {
        public final Trigger trigger;

        public TriggerFiredEvent(Trigger trigger) {
            this.trigger = trigger;
        }
    }

    public static class TriggerReleasedEvent extends TriggerEvent {
        public final Trigger trigger;

        public TriggerReleasedEvent(Trigger trigger) {
            this.trigger = trigger;
        }
    }

}
