package fr.mireole.emotions.client.player;

import fr.mireole.emotions.api.skin.Skin;
import fr.mireole.emotions.api.skin.SkinManager;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public class EmotionsPlayerInfo extends PlayerInfo {
    protected Skin skin;
    public final Skin defaultSkin;

    public EmotionsPlayerInfo(ClientboundPlayerInfoPacket.PlayerUpdate playerUpdate) {
        super(playerUpdate);
        registerTextures();
        defaultSkin = SkinManager.createSkin(super.getSkinLocation(), super.getModelName().equals("slim"), true);
        setSkinToDefault();
    }

    public EmotionsPlayerInfo(PlayerInfo info){
        super(new ClientboundPlayerInfoPacket.PlayerUpdate(info.getProfile(), info.getLatency(), info.getGameMode(), info.getTabListDisplayName()));
        setLastHealth(info.getLastHealth());
        setDisplayHealth(info.getDisplayHealth());
        setLastHealthTime(info.getLastHealthTime());
        setHealthBlinkTime(info.getHealthBlinkTime());
        setRenderVisibilityId(info.getRenderVisibilityId());
        registerTextures();
        defaultSkin = SkinManager.createSkin(super.getSkinLocation(), super.getModelName().equals("slim"), true);
        setSkinToDefault();
    }


    public void setSkinToDefault(){
        skin = defaultSkin;
    }

    public void setSkin(Skin skin) {
        this.skin = skin;
    }

    @Override
    @NotNull
    public ResourceLocation getSkinLocation() {
        return skin.getLocation();
    }

    public boolean isDefault(ResourceLocation skin){
        return skin == super.getSkinLocation();
    }

    public Skin getDefaultSkin(){
        return defaultSkin;
    }

    @Override
    @NotNull
    public String getModelName() {
        return skin.isSlim() ? "slim" : "default";
    }

    public Skin getSkin() {
        return skin;
    }
}
