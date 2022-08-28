package fr.mireole.emotions.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import fr.mireole.emotions.Emotions;
import fr.mireole.emotions.api.trigger.Triggers;
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
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public class TriggersScreen extends Screen {
    private static final ResourceLocation SCREEN_BACKGROUND = new ResourceLocation(Emotions.MOD_ID, "textures/gui/main_gui_base.png");
    public final int imageWidth = 400;
    private final int imageHeight = 200;
    public int leftPos;
    private int topPos;
    private AvailableTriggerList availableTriggersList;
    private boolean availableTriggersListOpened = false;
    private ActiveTriggersList activeTriggersList;

    public TriggersScreen() {
        super(new TranslatableComponent("emotions.screen.triggers"));
    }

    @Override
    protected void init() {
        super.init();
        Triggers.loadEnabledTriggers(); // Load triggers in case they were modified, to prevent having to restart the game in case they were modified.
        leftPos = (width - imageWidth) / 2;
        topPos = (height - imageHeight) / 2;
        TranslatableComponent newTriggerComponent = new TranslatableComponent("emotions.screen.triggers.new_trigger");
        addRenderableWidget(new Button(leftPos+1, topPos+1, font.width(newTriggerComponent.getVisualOrderText())+20, 20, newTriggerComponent, (button) -> openAvailableTriggersList()));
        activeTriggersList = new ActiveTriggersList(this, minecraft, width + 45, imageHeight-40, topPos+20, imageHeight+topPos-40, 50);
        activeTriggersList.setLeftPos(0);
        addRenderableWidget(activeTriggersList);
        activeTriggersList.update();
        availableTriggersList = new AvailableTriggerList(this, minecraft, width + 45, imageHeight-40, topPos+20, imageHeight+topPos-40, 20);
        availableTriggersList.setLeftPos(0);
    }


    @Override
    public void resize(@NotNull Minecraft pMinecraft, int pWidth, int pHeight) {
        super.resize(pMinecraft, pWidth, pHeight);
        leftPos = (pWidth - imageWidth) / 2;
        topPos = (pHeight - imageHeight) / 2;
    }

    @Override
    public void render(@NotNull PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
        renderBackground(pPoseStack);
        super.render(pPoseStack, pMouseX, pMouseY, pPartialTick);
        hideExceedingItems(pPoseStack);
        if (availableTriggersListOpened) {
            availableTriggersList.reRenderTooltips(pPoseStack, pMouseX, pMouseY); // Re-render button tooltips to prevent them from being hidden by hideExceedingItems
        }
    }

    // Renders again part of the images to hide the items exceeding the list
    private void hideExceedingItems(PoseStack stack) {
        if (availableTriggersListOpened) {
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
    public void renderBackground(@NotNull PoseStack pPoseStack, int pVOffset) {
        super.renderBackground(pPoseStack, pVOffset);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, SCREEN_BACKGROUND);
        blit(pPoseStack, leftPos, topPos, 0, 0, imageWidth, imageHeight);
    }

    @Override
    public void onClose() {
        Triggers.saveEnabledTriggers();
        super.onClose();
        assert minecraft != null;
        minecraft.setScreen(new EmotionsMainScreen());
    }

    public void openAvailableTriggersList() {
        if(availableTriggersList != null && !availableTriggersListOpened) {
            removeWidget(activeTriggersList);
            addRenderableWidget(availableTriggersList);
            availableTriggersListOpened = true;
        }
    }

    public void closeAvailableTriggersList() {
        if(availableTriggersList != null && availableTriggersListOpened) {
            removeWidget(availableTriggersList);
            availableTriggersListOpened = false;
            addRenderableWidget(activeTriggersList);
            activeTriggersList.update();
        }
    }

    public Font getFont() {
        return font;
    }

    @Override
    public void tick() {
        super.tick();
        activeTriggersList.tick();
    }
}
