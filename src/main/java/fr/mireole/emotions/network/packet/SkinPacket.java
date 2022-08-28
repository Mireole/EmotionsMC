package fr.mireole.emotions.network.packet;

import fr.mireole.emotions.api.skin.SkinManager;
import fr.mireole.emotions.api.skin.SkinSwapper;
import fr.mireole.emotions.network.EmotionsNetwork;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;
import java.util.function.Supplier;

public class SkinPacket {
    private Path image;
    private final String imageName;
    private final UUID player;
    private byte[] bytes;

    public SkinPacket(String imageName, Path image, UUID player){
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
            byte[] bytes = Files.readAllBytes(packet.image);
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
        Path path1 = Path.of("skins/downloaded");
        try {
            Files.createDirectories(path1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String name = "";
        if(context.getDirection() == NetworkDirection.PLAY_TO_CLIENT){
            name = packet.imageName;
        }
        else if (context.getDirection() == NetworkDirection.PLAY_TO_SERVER){
            if (context.getSender() != null) {
                name = context.getSender().getUUID() + ".png";
            }
        }
        Path path = Path.of(path1.toString(), name);
        byte[] bytes = packet.bytes;
        if(bytes.length > 8192) throw new IllegalArgumentException("The image size must be lower or equal to 8192");
        try {
            Files.write(path, bytes);
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
                        if (player != null) {
                            SkinSwapper.setSkinFor(player, SkinManager.createSkin(path, SkinSwapper.isSlim(player)));
                        }
                    }
                }
                case PLAY_TO_SERVER -> {
                    ServerPlayer sender = context.getSender();
                    if (sender != null) {
                        SkinPacket skinPacket = new SkinPacket(sender.getUUID() + ".png", path, sender.getUUID());
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
