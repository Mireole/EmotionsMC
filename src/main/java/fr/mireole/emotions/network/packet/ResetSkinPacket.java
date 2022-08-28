package fr.mireole.emotions.network.packet;

import fr.mireole.emotions.Emotions;
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

public record ResetSkinPacket(UUID player) {

    public static void encode(ResetSkinPacket packet, FriendlyByteBuf buffer) {
        buffer.writeUUID(packet.player);
    }

    public static ResetSkinPacket decode(FriendlyByteBuf buffer) {
        return new ResetSkinPacket(buffer.readUUID());
    }

    public static void handle(ResetSkinPacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            switch (context.getDirection()) {
                case PLAY_TO_CLIENT -> {
                    ClientLevel level = Minecraft.getInstance().level;
                    if (level != null) {
                        Player player = level.getPlayerByUUID(packet.player);
                        SkinSwapper.resetSkinFor(player);
                    }
                }
                case PLAY_TO_SERVER -> {
                    ServerPlayer sender = context.getSender();
                    if (sender != null) {
                        Path path = Path.of("skins/downloaded/"+sender.getUUID()+".png");
                        try {
                            Files.deleteIfExists(path);
                        } catch (IOException e) {
                            Emotions.LOGGER.error("Couldn't delete skin file with path " + path, e);
                        }
                        ResetSkinPacket skinPacket = new ResetSkinPacket(sender.getUUID());
                        sender.server.getPlayerList().broadcastAll(EmotionsNetwork.CHANNEL.toVanillaPacket(skinPacket, NetworkDirection.PLAY_TO_CLIENT)
                        );
                    }
                }
            }
        });
        context.setPacketHandled(true);
    }

}
