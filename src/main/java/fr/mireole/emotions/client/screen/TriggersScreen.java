package fr.mireole.emotions.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import fr.mireole.emotions.Emotions;
import fr.mireole.emotions.api.trigger.Trigger;
import fr.mireole.emotions.client.screen.widgets.ActiveTriggersList;
import fr.mireole.emotions.client.screen.widgets.AvailableTriggerList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TriggersScreen extends Screen {
    private static final ResourceLocation SCREEN_BACKGROUND = new ResourceLocation(Emotions.MOD_ID, "textures/gui/main_gui_base.png");
    public final int imageWidth = 400;
    private final int imageHeight = 200;
    public int leftPos;
    private int topPos;
    private AvailableTriggerList availableTriggersList;
    private boolean isAvailableTriggersListOpened = false;
    private ActiveTriggersList activeTriggersList;

    public TriggersScreen() {
        super(new TranslatableComponent("emotions.screen.triggers"));
    }

    @Override
    protected void init() {
        super.init();
        leftPos = (width - imageWidth) / 2;
        topPos = (height - imageHeight) / 2;
        TranslatableComponent component = new TranslatableComponent("emotions.screen.triggers.new_trigger");
        addRenderableWidget(new Button(leftPos+1, topPos+1, font.width(component.getVisualOrderText())+20, 20, component, (button) -> {
            openAvailableTriggersList();
        }));
        activeTriggersList = new ActiveTriggersList(this, minecraft, width + 45, imageHeight-40, topPos+20, imageHeight+topPos-40, 40);
        activeTriggersList.setLeftPos(0);
        addRenderableWidget(activeTriggersList);
        activeTriggersList.update();
        availableTriggersList = new AvailableTriggerList(this, minecraft, width + 45, imageHeight-40, topPos+20, imageHeight+topPos-40, 20);
        availableTriggersList.setLeftPos(0);
    }

    @Override
    public void resize(Minecraft pMinecraft, int pWidth, int pHeight) {
        super.resize(pMinecraft, pWidth, pHeight);
        leftPos = (pWidth - imageWidth) / 2;
        topPos = (pHeight - imageHeight) / 2;
    }

    @Override
    public void render(PoseStack p_96562_, int p_96563_, int p_96564_, float p_96565_) {
        renderBackground(p_96562_);
        super.render(p_96562_, p_96563_, p_96564_, p_96565_);
        hideExceedingItems(p_96562_);
    }

    // Renders again part of the images to hide the items exceeding the list
    private void hideExceedingItems(PoseStack stack) {
        if (isAvailableTriggersListOpened) {
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            RenderSystem.setShaderTexture(0, SCREEN_BACKGROUND);
            blit(stack, leftPos+150, topPos, 150, 20, imageWidth-150, 20);

            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            RenderSystem.setShaderTexture(0, SCREEN_BACKGROUND);
            blit(stack, leftPos+150, imageHeight+topPos-40, 150, imageHeight+topPos-40, imageWidth-150, 20);
        }
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void renderBackground(PoseStack p_96559_, int p_96560_) {
        super.renderBackground(p_96559_, p_96560_);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, SCREEN_BACKGROUND);
        blit(p_96559_, leftPos, topPos, 0, 0, imageWidth, imageHeight);
    }

    public void addTrigger(Trigger trigger) {
        System.out.println(trigger.getName());
    }

    @Override
    public void onClose() {
        super.onClose();
        assert minecraft != null;
        minecraft.setScreen(new EmotionsMainScreen());
    }

    public void openAvailableTriggersList() {
        if(availableTriggersList != null && !isAvailableTriggersListOpened) {
            removeWidget(activeTriggersList);
            addRenderableWidget(availableTriggersList);
            isAvailableTriggersListOpened = true;
        }
    }

    public void closeAvailableTriggersList() {
        if(availableTriggersList != null && isAvailableTriggersListOpened) {
            removeWidget(availableTriggersList);
            isAvailableTriggersListOpened = false;
            addRenderableWidget(activeTriggersList);
            activeTriggersList.update();
        }
    }

    public Font getFont() {
        return font;
    }
}
