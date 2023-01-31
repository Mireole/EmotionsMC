package fr.mireole.emotions.client.screen.widgets;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import fr.mireole.emotions.Emotions;
import fr.mireole.emotions.api.TriggersActionPair;
import fr.mireole.emotions.api.action.Action;
import fr.mireole.emotions.api.trigger.Triggers;
import fr.mireole.emotions.client.screen.TriggersScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class ActiveTriggersList extends ContainerObjectSelectionList<ActiveTriggersList.Entry> {
    public static final ResourceLocation INVERT_BUTTON = new ResourceLocation(Emotions.MOD_ID, "textures/gui/invert_button.png");
    public static final ResourceLocation REMOVE_BUTTON = new ResourceLocation(Emotions.MOD_ID, "textures/gui/remove_button.png");
    public static final ResourceLocation ADD_BUTTON = new ResourceLocation(Emotions.MOD_ID, "textures/gui/add_button.png");
    public static final ResourceLocation CYCLE_BUTTON = new ResourceLocation(Emotions.MOD_ID, "textures/gui/cycle_button.png");
    public final TriggersScreen screen;

    public ActiveTriggersList(TriggersScreen screen, Minecraft pMinecraft, int pWidth, int pHeight, int pY0, int pY1, int pItemHeight) {
        super(pMinecraft, pWidth, pHeight, pY0, pY1, pItemHeight);
        this.screen = screen;
        setRenderBackground(false);
        setRenderTopAndBottom(false);
    }

    public void update() {
        this.clearEntries();
        LinkedList<TriggersActionPair> enabledTriggers = Triggers.getEnabledTriggers();
        final AtomicBoolean first = new AtomicBoolean(true);
        enabledTriggers.forEach((pair) -> {
            if (first.get()) {
                first.set(false);
            } else {
                addEntry(new SeparatorEntry());
            }
            final AtomicBoolean chained = new AtomicBoolean(false);
            pair.getTriggers().forEach((trigger) -> {
                addEntry(trigger.getNewEntry(this, pair, chained.get()));
                chained.set(true);
            });
            addEntry(new ActionEntry(pair.getAction()));
        });

    }

    public void tick() {
        children().forEach(ActiveTriggersList.Entry::tick);
    }

    public abstract static class Entry extends ContainerObjectSelectionList.Entry<Entry> {
        public void tick() {

        }

    }

    public class ActionEntry extends Entry {
        private final Action action;

        public ActionEntry(Action action) {
            this.action = action;
        }

        @Override
        public @NotNull List<? extends NarratableEntry> narratables() {
            return ImmutableList.of();
        }

        @Override
        public void render(@NotNull PoseStack pPoseStack, int pIndex, int pTop, int pLeft, int pWidth, int pHeight, int pMouseX, int pMouseY, boolean pIsMouseOver, float pPartialTick) {
            action.setComponentPos(screen, pIndex, pLeft, pTop, pWidth, pHeight);
            action.render(pPoseStack, pMouseX, pMouseY, pPartialTick);
        }

        @Override
        public @NotNull List<? extends GuiEventListener> children() {
            return ImmutableList.of();
        }

        @Override
        public void tick() {
            action.tick();
        }

        @Override
        public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
            return action.mouseClicked(pMouseX, pMouseY, pButton);
        }

        @Override
        public boolean keyPressed(int pKeyCode, int pScanCode, int pModifiers) {
            return action.keyPressed(pKeyCode, pScanCode, pModifiers);
        }

        @Override
        public boolean charTyped(char pCodePoint, int pModifiers) {
            return action.charTyped(pCodePoint, pModifiers);
        }

    }

    public static class SeparatorEntry extends Entry {
        @Override
        public void tick() {

        }

        @Override
        public @NotNull List<? extends NarratableEntry> narratables() {
            return ImmutableList.of();
        }

        @Override
        public void render(@NotNull PoseStack pPoseStack, int pIndex, int pTop, int pLeft, int pWidth, int pHeight, int pMouseX, int pMouseY, boolean pIsMouseOver, float pPartialTick) {

        }

        @Override
        public @NotNull List<? extends GuiEventListener> children() {
            return ImmutableList.of();
        }
    }

}
