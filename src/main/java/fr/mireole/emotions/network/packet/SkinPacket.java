package fr.mireole.emotions.network.packet;

import fr.mireole.emotions.api.SkinSwapper;
import fr.mireole.emotions.network.EmotionsNetwork;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.UUID;
import java.util.function.Supplier;

public class SkinPacket {
    private File image;
    private final String imageName;
    private final UUID player;
    private byte[] bytes;

    public SkinPacket(String imageName, File image, UUID player){
        this.imageName = imageName;
        this.image = image;
        this.player = player;
    }

    public SkinPacket(String imageName, byte[] bytes, UUID player){
        this.imageName = imageName;
        this.bytes = bytes;
        this.player = player;
    }

    public static void encode(SkinPacket packet, FriendlyByteBuf buffer){
        try {
            if(!packet.imageName.endsWith(".png")) throw new IllegalArgumentException("The image must be a .png");
            buffer.writeUtf(packet.imageName);
            byte[] bytes = Files.readAllBytes(packet.image.toPath());
            if(bytes.length > 8192) throw new IllegalArgumentException("The image size must be lower or equal to 8192");
            buffer.writeUUID(packet.player);
            buffer.writeByteArray(bytes);

        } catch (IOException | IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    public static SkinPacket decode(FriendlyByteBuf buffer){
        String imageName = null;
        byte[] bytes = null;
        UUID player = null;
        try {
            imageName = buffer.readUtf();
            if(!imageName.endsWith(".png")) throw new IllegalArgumentException("The image must be a .png");
            player = buffer.readUUID();
            bytes = buffer.readByteArray();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        return new SkinPacket(imageName, bytes, player);
    }

    public static void handle(SkinPacket packet, Supplier<NetworkEvent.Context> contextSupplier){
        NetworkEvent.Context context = contextSupplier.get();
        File file1 = new File("skins/downloaded");
        file1.mkdirs();
        String name = "";
        if(context.getDirection() == NetworkDirection.PLAY_TO_CLIENT){
            name = packet.imageName;
        }
        else if (context.getDirection() == NetworkDirection.PLAY_TO_SERVER){
            name = context.getSender().getUUID() + ".png";
        }
        File file = new File(file1, name);
        byte[] bytes = packet.bytes;
        if(bytes.length > 8192) throw new IllegalArgumentException("The image size must be lower or equal to 8192");
        try {
            FileOutputStream outputStream = new FileOutputStream(file);
            outputStream.write(bytes);
            outputStream.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        context.enqueueWork(() -> {
            switch (context.getDirection()) {
                case PLAY_TO_CLIENT -> {
                    ClientLevel level = Minecraft.getInstance().level;
                    if (level != null) {
                        Player player = level.getPlayerByUUID(packet.player);
                        if (player instanceof AbstractClientPlayer clientPlayer) {
                            SkinSwapper.setSkinFor(clientPlayer, file);
                        }
                    }
                }
                case PLAY_TO_SERVER -> {
                    ServerPlayer sender = context.getSender();
                    if (sender != null) {
                        SkinPacket skinPacket = new SkinPacket(sender.getUUID() + ".png", file, sender.getUUID());
                        sender.server.getPlayerList().broadcastAll(
                                EmotionsNetwork.CHANNEL.toVanillaPacket(skinPacket, NetworkDirection.PLAY_TO_CLIENT)
                        );
                    }
                }
            }
        });
        context.setPacketHandled(true);
    }

}
