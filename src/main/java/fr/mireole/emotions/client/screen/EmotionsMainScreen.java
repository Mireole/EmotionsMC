package fr.mireole.emotions.client.screen;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import fr.mireole.emotions.Emotions;
import fr.mireole.emotions.api.SkinSwapper;
import fr.mireole.emotions.client.player.SkinPlayer;
import fr.mireole.emotions.client.texture.SkinTexture;
import net.minecraft.ChatFormatting;
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
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.PlayerModelPart;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

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
        LocalPlayer localPlayer = minecraft.player;
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
                        SkinSwapper.sendSkinToServer(new File("skins/", players.get(selectedSkin).getName().getString()));
                    }
                    String newModelName = checkbox.selected() ? "slim" : "default";
                    SkinSwapper.sendModelNameToServer(newModelName);

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
                (button) -> {
                    minecraft.setScreen(new TriggersScreen());
                }
        ));
        TranslatableComponent component = new TranslatableComponent("emotions.screen.main.slim_checkbox");
        checkbox = new Checkbox(leftPos+1, topPos+1, font.width(component.getVisualOrderText())+20, 20, component, minecraft.player.getModelName().equals("slim"));
        addRenderableWidget(checkbox);
        File skinsDirectory = new File(minecraft.gameDirectory, "skins/");
        File[] files = skinsDirectory.listFiles();
        int i = 0;
        for(PlayerModelPart part : PlayerModelPart.values()){
            if(minecraft.options.isModelPartEnabled(part) && part != PlayerModelPart.CAPE){
                i |= part.getMask();
            }
        }
        SkinPlayer player1 = new SkinPlayer(localPlayer, true);
        player1.setModelParts(i);
        players.add(player1);
        if (files != null) {
            if (files.length > 0) {
                for (File file : files) {
                    if (file.getName().endsWith(".png")) {
                        SkinPlayer player = new SkinPlayer(minecraft.level, file);
                        player.setModelParts(i);
                        players.add(player);
                    }
                }
            }
        }
        playerXRot = -90;
        playerYRot = -90;
        selectedSkin = getSelectedSkin();
        updateArrowButtons();
        setModelNames(localPlayer.getModelName());
    }

    private int getSelectedSkin() {
        assert minecraft != null && minecraft.player != null;
        if(minecraft.textureManager.getTexture(minecraft.player.getSkinTextureLocation()) instanceof SkinTexture playerSkin) {
            for (SkinPlayer player : players) {
                if (minecraft.textureManager.getTexture(player.getSkinTextureLocation()) instanceof SkinTexture skinPlayerSkin) {
                    try {
                        boolean b = Files.mismatch(new File(playerSkin.getSkinLocation().getPath()).toPath(), new File(skinPlayerSkin.getSkinLocation().getPath()).toPath()) == -1;
                        if(b) return players.indexOf(player);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return 0;
    }

    @Override
    public void render(@NotNull PoseStack p_96562_, int p_96563_, int p_96564_, float p_96565_) {
        renderBackground(p_96562_);
        super.render(p_96562_, p_96563_, p_96564_, p_96565_);
        rightArrow.render(p_96562_, p_96563_, p_96564_, p_96565_);
        leftArrow.render(p_96562_, p_96563_, p_96564_, p_96565_);
    }

    @Override
    public void renderBackground(@NotNull PoseStack p_96559_, int p_96560_) {
        super.renderBackground(p_96559_, p_96560_);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, SCREEN_BACKGROUND);
        this.blit(p_96559_, leftPos, topPos, 0, 0, imageWidth, imageHeight);
        renderLabels(p_96559_);
        renderPlayer(leftPos + 200, topPos + 160, 60, playerXRot, playerYRot, players.get(selectedSkin));
        if(selectedSkin + 1 < players.size()){
            renderPlayer(leftPos + 300, topPos + 130, 30, playerXRot, playerYRot, players.get(selectedSkin + 1));
        }
        if(selectedSkin > 0){
            renderPlayer(leftPos + 100, topPos + 130, 30, playerXRot, playerYRot, players.get(selectedSkin-1));
        }
    }

    public void renderPlayer(int p_98851_, int p_98852_, int p_98853_, float p_98854_, float p_98855_, SkinPlayer p_98856_) {
        float f = p_98854_ / 100;
        float f1 = p_98855_ / 100;
        PoseStack posestack = RenderSystem.getModelViewStack();
        posestack.pushPose();
        posestack.translate(p_98851_, p_98852_, 1050.0D);
        posestack.scale(1.0F, 1.0F, -1.0F);
        RenderSystem.applyModelViewMatrix();
        PoseStack posestack1 = new PoseStack();
        posestack1.translate(0.0D, 0.0D, 1000.0D);
        posestack1.scale((float) p_98853_, (float) p_98853_, (float) p_98853_);
        Quaternion quaternion = Vector3f.ZP.rotationDegrees(180.0F);
        Quaternion quaternion1 = Vector3f.XP.rotationDegrees(f1 * 20.0F);
        quaternion.mul(quaternion1);
        posestack1.mulPose(quaternion);
        float f2 = p_98856_.yBodyRot;
        float f3 = p_98856_.getYRot();
        float f4 = p_98856_.getXRot();
        float f5 = p_98856_.yHeadRotO;
        float f6 = p_98856_.yHeadRot;
        p_98856_.yBodyRot = 180.0F + f * 20.0F;
        p_98856_.setYRot(180.0F + f * 40.0F);
        p_98856_.setXRot(-f1 * 20.0F);
        p_98856_.yHeadRot = p_98856_.getYRot() / 2 + 90;
        p_98856_.yHeadRotO = p_98856_.getYRot() / 2 + 90;
        final Vector3f INVENTORY_DIFFUSE_LIGHT_0 = Util.make(new Vector3f(0.2F, -1.0F, -1.0F), Vector3f::normalize);
        final Vector3f INVENTORY_DIFFUSE_LIGHT_1 = Util.make(new Vector3f(-0.2F, 0.0F, -1.0F), Vector3f::normalize);
        RenderSystem.setShaderLights(INVENTORY_DIFFUSE_LIGHT_1, INVENTORY_DIFFUSE_LIGHT_0);
        EntityRenderDispatcher entityrenderdispatcher = Minecraft.getInstance().getEntityRenderDispatcher();
        quaternion1.conj();
        entityrenderdispatcher.overrideCameraOrientation(quaternion1);
        entityrenderdispatcher.setRenderShadow(false);
        MultiBufferSource.BufferSource multibuffersource$buffersource = Minecraft.getInstance().renderBuffers().bufferSource();
        RenderSystem.runAsFancy(() -> entityrenderdispatcher.render(p_98856_, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F, posestack1, multibuffersource$buffersource, 15728880));
        multibuffersource$buffersource.endBatch();
        entityrenderdispatcher.setRenderShadow(true);
        p_98856_.yBodyRot = f2;
        p_98856_.setYRot(f3);
        p_98856_.setXRot(f4);
        p_98856_.yHeadRotO = f5;
        p_98856_.yHeadRot = f6;
        posestack.popPose();
        RenderSystem.applyModelViewMatrix();
        Lighting.setupFor3DItems();
    }

    public void renderLabels(PoseStack poseStack) {

    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public boolean keyPressed(int p_96552_, int p_96553_, int p_96554_) {
        if (p_96552_ == Minecraft.getInstance().options.keyInventory.getKey().getValue()) {
            onClose();
            return true;
        }
        return super.keyPressed(p_96552_, p_96553_, p_96554_);
    }

    @Override
    public boolean mouseDragged(double p_94699_, double p_94700_, int p_94701_, double p_94702_, double p_94703_) {
        playerXRot -= p_94702_ * 20;
        playerYRot -= p_94703_ * 20;
        playerYRot = Math.max(playerYRot, -180);
        playerYRot = Math.min(playerYRot, 180);
        return super.mouseDragged(p_94699_, p_94700_, p_94701_, p_94702_, p_94703_);
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

    private void setModelNames(String modelName){
        for (SkinPlayer player : players){
            SkinSwapper.setModelNameFor(player, modelName);
        }
    }

    @Override
    public boolean mouseClicked(double p_94695_, double p_94696_, int p_94697_) {
        if (rightArrow.mouseClicked(p_94695_, p_94696_, p_94697_)) {
            setSelectedSkin(selectedSkin + 1);
            updateArrowButtons();
            return true;
        } else if (leftArrow.mouseClicked(p_94695_, p_94696_, p_94697_)) {
            setSelectedSkin(selectedSkin - 1);
            updateArrowButtons();
            return true;
        } else if (checkbox.mouseClicked(p_94695_, p_94696_, p_94697_)){
            setModelNames(checkbox.selected() ? "slim" : "default");
            return true;
        }
        return super.mouseClicked(p_94695_, p_94696_, p_94697_);
    }

}