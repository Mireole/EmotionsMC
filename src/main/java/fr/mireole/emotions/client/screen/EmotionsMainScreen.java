package fr.mireole.emotions.client.screen;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import fr.mireole.emotions.Emotions;
import fr.mireole.emotions.api.skin.Skin;
import fr.mireole.emotions.api.skin.SkinManager;
import fr.mireole.emotions.api.skin.SkinSwapper;
import fr.mireole.emotions.client.player.SkinPlayer;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.components.StateSwitchingButton;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.PlayerModelPart;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@OnlyIn(Dist.CLIENT)
public class EmotionsMainScreen extends Screen {
    private static final ResourceLocation SCREEN_BACKGROUND = new ResourceLocation(Emotions.MOD_ID, "textures/gui/main_gui_base.png");
    private static final ResourceLocation BUTTONS_LOCATION = new ResourceLocation("textures/gui/recipe_book.png");
    private final int imageWidth = 400;
    private final int imageHeight = 200;
    private int leftPos;
    private int topPos;
    private final List<SkinPlayer> players = new ArrayList<>();
    private int selectedSkin;
    private float playerXRot;
    private float playerYRot;
    private StateSwitchingButton rightArrow;
    private StateSwitchingButton leftArrow;
    private Checkbox checkbox;

    public EmotionsMainScreen() {
        super(new TranslatableComponent("emotions.screen.main"));
    }

    @Override
    protected void init() {
        assert minecraft != null && minecraft.player != null;
        leftPos = (width - imageWidth) / 2;
        topPos = (height - imageHeight) / 2;
        rightArrow = new StateSwitchingButton(leftPos + 380, topPos + imageHeight / 2 - 6, 12, 17, false);
        rightArrow.initTextureValues(1, 208, 13, 18, BUTTONS_LOCATION);
        leftArrow = new StateSwitchingButton(leftPos + 11, topPos + imageHeight / 2 - 6, 12, 17, true);
        leftArrow.initTextureValues(1, 208, 13, 18, BUTTONS_LOCATION);
        addRenderableWidget(new Button(leftPos + imageWidth - font.width(new TranslatableComponent("emotions.screen.main.select")) - 9,
                topPos + imageHeight - 21,
                font.width(new TranslatableComponent("emotions.screen.main.select")) + 8,
                20,
                new TranslatableComponent("emotions.screen.main.select"),
                (button) -> {
                    if(selectedSkin == 0){
                        SkinSwapper.resetSkinForServer();
                    }
                    else {
                        Skin skin = SkinSwapper.getSkin(players.get(selectedSkin));
                        if (skin != null) {
                            SkinSwapper.sendSkinToServer(skin);
                            SkinSwapper.setSkinFor(minecraft.player, skin);
                        }
                    }
                    SkinSwapper.sendSlimPacket(checkbox.selected());

                    onClose();
        }));
        addRenderableWidget(new Button(leftPos + 1,
                topPos + imageHeight - 21,
                font.width(new TranslatableComponent("emotions.screen.main.open_skins_folder")) + 8,
                20,
                new TranslatableComponent("emotions.screen.main.open_skins_folder"),
                (button) -> Util.getPlatform().openFile(new File(minecraft.gameDirectory, "skins/"))));
        addRenderableWidget(new Button(leftPos + imageWidth - font.width(new TranslatableComponent("emotions.screen.main.open_triggers_menu")) - 9,
                topPos + 1,
                font.width(new TranslatableComponent("emotions.screen.main.open_triggers_menu")) + 8,
                20,
                new TranslatableComponent("emotions.screen.main.open_triggers_menu"),
                (button) -> minecraft.setScreen(new TriggersScreen())
        ));
        TranslatableComponent component = new TranslatableComponent("emotions.screen.main.slim_checkbox");
        checkbox = new Checkbox(leftPos+1, topPos+1, font.width(component.getVisualOrderText())+20, 20, component, minecraft.player.getModelName().equals("slim"));
        addRenderableWidget(checkbox);
        generatePlayers();
        playerXRot = -90;
        playerYRot = -90;
        selectedSkin = getActiveSkin();
        updateArrowButtons();
        updateSlimCheckbox();
    }

    public void generatePlayers(){
        assert minecraft != null && minecraft.player != null;
        players.clear();
        LocalPlayer localPlayer = minecraft.player;
        int i = 0;
        for(PlayerModelPart part : PlayerModelPart.values()){
            if(minecraft.options.isModelPartEnabled(part) && part != PlayerModelPart.CAPE){
                i |= part.getMask();
            }
        }
        SkinPlayer player1 = new SkinPlayer(localPlayer, true);
        player1.setModelParts(i);
        players.add(player1);
        for (Skin skin : SkinManager.getSkins()) {
            SkinPlayer player = new SkinPlayer(localPlayer.clientLevel, skin);
            player.setModelParts(i);
            players.add(player);
        }
    }

    private int getActiveSkin() {
        assert minecraft != null;
        Skin skin = SkinSwapper.getSkin(minecraft.player);
        assert skin != null;
        for(Skin skin1 : SkinManager.getSkins()){
            if(skin1.getLocation().equals(skin.getLocation())){
                return SkinManager.getSkins().indexOf(skin1) + 1;
            }
        }
        return 0;
    }

    @Override
    public void render(@NotNull PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        renderBackground(poseStack);
        super.render(poseStack, mouseX, mouseY, partialTick);
        rightArrow.render(poseStack, mouseX, mouseY, partialTick);
        leftArrow.render(poseStack, mouseX, mouseY, partialTick);
    }

    @Override
    public void renderBackground(@NotNull PoseStack poseStack, int vOffset) {
        super.renderBackground(poseStack, vOffset);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, SCREEN_BACKGROUND);
        this.blit(poseStack, leftPos, topPos, 0, 0, imageWidth, imageHeight);
        renderLabels();
        renderPlayer(leftPos + 200, topPos + 160, 60, playerXRot, playerYRot, players.get(selectedSkin));
        if(selectedSkin + 1 < players.size()){
            renderPlayer(leftPos + 300, topPos + 130, 30, playerXRot, playerYRot, players.get(selectedSkin + 1));
        }
        if(selectedSkin > 0){
            renderPlayer(leftPos + 100, topPos + 130, 30, playerXRot, playerYRot, players.get(selectedSkin-1));
        }
    }

    @SuppressWarnings("deprecation")
    public void renderPlayer(int x, int y, int height, float xRot, float yRot, SkinPlayer player) {
        float f = xRot / 100;
        float f1 = yRot / 100;
        PoseStack posestack = RenderSystem.getModelViewStack();
        posestack.pushPose();
        posestack.translate(x, y, 1050.0D);
        posestack.scale(1.0F, 1.0F, -1.0F);
        RenderSystem.applyModelViewMatrix();
        PoseStack posestack1 = new PoseStack();
        posestack1.translate(0.0D, 0.0D, 1000.0D);
        posestack1.scale((float) height, (float) height, (float) height);
        Quaternion quaternion = Vector3f.ZP.rotationDegrees(180.0F);
        Quaternion quaternion1 = Vector3f.XP.rotationDegrees(f1 * 20.0F);
        quaternion.mul(quaternion1);
        posestack1.mulPose(quaternion);
        float f2 = player.yBodyRot;
        float f3 = player.getYRot();
        float f4 = player.getXRot();
        float f5 = player.yHeadRotO;
        float f6 = player.yHeadRot;
        player.yBodyRot = 180.0F + f * 20.0F;
        player.setYRot(180.0F + f * 40.0F);
        player.setXRot(-f1 * 20.0F);
        player.yHeadRot = player.getYRot() / 2 + 90;
        player.yHeadRotO = player.getYRot() / 2 + 90;
        final Vector3f INVENTORY_DIFFUSE_LIGHT_0 = Util.make(new Vector3f(0.2F, -1.0F, -1.0F), Vector3f::normalize);
        final Vector3f INVENTORY_DIFFUSE_LIGHT_1 = Util.make(new Vector3f(-0.2F, 0.0F, -1.0F), Vector3f::normalize);
        RenderSystem.setShaderLights(INVENTORY_DIFFUSE_LIGHT_1, INVENTORY_DIFFUSE_LIGHT_0);
        EntityRenderDispatcher entityrenderdispatcher = Minecraft.getInstance().getEntityRenderDispatcher();
        quaternion1.conj();
        entityrenderdispatcher.overrideCameraOrientation(quaternion1);
        entityrenderdispatcher.setRenderShadow(false);
        MultiBufferSource.BufferSource multibuffersource$buffersource = Minecraft.getInstance().renderBuffers().bufferSource();
        RenderSystem.runAsFancy(() -> entityrenderdispatcher.render(player, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F, posestack1, multibuffersource$buffersource, 15728880));
        multibuffersource$buffersource.endBatch();
        entityrenderdispatcher.setRenderShadow(true);
        player.yBodyRot = f2;
        player.setYRot(f3);
        player.setXRot(f4);
        player.yHeadRotO = f5;
        player.yHeadRot = f6;
        posestack.popPose();
        RenderSystem.applyModelViewMatrix();
        Lighting.setupFor3DItems();
    }

    public void renderLabels() {

    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public boolean keyPressed(int pKeyCode, int pScanCode, int pModifiers) {
        if (pKeyCode == Minecraft.getInstance().options.keyInventory.getKey().getValue()) {
            onClose();
            return true;
        }
        return super.keyPressed(pKeyCode, pScanCode, pModifiers);
    }

    @Override
    public boolean mouseDragged(double pMouseX, double pMouseY, int pButton, double pDragX, double pDragY) {
        playerXRot -= pDragX * 20;
        playerYRot -= pDragY * 20;
        playerYRot = Math.max(playerYRot, -180);
        playerYRot = Math.min(playerYRot, 180);
        return super.mouseDragged(pMouseX, pMouseY, pButton, pDragX, pDragY);
    }

    private void setSelectedSkin(int newSelectedSkin) {
        newSelectedSkin = Math.max(newSelectedSkin, 0);
        newSelectedSkin = Math.min(newSelectedSkin, players.size() - 1);
        selectedSkin = newSelectedSkin;
    }

    private void updateArrowButtons() {
        rightArrow.visible = players.size() > 1 && selectedSkin < players.size() - 1;
        leftArrow.visible = players.size() > 1 && selectedSkin > 0;
    }

    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
        if (rightArrow.mouseClicked(pMouseX, pMouseY, pButton)) {
            setSelectedSkin(selectedSkin + 1);
            updateArrowButtons();
            updateSlimCheckbox();
            return true;
        } else if (leftArrow.mouseClicked(pMouseX, pMouseY, pButton)) {
            setSelectedSkin(selectedSkin - 1);
            updateArrowButtons();
            updateSlimCheckbox();
            return true;
        } else if (checkbox.mouseClicked(pMouseX, pMouseY, pButton)){
            SkinManager.setSlim(Objects.requireNonNull(SkinSwapper.getSkin(players.get(selectedSkin))), checkbox.selected());
            return true;
        }
        return super.mouseClicked(pMouseX, pMouseY, pButton);
    }

    private void updateSlimCheckbox() {
        if(SkinSwapper.isSlim(players.get(selectedSkin)) ^ checkbox.selected()) {
            checkbox.onPress();
        }
        checkbox.active = selectedSkin != 0;
    }

}