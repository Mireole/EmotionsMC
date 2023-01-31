package fr.mireole.emotions.api.trigger;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import fr.mireole.emotions.api.TriggersActionPair;
import fr.mireole.emotions.client.screen.widgets.ActiveTriggersList;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

@OnlyIn(Dist.CLIENT)
public class ConditionTrigger extends BaseTrigger {
    private final String name;
    protected final transient Supplier<Boolean> condition; // TODO: Perhaps provide the player for better performance?
    private final transient TranslatableComponent translatableName;
    private transient TranslatableComponent description;

    public ConditionTrigger(String name, Supplier<Boolean> callable) {
        this(name, callable, new TranslatableComponent("trigger.emotions." + name), new TranslatableComponent("trigger.emotions." + name + ".description"));
    }

    public ConditionTrigger(String name, Supplier<Boolean> callable, TranslatableComponent translatableName, TranslatableComponent description) {
        this.name = name;
        this.condition = callable;
        this.translatableName = translatableName;
        this.description = description;
    }

    public boolean isActive() {
        return condition.get() ^ inverted;
    }

    public String getName() {
        return name;
    }

    public Component getTranslatableName() {
        return translatableName;
    }

    public Component getDescription() {
        return description;
    }

    public boolean isInverted() {
        return inverted;
    }

    public void invert() {
        this.setInverted(!inverted);
    }

    public void setInverted(boolean inverted) {
        this.inverted = inverted;
        this.description = new TranslatableComponent("trigger.emotions." + (inverted ? "not_" : "") + name + ".description");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ConditionTrigger trigger = (ConditionTrigger) o;
        return isInverted() == trigger.isInverted() && name.equals(trigger.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, isInverted());
    }

    @Override
    public String toString() {
        return name + ":" + (isInverted() ? "inverted" : "normal");
    }

    public @NotNull Trigger copy() {
        return new ConditionTrigger(name, condition, translatableName, description);
    }

    @Override
    public ActiveTriggersList.Entry getNewEntry(ActiveTriggersList activeTriggersList, TriggersActionPair pair, boolean chained) {
        return new Entry(activeTriggersList, this, pair, chained);
    }

    private static class Entry extends BaseTrigger.Entry<ConditionTrigger> {

        public Entry(ActiveTriggersList activeTriggersList, ConditionTrigger trigger, TriggersActionPair pair, boolean chained) {
            super(activeTriggersList, trigger, pair, chained);
        }
        @Override
        public @NotNull List<? extends NarratableEntry> narratables() {
            return ImmutableList.of(textWidget);
        }

        @Override
        public void render(@NotNull PoseStack pPoseStack, int pIndex, int pTop, int pLeft, int pWidth, int pHeight, int pMouseX, int pMouseY, boolean pIsMouseOver, float pPartialTick) {
            int i = activeTriggersList.screen.leftPos + activeTriggersList.screen.imageWidth - pLeft + 30;
            if (!chained) {
                textWidget.setWidth(i - 160);

                addButton.x = activeTriggersList.screen.leftPos + activeTriggersList.screen.imageWidth - removeButton.getWidth() - 116;
                addButton.y = pTop;
                addButton.render(pPoseStack, pMouseX, pMouseY, pPartialTick);
            } else {
                textWidget.setWidth(i - 138);
            }
            textWidget.x = pLeft - 30;
            textWidget.y = (int) (pTop + (float) (removeButton.getHeight() - activeTriggersList.screen.getFont().lineHeight) / 2); // Centering the text on the line
            textWidget.render(pPoseStack, pMouseX, pMouseY, pPartialTick);

            removeButton.x = activeTriggersList.screen.leftPos + activeTriggersList.screen.imageWidth - removeButton.getWidth() - 72;
            removeButton.y = pTop;
            removeButton.render(pPoseStack, pMouseX, pMouseY, pPartialTick);

            invertButton.x = activeTriggersList.screen.leftPos + activeTriggersList.screen.imageWidth - removeButton.getWidth() - 94;
            invertButton.y = pTop;
            invertButton.render(pPoseStack, pMouseX, pMouseY, pPartialTick);
        }

        @Override
        public @NotNull List<? extends GuiEventListener> children() {
            return ImmutableList.of(removeButton, invertButton, addButton, textWidget);
        }

        @Override
        public void tick() {
            super.tick();
            textWidget.tick();
        }

        @Override
        protected Component getDescription() {
            if (chained) {
                return new TranslatableComponent("emotions.screen.triggers.and", trigger.getDescription());
            }
            return trigger.getDescription();
        }
    }
}
