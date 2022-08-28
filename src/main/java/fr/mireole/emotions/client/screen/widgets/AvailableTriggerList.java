package fr.mireole.emotions.client.screen.widgets;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import fr.mireole.emotions.api.action.SkinAction;
import fr.mireole.emotions.api.trigger.Trigger;
import fr.mireole.emotions.api.trigger.Triggers;
import fr.mireole.emotions.client.screen.TriggersScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public class AvailableTriggerList extends ContainerObjectSelectionList<AvailableTriggerList.Entry> {
    private final TriggersScreen screen;
    private int maxWidth;
    public AvailableTriggerList(TriggersScreen screen, Minecraft minecraft, int width, int height, int y0, int y1, int itemHeight) {
        super(minecraft, width, height, y0, y1, itemHeight);
        setRenderBackground(false);
        setRenderTopAndBottom(false);
        this.screen = screen;
        clearEntries();
        for(Trigger trigger : Triggers.getTriggers()) {
            if(screen.getFont().width(trigger.getTranslatableName()) > maxWidth) {
                maxWidth = screen.getFont().width(trigger.getTranslatableName());
            }
            addEntry(new Entry(trigger, Triggers.getEnabledTriggers().containsKey(trigger)));
        }
    }

    public void reRenderTooltips(PoseStack poseStack, int mouseX, int mouseY) {
        children().forEach((widget) -> {
            if (widget != null) {
                widget.reRenderButtonTooltips(poseStack, mouseX, mouseY);
            }
        });
    }

    public class Entry extends ContainerObjectSelectionList.Entry<AvailableTriggerList.Entry> {
        private final Button button;

        public Entry(Trigger trigger, boolean disabled) {
            this.button = new Button(0, 0, screen.imageWidth / 2, 20, trigger.getTranslatableName(), (button) -> {
                Triggers.enableTrigger(trigger, new SkinAction(null));
                screen.closeAvailableTriggersList();
            }, (pButton, pPoseStack, pMouseX, pMouseY) -> {
                if (disabled) {
                    List<Component> components = new ArrayList<>();
                    components.add(new TranslatableComponent("emotions.triggers.disabled_line_1"));
                    components.add(new TranslatableComponent("emotions.triggers.disabled_line_2"));
                    screen.renderComponentTooltip(pPoseStack, components, pMouseX, pMouseY);
                }
            });
            this.button.active = !disabled;
        }

        @Override
        public @NotNull List<? extends NarratableEntry> narratables() {
            return ImmutableList.of();
        }

        @Override
        public @NotNull List<? extends GuiEventListener> children() {
            return ImmutableList.of(button);
        }

        @Override
        public void render(@NotNull PoseStack pPoseStack, int pIndex, int pTop, int pLeft, int pWidth, int pHeight, int pMouseX, int pMouseY, boolean pIsMouseOver, float pPartialTick) {
            button.x = screen.leftPos + 150;
            button.y = pTop;
            button.render(pPoseStack, pMouseX, pMouseY, pPartialTick);
        }

        public void reRenderButtonTooltips(PoseStack poseStack, int mouseX, int mouseY) {
            this.children().forEach((widget) -> {
                if (widget instanceof Button button) {
                    if (button.isHoveredOrFocused()) {
                        button.renderToolTip(poseStack, mouseX, mouseY);
                    }
                }
            });
        }

    }

}
