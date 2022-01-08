package fr.mireole.emotions.api;

import fr.mireole.emotions.client.player.EmotionsPlayerInfo;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SkinSwapper {

    public static void setSkinFor(AbstractClientPlayer player, ResourceLocation skin){
        PlayerInfo info = player.getPlayerInfo();
        if(info != null) {
            if (!(info instanceof EmotionsPlayerInfo)) {
                player.playerInfo = new EmotionsPlayerInfo(info);
                info = player.getPlayerInfo();
            }
            ((EmotionsPlayerInfo) info).setSkin(skin);
        }
    }

    public static void resetSkinFor(AbstractClientPlayer player) {
        PlayerInfo info = player.getPlayerInfo();
        if (info != null) {
            if (!(info instanceof EmotionsPlayerInfo)) {
                player.playerInfo = new EmotionsPlayerInfo(info);
                info = player.getPlayerInfo();
            }
            ((EmotionsPlayerInfo) info).setSkinToDefault();
        }
    }

}
