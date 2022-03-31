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

import java.util.UUID;
import java.util.function.Supplier;

public record ModelNamePacket(UUID playerUUID, String modelName) {

    public static void encode(ModelNamePacket packet, FriendlyByteBuf buffer) {
        buffer.writeUUID(packet.playerUUID);
        buffer.writeUtf(packet.modelName);
    }

    public static ModelNamePacket decode(FriendlyByteBuf buffer) {
        return new ModelNamePacket(buffer.readUUID(), buffer.readUtf());
    }

    public static void handle(ModelNamePacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            switch (context.getDirection()) {
                case PLAY_TO_CLIENT -> {
                    ClientLevel level = Minecraft.getInstance().level;
                    if (level != null) {
                        Player player = level.getPlayerByUUID(packet.playerUUID);
                        if (player instanceof AbstractClientPlayer clientPlayer) {
                            SkinSwapper.setModelNameFor(clientPlayer, packet.modelName);
                        }
                    }
                }
                case PLAY_TO_SERVER -> {
                    ServerPlayer sender = context.getSender();
                    if (sender != null) {
                        ModelNamePacket newPacket = new ModelNamePacket(sender.getUUID(), packet.modelName);
                        sender.server.getPlayerList().broadcastAll(
                                EmotionsNetwork.CHANNEL.toVanillaPacket(newPacket, NetworkDirection.PLAY_TO_CLIENT)
                        );
                    }
                }
            }
        });
        context.setPacketHandled(true);
    }

}
