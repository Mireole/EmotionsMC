package fr.mireole.emotions.client.player;

import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public class EmotionsPlayerInfo extends PlayerInfo {
    private ResourceLocation skin;

    public EmotionsPlayerInfo(ClientboundPlayerInfoPacket.PlayerUpdate p_105311_) {
        super(p_105311_);
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
    }


    public void setSkinToDefault(){
        setSkin(super.getSkinLocation());
    }

    public void setSkin(ResourceLocation skin) {
        this.skin = skin;
    }

    @Override
    public @NotNull ResourceLocation getSkinLocation() {
        return skin;
    }
}
