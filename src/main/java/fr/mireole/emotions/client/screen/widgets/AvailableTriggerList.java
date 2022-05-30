package fr.mireole.emotions.client.screen.widgets;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import fr.mireole.emotions.api.trigger.Trigger;
import fr.mireole.emotions.api.trigger.Triggers;
import fr.mireole.emotions.client.screen.TriggersScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

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
            if(screen.getFont().width(trigger.getComponent()) > maxWidth) {
                maxWidth = screen.getFont().width(trigger.getComponent());
            }
            addEntry(new Entry(trigger));
        }
    }

    public class Entry extends ContainerObjectSelectionList.Entry<AvailableTriggerList.Entry> {
        private final Button button;
        private final Trigger trigger;

        public Entry(Trigger trigger) {
            this.trigger = trigger;
            this.button = new Button(0, 0, screen.imageWidth / 2, 20, trigger.getComponent(), (button) -> {
                Triggers.enableTrigger(trigger, new Trigger.Action(null, null));
                screen.closeAvailableTriggersList();
            });
        }

        @Override
        public List<? extends NarratableEntry> narratables() {
            return ImmutableList.of();
        }

        @Override
        public List<? extends GuiEventListener> children() {
            return ImmutableList.of(button);
        }

        @Override
        public void render(PoseStack p_93523_, int p_93524_, int p_93525_, int p_93526_, int p_93527_, int p_93528_, int p_93529_, int p_93530_, boolean p_93531_, float p_93532_) {
            button.x = screen.leftPos + 150;
            button.y = p_93525_;
            button.render(p_93523_, p_93529_, p_93530_, p_93532_);
        }
    }

}
