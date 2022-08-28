package fr.mireole.emotions.client.player;

import com.mojang.authlib.GameProfile;
import fr.mireole.emotions.api.skin.Skin;
import fr.mireole.emotions.api.skin.SkinManager;
import fr.mireole.emotions.api.skin.SkinSwapper;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.io.File;
import java.util.Objects;

@OnlyIn(Dist.CLIENT)
public class SkinPlayer extends AbstractClientPlayer {

    public SkinPlayer(ClientLevel level, Skin skin){
        super(level, new GameProfile(createPlayerUUID(skin.getLocation().getPath()), skin.getLocation().getPath()));
        playerInfo = new EmotionsPlayerInfo(new ClientboundPlayerInfoPacket.PlayerUpdate(getGameProfile(), 0, null, null));
        SkinSwapper.setSkinFor(this, skin);
        setPos(0, -32768, 0); //prevents the name from being rendered by placing the SkinPlayer in a nearly unreachable place
    }

    public SkinPlayer(AbstractClientPlayer player, boolean useDefaultSkin){
        super(player.clientLevel, new GameProfile(createPlayerUUID("SkinPlayer_"+player.getName()), "SkinPlayer_"+player.getName()));
        playerInfo = new EmotionsPlayerInfo(Objects.requireNonNull(player.getPlayerInfo()));
        SkinSwapper.setSkinFor(this, useDefaultSkin ? SkinSwapper.getDefaultSkin(player) : SkinSwapper.getSkin(player));
        setPos(0, -32768, 0); //prevents the name from being rendered by placing the SkinPlayer in a nearly unreachable place
    }

    public void setModelParts(int modelParts){
        getEntityData().set(DATA_PLAYER_MODE_CUSTOMISATION, (byte)modelParts);
    }

}
