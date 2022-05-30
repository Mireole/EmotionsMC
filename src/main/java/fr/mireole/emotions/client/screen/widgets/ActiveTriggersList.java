package fr.mireole.emotions.client.screen.widgets;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import fr.mireole.emotions.api.trigger.Trigger;
import fr.mireole.emotions.api.trigger.Triggers;
import fr.mireole.emotions.client.screen.TriggersScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ActiveTriggersList extends ContainerObjectSelectionList<ActiveTriggersList.Entry> {
    private final TriggersScreen screen;

    public ActiveTriggersList(TriggersScreen screen, Minecraft p_94010_, int p_94011_, int p_94012_, int p_94013_, int p_94014_, int p_94015_) {
        super(p_94010_, p_94011_, p_94012_, p_94013_, p_94014_, p_94015_);
        this.screen = screen;
        setRenderBackground(false);
        setRenderTopAndBottom(false);
    }

    public void update() {
        this.clearEntries();
        ArrayListMultimap<Trigger, Trigger.Action> enabledTriggers = Triggers.getEnabledTriggers();
        enabledTriggers.forEach((trigger, action) -> addEntry(new Entry(trigger, action)));

    }

    public class Entry extends ContainerObjectSelectionList.Entry<ActiveTriggersList.Entry> {
        private final Trigger trigger;
        private final Trigger.Action action;
        private final Button removeButton;

        public Entry(Trigger trigger, Trigger.Action action) {
            this.trigger = trigger;
            this.action = action;
            removeButton = new Button(0, 0, 50, 20, new TranslatableComponent("emotions.screen.triggers.remove"), (pButton) -> {
                Triggers.disableTrigger(this.trigger, this.action);
                update();
            });
        }

        @Override
        public @NotNull List<? extends NarratableEntry> narratables() {
            return ImmutableList.of();
        }

        @Override
        public void render(@NotNull PoseStack pPoseStack, int pIndex, int pTop, int pLeft, int pWidth, int pHeight, int pMouseX, int pMouseY, boolean pIsMouseOver, float pPartialTick) {
            if (pTop < y0 || pTop + pHeight > y1) { // Prevents rendering outside the list
                return;
            }
            screen.getFont().draw(pPoseStack, trigger.getName(), pLeft + 10, pTop + (float)(removeButton.getHeight() - screen.getFont().lineHeight) / 2, 4210752); // Trigger name

            removeButton.x = screen.leftPos + screen.imageWidth - removeButton.getWidth() - 64;
            removeButton.y = pTop;
            removeButton.render(pPoseStack, pMouseX, pMouseY, pPartialTick);
        }

        @Override
        public @NotNull List<? extends GuiEventListener> children() {
            return ImmutableList.of(removeButton);
        }
    }

}
