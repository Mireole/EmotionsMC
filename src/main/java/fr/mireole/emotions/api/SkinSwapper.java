package fr.mireole.emotions.api;

import fr.mireole.emotions.Emotions;
import fr.mireole.emotions.client.player.EmotionsPlayerInfo;
import fr.mireole.emotions.client.texture.SkinTexture;
import fr.mireole.emotions.network.EmotionsNetwork;
import fr.mireole.emotions.network.packet.ModelNamePacket;
import fr.mireole.emotions.network.packet.ResetSkinPacket;
import fr.mireole.emotions.network.packet.SkinPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import java.io.File;

@OnlyIn(Dist.CLIENT)
public class SkinSwapper {

    public static void setSkinFor(@NotNull AbstractClientPlayer player, ResourceLocation skin){
        EmotionsPlayerInfo info = getEmotionsPlayerInfo(player);
        if (info != null) {
            info.setSkin(skin);
        }
    }

    public static void setModelNameFor(@NotNull AbstractClientPlayer player, String modelName){
        EmotionsPlayerInfo info = getEmotionsPlayerInfo(player);
        if (info != null) {
            info.setModelName(modelName);
        }
    }

    public static void resetSkinFor(AbstractClientPlayer player) {
        EmotionsPlayerInfo info = getEmotionsPlayerInfo(player);
        if (info != null) {
            info.setSkinToDefault();
        }
    }

    public static void setSkinFor(AbstractClientPlayer player, File image){
        Minecraft mc = Minecraft.getInstance();
        TextureManager textureManager = mc.getTextureManager();
        ResourceLocation skinLocation = new ResourceLocation(Emotions.MOD_ID, player.getUUID().toString());
        textureManager.release(skinLocation);
        String texturePath = Minecraft.getInstance().gameDirectory.toURI().relativize(new File(image.getAbsolutePath().replace("\\", "/")).toURI()).getPath();
        textureManager.register(skinLocation, new SkinTexture(new ResourceLocation(texturePath)));
        setSkinFor(player, skinLocation);
    }

    public static void sendSkinToServer(File image){
        SkinPacket packet = new SkinPacket(image.getName(), image, Minecraft.getInstance().player.getUUID());
        EmotionsNetwork.CHANNEL.sendToServer(packet);
    }

    public static void resetSkinForServer(){
        ResetSkinPacket packet = new ResetSkinPacket(Minecraft.getInstance().player.getUUID());
        EmotionsNetwork.CHANNEL.sendToServer(packet);
    }

    public static void sendModelNameToServer(String modelName){
        ModelNamePacket packet = new ModelNamePacket(Minecraft.getInstance().player.getUUID(), modelName);
        EmotionsNetwork.CHANNEL.sendToServer(packet);
    }

    public static boolean isDefaultSkin(AbstractClientPlayer player, ResourceLocation skin){
        EmotionsPlayerInfo info = getEmotionsPlayerInfo(player);
        if (info != null) {
            return info.isDefault(skin);
        }
        return true;
    }

    public static ResourceLocation getDefaultSkin(AbstractClientPlayer player) {
        EmotionsPlayerInfo info = getEmotionsPlayerInfo(player);
        if (info != null) {
            return info.getDefaultSkin();
        }
        return null;
    }

    protected static EmotionsPlayerInfo getEmotionsPlayerInfo(AbstractClientPlayer player) {
        PlayerInfo info = player.getPlayerInfo();
        if (info != null) {
            if (!(info instanceof EmotionsPlayerInfo)) {
                player.playerInfo = new EmotionsPlayerInfo(info);
                info = player.getPlayerInfo();
            }
            return (EmotionsPlayerInfo) info;
        }
        return null;
    }

}
