package fr.mireole.emotions.api.action;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import fr.mireole.emotions.Emotions;
import fr.mireole.emotions.api.skin.Skin;
import fr.mireole.emotions.api.skin.SkinManager;
import fr.mireole.emotions.api.skin.SkinSwapper;
import fr.mireole.emotions.client.screen.TriggersScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.atomic.AtomicBoolean;

@OnlyIn(Dist.CLIENT)
public class SkinAction extends Action {
    public static final ResourceLocation UNKNOWN_SKIN = new ResourceLocation(Emotions.MOD_ID, "textures/gui/unknown_skin.png");

    private String skinName;
    private final transient EditBox editBox;
    private final transient Font font;
    private transient int left;
    private transient int top;

    public SkinAction() {
        font = Minecraft.getInstance().font;

        editBox = new EditBox(font, 0, 0, 300, 20, new TextComponent("test"));
        editBox.setResponder((text) -> {
            AtomicBoolean found = new AtomicBoolean(false);
            if ("default".startsWith(text)) {
                found.set(true);
                setSkinName(text);
                editBox.setSuggestion("default".replaceFirst(text, ""));
            } else {
                SkinManager.getSkins().forEach(skin -> {
                    if (skin.getName().startsWith(text)) {
                        editBox.setSuggestion(skin.getName().replaceFirst(text, "").replace(".png", ""));
                        found.set(true);
                    }
                    setSkinName(text);
                });
            }
            if (!found.get()) {
                editBox.setSuggestion(null);
            }
        });

        if (getSkinName() != null) {
            editBox.setValue(getSkinName().replace(".png", ""));
        }
    }
    public SkinAction(String skinName) {
        this();
        this.skinName = skinName;
    }

    public String getSkinName() {
        return skinName;
    }

    public void setSkinName(String skinName) {
        this.skinName = skinName;
    }

    @Override
    public boolean isValid() {
        return skinName != null && !skinName.isEmpty() && (SkinManager.getSkin(skinName + ".png") != null || skinName.equals("default"));
    }

    public Skin getSkin() {
        Skin skin = SkinManager.getSkin(skinName + ".png");
        if (skin != null) {
            return skin;
        }
        return SkinManager.getSkin(skinName);
    }

    @Override
    public void setComponentPos(TriggersScreen screen, int index, int left, int top, int width, int height) {
        if (editBox.getValue().equals("") && skinName != null && !skinName.equals("")){
            editBox.setValue(skinName);
        }
        editBox.x = left + font.width(new TranslatableComponent("emotions.screen.triggers.set_skin_to")) - 26;
        editBox.y = top + 22;
        editBox.setWidth(screen.leftPos + screen.imageWidth - 66 - left - font.width(new TranslatableComponent("emotions.screen.triggers.set_skin_to")));
        this.left = left;
        this.top = top;
    }

    @Override
    public void execute() {
        LocalPlayer player = Minecraft.getInstance().player;
        assert player != null;
        if (getSkinName().equals("default")) {
            SkinSwapper.resetSkinFor(player);
            SkinSwapper.resetSkinForServer();
        } else {
            Skin skin = getSkin();
            assert skin != null;
            SkinSwapper.setSkinFor(player, skin);
            SkinSwapper.sendSkinToServer(skin);
        }
    }

    @Override
    public void render(@NotNull PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
        font.draw(pPoseStack, new TranslatableComponent("emotions.screen.triggers.set_skin_to"), left - 30, top + 22 + (float) (22 - font.lineHeight) / 2, 4210752); // "Set skin to" or its translation

        editBox.render(pPoseStack, pMouseX, pMouseY, pPartialTick);

        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        if (isValid()) {
            Skin skin = getSkinName().equals("default") ? SkinSwapper.getDefaultSkin(Minecraft.getInstance().player) : getSkin();
            assert skin != null;
            RenderSystem.setShaderTexture(0, skin.getLocation());
            GuiComponent.blit(pPoseStack, editBox.x + editBox.getWidth() + 4, top + 24, 16, 16, 8, 8, 8, 8, 64, 64);
        } else {
            RenderSystem.setShaderTexture(0, UNKNOWN_SKIN);
            GuiComponent.blit(pPoseStack, editBox.x + editBox.getWidth() + 4, top + 24, 16, 16, 0, 0, 8, 8, 8, 8);
        }
    }

    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
        return editBox.mouseClicked(pMouseX, pMouseY, pButton);
    }

    @Override
    public boolean keyPressed(int pKeyCode, int pScanCode, int pModifiers) {
        return editBox.keyPressed(pKeyCode, pScanCode, pModifiers);
    }

    @Override
    public boolean charTyped(char pCodePoint, int pModifiers) {
        return editBox.charTyped(pCodePoint, pModifiers);
    }

    @Override
    public void tick(){
        editBox.tick();
    }

}