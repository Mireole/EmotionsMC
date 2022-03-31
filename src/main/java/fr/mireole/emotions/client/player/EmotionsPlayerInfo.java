package fr.mireole.emotions.client.player;

import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public class EmotionsPlayerInfo extends PlayerInfo {
    protected ResourceLocation skin;
    protected String modelName;
    protected boolean isDefaultSkin = true; // Used to prevent weird things like the player having a steve / alex skin instead of his default one sometimes.

    public EmotionsPlayerInfo(ClientboundPlayerInfoPacket.PlayerUpdate p_105311_) {
        super(p_105311_);
        registerTextures();
        modelName = super.getModelName();
        setSkinToDefault();
    }

    public EmotionsPlayerInfo(PlayerInfo info){
        super(new ClientboundPlayerInfoPacket.PlayerUpdate(info.getProfile(), info.getLatency(), info.getGameMode(), info.getTabListDisplayName()));
        setLastHealth(info.getLastHealth());
        setDisplayHealth(info.getDisplayHealth());
        setLastHealthTime(info.getLastHealthTime());
        setHealthBlinkTime(info.getHealthBlinkTime());
        setRenderVisibilityId(info.getRenderVisibilityId());
        setSkin(info.getSkinLocation());
        modelName = info.getModelName();
        registerTextures();
    }


    public void setSkinToDefault(){
        if(!isDefaultSkin) {
            setSkin(super.getSkinLocation());
            isDefaultSkin = true;
        }
    }

    public void setSkin(ResourceLocation skin) {
        isDefaultSkin = false;
        this.skin = skin;
    }

    @Override
    @NotNull
    public ResourceLocation getSkinLocation() {
        return skin;
    }

    public boolean isDefault(ResourceLocation skin){
        return skin == super.getSkinLocation();
    }

    public ResourceLocation getDefaultSkin(){
        return super.getSkinLocation();
    }

    @Override
    @NotNull
    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName){
        this.modelName = modelName;
    }

}
