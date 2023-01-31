package fr.mireole.emotions.api.trigger;

import fr.mireole.emotions.api.TriggersActionPair;
import fr.mireole.emotions.client.screen.widgets.ActiveTriggersList;
import fr.mireole.emotions.client.screen.widgets.ScrollingTextWidget;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;

public abstract class BaseTrigger extends Trigger {

    public abstract static class Entry<T extends BaseTrigger> extends ActiveTriggersList.Entry {
        protected final ActiveTriggersList activeTriggersList;
        protected final T trigger;
        protected final TriggersActionPair pair;
        protected final boolean chained;
        protected final ImageButton removeButton;
        protected final ImageButton invertButton;
        protected final ImageButton addButton;
        protected final ScrollingTextWidget textWidget;
        
        public Entry(ActiveTriggersList activeTriggersList, T trigger, TriggersActionPair pair, boolean chained) {
            this.activeTriggersList = activeTriggersList;
            this.trigger = trigger;
            this.pair = pair;
            this.chained = chained;

            removeButton = new ImageButton(0, 0, 20, 20, 0, 0, 20, ActiveTriggersList.REMOVE_BUTTON, 32, 64, (pButton) -> {
                this.pair.removeTrigger(this.trigger);
                activeTriggersList.update();
            }, new TranslatableComponent("emotions.screen.triggers.remove"));

            invertButton = new ImageButton(0, 0, 20, 20, 0, 0, 20, ActiveTriggersList.INVERT_BUTTON, 32, 64, (pButton) -> {
                this.trigger.invert();
                activeTriggersList.update();
            }, new TranslatableComponent("emotions.screen.triggers.add"));

            addButton = new ImageButton(0, 0, 20, 20, 0, 0, 20, ActiveTriggersList.ADD_BUTTON, 32, 64, (pButton) -> activeTriggersList.screen.openAvailableTriggersList(pair), new TranslatableComponent("emotions.screen.triggers.invert"));

            textWidget = new ScrollingTextWidget(getDescription());

        }

        protected abstract Component getDescription();

        // TODO: Put the render method here (might require some serious changes to the existing code)
    }

}
