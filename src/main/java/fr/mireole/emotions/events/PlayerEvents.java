package fr.mireole.emotions.events;

import fr.mireole.emotions.Emotions;
import fr.mireole.emotions.network.EmotionsNetwork;
import fr.mireole.emotions.network.packet.SkinPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.PacketDistributor;

import java.io.File;
import java.util.Objects;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = Emotions.MOD_ID)
public class PlayerEvents {

    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event){
        if(!event.getPlayer().level.isClientSide){
            File file = new File("skins/downloaded");
            if(file.exists()){
                for (File file1 : Objects.requireNonNull(file.listFiles())) {
                    try {
                        String name = file1.getName().replace(".png", "");
                        Player player = event.getPlayer().level.getPlayerByUUID(UUID.fromString(name));
                        if (player != null) {
                            EmotionsNetwork.CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) event.getPlayer()), new SkinPacket(file1.getName(), file1.toPath(), player.getUUID()));
                        }
                    }
                    catch (IllegalArgumentException ignored) {

                    }
                }
            }
            File file2 = new File(event.getPlayer().getUUID() + ".png");
            if(file2.exists()){
                Objects.requireNonNull(event.getPlayer().getServer()).getPlayerList().broadcastAll(
                        EmotionsNetwork.CHANNEL.toVanillaPacket(new SkinPacket(file2.getName(), file2.toPath(), event.getPlayer().getUUID()), NetworkDirection.PLAY_TO_CLIENT)
                );
            }
        }


    }

    @SubscribeEvent
    public static void startTracking(PlayerEvent.StartTracking event){
        if(!event.getPlayer().level.isClientSide() && event.getTarget() instanceof Player player){
            if(player.getUUID().equals(event.getPlayer().getUUID())){
                return;
            }
            File file = new File("skins/downloaded/" + player.getUUID() + ".png");
            if(file.exists()){
                EmotionsNetwork.CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) event.getPlayer()), new SkinPacket(file.getName(), file.toPath(), player.getUUID()));
            }
        }

    }

}
