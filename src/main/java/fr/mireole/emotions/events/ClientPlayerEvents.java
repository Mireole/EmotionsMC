package fr.mireole.emotions.events;

import fr.mireole.emotions.Emotions;
import fr.mireole.emotions.api.trigger.Triggers;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = Emotions.MOD_ID)
public class ClientPlayerEvents {
    private static int tick = 1;
    private static final int COOLDOWN = 2;
    private static final Executor executor = Executors.newSingleThreadExecutor();

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        executor.execute(() -> {
            Player player = Minecraft.getInstance().player;
            if (player != null && player.isAlive() && event.phase == TickEvent.Phase.START) {
                if (tick < COOLDOWN) {
                    tick++;
                } else {
                    Triggers.getEnabledTriggers().forEach((pair) -> {
                        final AtomicBoolean triggersAreActive = new AtomicBoolean(true);
                        pair.getTriggers().forEach((trigger) -> {
                            if (!trigger.isActive()) {
                                triggersAreActive.set(false);
                            }
                        });
                        if (pair.getAction().isValid() && triggersAreActive.get() && tick >= COOLDOWN) {
                            pair.getAction().execute();
                            tick = 0;
                        }
                    });
                }
            }
        });
    }

}
