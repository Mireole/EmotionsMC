package fr.mireole.emotions.client.screen.widgets;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import fr.mireole.emotions.Emotions;
import fr.mireole.emotions.api.action.Action;
import fr.mireole.emotions.api.trigger.Trigger;
import fr.mireole.emotions.api.trigger.Triggers;
import fr.mireole.emotions.client.screen.TriggersScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashMap;
import java.util.List;

public class ActiveTriggersList extends ContainerObjectSelectionList<ActiveTriggersList.Entry> {
    public static final ResourceLocation INVERT_BUTTON = new ResourceLocation(Emotions.MOD_ID, "textures/gui/invert_button.png");
    private final TriggersScreen screen;

    public ActiveTriggersList(TriggersScreen screen, Minecraft pMinecraft, int pWidth, int pHeight, int pY0, int pY1, int pItemHeight) {
        super(pMinecraft, pWidth, pHeight, pY0, pY1, pItemHeight);
        this.screen = screen;
        setRenderBackground(false);
        setRenderTopAndBottom(false);
    }

    public void update() {
        this.clearEntries();
        LinkedHashMap<Trigger, Action> enabledTriggers = Triggers.getEnabledTriggers();
        enabledTriggers.forEach((trigger, action) -> addEntry(new Entry(trigger, action)));

    }

    public void tick(){
        children().forEach(Entry::tick);
    }

    public class Entry extends ContainerObjectSelectionList.Entry<ActiveTriggersList.Entry> {
        private final Trigger trigger;
        private final Action action;
        private final Button removeButton;
        private final ImageButton invertButton;

        public Entry(Trigger trigger, Action action) {
            this.trigger = trigger;
            this.action = action;
            removeButton = new Button(0, 0, screen.getFont().width(new TranslatableComponent("emotions.screen.triggers.remove")) + 8, 20, new TranslatableComponent("emotions.screen.triggers.remove"), (pButton) -> {
                Triggers.disableTrigger(this.trigger, this.action);
                update();
            });
            invertButton = new ImageButton(0, 0, 20, 20, 0, 0, 20, INVERT_BUTTON, 32, 64, (pButton) -> {
                this.trigger.invert();
                update();
            }, new TranslatableComponent("emotions.screen.triggers.invert"));
        }

        @Override
        public @NotNull List<? extends NarratableEntry> narratables() {
            return ImmutableList.of();
        }

        @Override
        public void render(@NotNull PoseStack pPoseStack, int pIndex, int pTop, int pLeft, int pWidth, int pHeight, int pMouseX, int pMouseY, boolean pIsMouseOver, float pPartialTick) {
            if (pTop + 25 < y0 || pTop - 25 + pHeight > y1) { // Prevents rendering outside the list (22: line height, total entry  height / 2): make this cleaner because it looks ugly in game
                return;
            }

            // First line
            if (!(pTop < y0)) { // Only render if it is not out of the list
                screen.getFont().draw(pPoseStack, trigger.getDescription(), pLeft - 30, pTop + (float) (removeButton.getHeight() - screen.getFont().lineHeight) / 2, 4210752); // Trigger name

                removeButton.x = screen.leftPos + screen.imageWidth - removeButton.getWidth() - 72;
                removeButton.y = pTop;
                removeButton.render(pPoseStack, pMouseX, pMouseY, pPartialTick);

                invertButton.x = screen.leftPos + screen.imageWidth - removeButton.getWidth() - 94;
                invertButton.y = pTop;
                invertButton.render(pPoseStack, pMouseX, pMouseY, pPartialTick);
            }

            // Second line
            if (!(pTop + pHeight > y1)) { // Only render if it is not out of the list
                action.setComponentPos(screen, pIndex, pLeft, pTop, pWidth, pHeight);
                action.render(pPoseStack, pMouseX, pMouseY, pPartialTick);
            }

        }

        @Override
        public @NotNull List<? extends GuiEventListener> children() {
            return ImmutableList.of(removeButton, invertButton, action);
        }

        public void tick(){
            action.tick();
        }

    }

}
