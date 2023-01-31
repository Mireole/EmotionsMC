package fr.mireole.emotions.network;

import fr.mireole.emotions.Emotions;
import fr.mireole.emotions.network.packet.ResetSkinPacket;
import fr.mireole.emotions.network.packet.SkinPacket;
import fr.mireole.emotions.network.packet.SlimPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class EmotionsNetwork {

    public static final ResourceLocation CHANNEL_NAME = new ResourceLocation(Emotions.MOD_ID, "network");

    public static final String PROTOCOL_VERSION = "0.0.0";

    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            CHANNEL_NAME,
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals);

    public static void init() {
        CHANNEL.registerMessage(0, SkinPacket.class,
                SkinPacket::encode,
                SkinPacket::decode,
                SkinPacket::handle);

        CHANNEL.registerMessage(1, ResetSkinPacket.class,
                ResetSkinPacket::encode,
                ResetSkinPacket::decode,
                ResetSkinPacket::handle);

        CHANNEL.registerMessage(2, SlimPacket.class,
                SlimPacket::encode,
                SlimPacket::decode,
                SlimPacket::handle);
    }

}
