package fr.mireole.emotions.client.screen.widgets;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public class ScrollingTextWidget extends AbstractWidget {
    private static final int SCROLLING_SPEED = 1;
    private static final int BLANK_SPACE = 10;
    private int xOffset = BLANK_SPACE;
    private int messageWidth;
    private final Font font;

    public ScrollingTextWidget(int pX, int pY, int pWidth, int pHeight, Component pMessage) {
        super(pX, pY, pWidth, pHeight, pMessage);
        font = Minecraft.getInstance().font;
        messageWidth = font.width(pMessage);
    }

    public ScrollingTextWidget(Component pMessage) {
        this(0, 0, 0, 0, pMessage);
        setHeight(font.lineHeight);
    }

    @Override
    public void setMessage(@NotNull Component pMessage) {
        super.setMessage(pMessage);
        messageWidth = font.width(pMessage);
    }

    @Override
    public void updateNarration(NarrationElementOutput pNarrationElementOutput) {
        pNarrationElementOutput.add(NarratedElementType.TITLE, this.getMessage());
    }

    @Override
    public void render(@NotNull PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
        // Set a mask to prevent the text from going out of the widget
        Minecraft minecraft = Minecraft.getInstance();
        double scale = minecraft.getWindow().getGuiScale();
        RenderSystem.enableScissor((int) (x * scale), (int) ((minecraft.getWindow().getHeight() - (y * scale)) - (height * scale)), (int) (width * scale), (int) (height * scale)); // TODO: Get the other code to use scissor too
        font.draw(pPoseStack, getMessage(), xOffset + x, y, 4210752);
        font.draw(pPoseStack, getMessage(), xOffset + x - messageWidth - BLANK_SPACE, y, 4210752);
        RenderSystem.disableScissor();
    }

    public void tick() {
        if (messageWidth > width) {
            xOffset -= SCROLLING_SPEED;
            if (xOffset < 0) {
                xOffset = messageWidth + BLANK_SPACE;
            }
        } else {
            xOffset = 0;
        }
    }

}
