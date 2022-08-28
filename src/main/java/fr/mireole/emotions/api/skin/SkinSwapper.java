package fr.mireole.emotions.api.skin;

import fr.mireole.emotions.client.player.EmotionsPlayerInfo;
import fr.mireole.emotions.network.EmotionsNetwork;
import fr.mireole.emotions.network.packet.SlimPacket;
import fr.mireole.emotions.network.packet.ResetSkinPacket;
import fr.mireole.emotions.network.packet.SkinPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;

@OnlyIn(Dist.CLIENT)
public class SkinSwapper {

    public static void setSkinFor(@NotNull Player player, Skin skin){
        if (player instanceof AbstractClientPlayer player1) {
            EmotionsPlayerInfo info = getEmotionsPlayerInfo(player1);
            if (info != null) {
                info.setSkin(skin);
            }
        }
    }

    public static void resetSkinFor(Player player) {
        if (player instanceof AbstractClientPlayer player1) {
            EmotionsPlayerInfo info = getEmotionsPlayerInfo(player1);
            if (info != null) {
                info.setSkinToDefault();
            }
        }
    }

    public static void sendSkinToServer(Skin skin){
        if (skin.getSkinLocation() != null) {
            assert Minecraft.getInstance().player != null;
            SkinPacket packet = new SkinPacket(skin.getName(), Path.of(skin.getSkinLocation().getPath()), Minecraft.getInstance().player.getUUID());
            EmotionsNetwork.CHANNEL.sendToServer(packet);
        }
    }

    public static void resetSkinForServer(){
        assert Minecraft.getInstance().player != null;
        ResetSkinPacket packet = new ResetSkinPacket(Minecraft.getInstance().player.getUUID());
        EmotionsNetwork.CHANNEL.sendToServer(packet);
    }

    public static void sendSlimPacket(boolean slim){
        assert Minecraft.getInstance().player != null;
        SlimPacket packet = new SlimPacket(Minecraft.getInstance().player.getUUID(), slim);
        EmotionsNetwork.CHANNEL.sendToServer(packet);
    }

    public static boolean isDefaultSkin(Player player, ResourceLocation skin){
        if (player instanceof AbstractClientPlayer player1) {
            EmotionsPlayerInfo info = getEmotionsPlayerInfo(player1);
            if (info != null) {
                return info.isDefault(skin);
            }
        }
        return true;
    }

    public static Skin getDefaultSkin(Player player) {
        if (player instanceof AbstractClientPlayer player1) {
            EmotionsPlayerInfo info = getEmotionsPlayerInfo(player1);
            if (info != null) {
                return info.getDefaultSkin();
            }
        }
        return null;
    }

    public static boolean isSlim(Player player){
        if (player instanceof AbstractClientPlayer player1) {
            EmotionsPlayerInfo info = getEmotionsPlayerInfo(player1);
            if (info != null) {
                return info.getModelName().equals("slim");
            }
        }
        return false;
    }

    public static Skin getSkin(Player player){
        if (player instanceof AbstractClientPlayer player1) {
            EmotionsPlayerInfo info = getEmotionsPlayerInfo(player1);
            if (info != null) {
                return info.getSkin();
            }
        }
        return null;
    }

    public static void setSlim(Player player, boolean slim){
        if (player instanceof AbstractClientPlayer) {
            Skin skin = getSkin(player);
            if (skin != null) {
                skin.setSlim(slim);
            }
        }
    }

    protected static EmotionsPlayerInfo getEmotionsPlayerInfo(Player player) {
        if (player instanceof AbstractClientPlayer player1) {
            PlayerInfo info = player1.getPlayerInfo();
            if (info != null) {
                if (!(info instanceof EmotionsPlayerInfo)) {
                    player1.playerInfo = new EmotionsPlayerInfo(info);
                    info = player1.getPlayerInfo();
                }
                return (EmotionsPlayerInfo) info;
            }
        }
        return null;
    }

}
