package fr.mireole.emotions.api.trigger;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonObject;
import com.mojang.blaze3d.vertex.PoseStack;
import fr.mireole.emotions.api.TriggersActionPair;
import fr.mireole.emotions.client.KeyMappings;
import fr.mireole.emotions.client.screen.widgets.ActiveTriggersList;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@OnlyIn(Dist.CLIENT)
public class KeyTrigger extends BaseTrigger {
    private transient KeyMapping key;
    private int triggerKey = 0;
    private static final TranslatableComponent TRANSLATABLE_NAME = new TranslatableComponent("trigger.emotions.key_pressed");

    public KeyTrigger() {
        this(null);
    }

    public KeyTrigger(KeyMapping key) {
        this.key = key;
    }

    private void updateKey() {
        if (key == null) {
            key = KeyMappings.triggerKeys[triggerKey];
        }
    }

    @Override
    public boolean isActive() {
        updateKey();
        return key.isDown() ^ inverted;
    }

    @Override
    public String getName() {
        return "key_pressed";
    }

    @Override
    public Component getTranslatableName() {
        return TRANSLATABLE_NAME;
    }

    @Override
    public Component getDescription() {
        updateKey();
        return new TranslatableComponent("trigger.emotions." + (inverted ? "not_" : "") + "key_pressed.description");
    }

    @Override
    public @NotNull Trigger copy() {
        updateKey();
        KeyTrigger trigger = new KeyTrigger(key);
        trigger.setInverted(inverted);
        trigger.updateKey();
        return trigger;
    }

    @Override
    public void setInverted(boolean inverted) {
        super.setInverted(inverted);
    }

    @Override
    public ActiveTriggersList.Entry getNewEntry(ActiveTriggersList activeTriggersList, TriggersActionPair pair, boolean chained) {
        updateKey();
        return new KeyTrigger.Entry(activeTriggersList, this, pair, chained);
    }

    public void cycleKey() {
        updateKey();
        triggerKey = (triggerKey + 1) % KeyMappings.triggerKeys.length;
        key = KeyMappings.triggerKeys[triggerKey];
    }

    @SuppressWarnings("unused")
    public static Trigger deserialize(JsonObject json) {
        KeyMapping key = KeyMappings.triggerKeys[json.get("triggerKey").getAsInt()];
        KeyTrigger trigger = new KeyTrigger(key);
        trigger.triggerKey = json.get("triggerKey").getAsInt();
        trigger.setInverted(json.get("inverted").getAsBoolean());
        return trigger;
    }

    public static class Entry extends BaseTrigger.Entry<KeyTrigger> {
        private final ImageButton cycleButton;


        public Entry(ActiveTriggersList activeTriggersList, KeyTrigger trigger, TriggersActionPair pair, boolean chained) {
            super(activeTriggersList, trigger, pair, chained);
            cycleButton = new ImageButton(0, 0, 20, 20, 0, 0, 20, ActiveTriggersList.CYCLE_BUTTON, 32, 64, (pButton) -> trigger.cycleKey(), new TranslatableComponent("emotions.screen.triggers.cycle_key"));
        }

        @Override
        protected Component getDescription() {
            if (chained) {
                return new TranslatableComponent("emotions.screen.triggers.and", trigger.getDescription());
            }
            return trigger.getDescription();
        }

        @Override
        public @NotNull List<? extends NarratableEntry> narratables() {
            return ImmutableList.of();
        }

        @Override
        public void render(@NotNull PoseStack pPoseStack, int pIndex, int pTop, int pLeft, int pWidth, int pHeight, int pMouseX, int pMouseY, boolean pIsMouseOver, float pPartialTick) {
            int i = activeTriggersList.screen.leftPos + activeTriggersList.screen.imageWidth - pLeft + 30;
            if (!chained) {
                textWidget.setWidth(i - 162);

                addButton.x = activeTriggersList.screen.leftPos + activeTriggersList.screen.imageWidth - removeButton.getWidth() - 116;
                addButton.y = pTop;
                addButton.render(pPoseStack, pMouseX, pMouseY, pPartialTick);

                cycleButton.x = activeTriggersList.screen.leftPos + activeTriggersList.screen.imageWidth - removeButton.getWidth() - 138;
            } else {
                textWidget.setWidth(i - 142);
                cycleButton.x = activeTriggersList.screen.leftPos + activeTriggersList.screen.imageWidth - removeButton.getWidth() - 116;
            }
            textWidget.x = pLeft - 30;
            textWidget.y = (int) (pTop + (float) (removeButton.getHeight() - activeTriggersList.screen.getFont().lineHeight) / 2); // Centering the text on the line
            textWidget.render(pPoseStack, pMouseX, pMouseY, pPartialTick);

            cycleButton.y = pTop;
            cycleButton.render(pPoseStack, pMouseX, pMouseY, pPartialTick);

            removeButton.x = activeTriggersList.screen.leftPos + activeTriggersList.screen.imageWidth - removeButton.getWidth() - 72;
            removeButton.y = pTop;
            removeButton.render(pPoseStack, pMouseX, pMouseY, pPartialTick);

            invertButton.x = activeTriggersList.screen.leftPos + activeTriggersList.screen.imageWidth - removeButton.getWidth() - 94;
            invertButton.y = pTop;
            invertButton.render(pPoseStack, pMouseX, pMouseY, pPartialTick);
        }

        @Override
        public @NotNull List<? extends GuiEventListener> children() {
            return ImmutableList.of(removeButton, invertButton, addButton, cycleButton);
        }
    }
}