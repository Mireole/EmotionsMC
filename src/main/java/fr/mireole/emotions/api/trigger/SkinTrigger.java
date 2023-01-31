package fr.mireole.emotions.api.trigger;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonObject;
import com.mojang.blaze3d.vertex.PoseStack;
import fr.mireole.emotions.api.TriggersActionPair;
import fr.mireole.emotions.api.skin.Skin;
import fr.mireole.emotions.api.skin.SkinManager;
import fr.mireole.emotions.client.screen.widgets.ActiveTriggersList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.io.FilenameUtils;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@OnlyIn(Dist.CLIENT)
public class SkinTrigger extends BaseTrigger {
    private String skinName;
    private static final Component TRANSLATABLE_NAME = new TranslatableComponent("trigger.emotions.skin_used");

    public SkinTrigger() {
        this(null);
    }

    public SkinTrigger(String skinName) {
        if (skinName == null) {
            skinName = "default";
        }
        this.skinName = skinName;
    }

    @Override
    public boolean isActive() {
        Skin skin = SkinManager.getCurrentSkin();
        if (skin == null) {
            return false;
        }
        return skin.getName().equals(skinName) ^ inverted;
    }

    @Override
    public String getName() {
        return "skin_used";
    }

    @Override
    public Component getTranslatableName() {
        return TRANSLATABLE_NAME;
    }

    @Override
    public Component getDescription() {
        return new TranslatableComponent("trigger.emotions." + (inverted ? "not_" : "") + "skin_used.description", FilenameUtils.removeExtension(skinName));
    }

    @Override
    public @NotNull Trigger copy() {
        SkinTrigger trigger = new SkinTrigger();
        trigger.skinName = skinName;
        trigger.inverted = inverted;
        return trigger;
    }

    @Override
    public ActiveTriggersList.Entry getNewEntry(ActiveTriggersList activeTriggersList, TriggersActionPair pair, boolean chained) {
        return new Entry(activeTriggersList, this, pair, chained);
    }

    public void cycleSkin() {
        List<Skin> skins = SkinManager.getSkins();
        skins.add(SkinManager.getDefaultSkin(Minecraft.getInstance().player));
        int index = skins.indexOf(getSkin());
        setSkinName(skins.get((index + 1) % skins.size()).getName());
    }

    public void setSkinName(String skinName) {
        this.skinName = skinName;
    }

    public Skin getSkin() {
        return SkinManager.getSkin(skinName) == null ? SkinManager.getDefaultSkin(Minecraft.getInstance().player) : SkinManager.getSkin(skinName);
    }

    @SuppressWarnings("unused")
    public static Trigger deserialize(JsonObject json) {
        SkinTrigger trigger = new SkinTrigger();
        trigger.skinName = json.get("skinName").getAsString();
        if (SkinManager.getSkin(trigger.skinName) == null) {
            List<Skin> skins = SkinManager.getSkins();
            trigger.skinName = skins.get(0).getName();
        }
        trigger.inverted = json.get("inverted").getAsBoolean();
        return trigger;
    }

    public static class Entry extends BaseTrigger.Entry<SkinTrigger> {
        private final ImageButton cycleButton;

        public Entry(ActiveTriggersList activeTriggersList, SkinTrigger trigger, TriggersActionPair pair, boolean chained) {
            super(activeTriggersList, trigger, pair, chained);
            cycleButton = new ImageButton(0, 0, 20, 20, 0, 0, 20, ActiveTriggersList.CYCLE_BUTTON, 32, 64, (pButton) -> {
                trigger.cycleSkin();
                activeTriggersList.update();
            }, new TranslatableComponent("emotions.screen.triggers.cycle_skin"));
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
            return ImmutableList.of(textWidget);
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
            return ImmutableList.of(removeButton, invertButton, addButton, cycleButton, textWidget);
        }

        @Override
        public void tick() {
            textWidget.tick();
        }
    }
}
