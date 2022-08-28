package fr.mireole.emotions.events;

import fr.mireole.emotions.Emotions;
import fr.mireole.emotions.api.skin.Skin;
import fr.mireole.emotions.api.skin.SkinManager;
import fr.mireole.emotions.api.skin.SkinSwapper;
import fr.mireole.emotions.api.trigger.Triggers;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = Emotions.MOD_ID)
public class ClientPlayerEvents {
    private static int cooldown = 1;

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event){
        new Thread(() -> {
            Player player = Minecraft.getInstance().player;
            if(player != null && player.isAlive() && event.phase == TickEvent.Phase.START){
                if(cooldown < 2){
                    cooldown++;
                }
                else {
                    Triggers.getEnabledTriggers().forEach((trigger, action) -> {
                        if (action.isValid() && trigger.isActive() && cooldown >= 2) {
                            action.execute();
                            cooldown = 0;
                        }
                    });
                }
            }
        }).start();
    }

}
