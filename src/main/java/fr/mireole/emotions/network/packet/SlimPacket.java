package fr.mireole.emotions.network.packet;

import fr.mireole.emotions.api.skin.SkinSwapper;
import fr.mireole.emotions.network.EmotionsNetwork;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public record SlimPacket(UUID playerUUID, boolean slim) {

    public static void encode(SlimPacket packet, FriendlyByteBuf buffer) {
        buffer.writeUUID(packet.playerUUID);
        buffer.writeBoolean(packet.slim);
    }

    public static SlimPacket decode(FriendlyByteBuf buffer) {
        return new SlimPacket(buffer.readUUID(), buffer.readBoolean());
    }

    public static void handle(SlimPacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            switch (context.getDirection()) {
                case PLAY_TO_CLIENT -> {
                    ClientLevel level = Minecraft.getInstance().level;
                    if (level != null) {
                        Player player = level.getPlayerByUUID(packet.playerUUID);
                        SkinSwapper.setSlim(player, packet.slim);
                    }
                }
                case PLAY_TO_SERVER -> {
                    ServerPlayer sender = context.getSender();
                    if (sender != null) {
                        SlimPacket newPacket = new SlimPacket(sender.getUUID(), packet.slim);
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
