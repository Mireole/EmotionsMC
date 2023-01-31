package fr.mireole.emotions.client;

import com.mojang.blaze3d.platform.InputConstants;
import fr.mireole.emotions.Emotions;
import fr.mireole.emotions.client.screen.EmotionsMainScreen;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.ClientRegistry;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(modid = Emotions.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class KeyMappings {
    public static final String KEY_CATEGORY = "key.categories.emotions";
    public static KeyMapping mainGuiKey;
    public static KeyMapping[] triggerKeys;

    public static void init() {
        mainGuiKey = new KeyMapping("key.emotions.main_gui_key", InputConstants.KEY_RSHIFT, KEY_CATEGORY);
        ClientRegistry.registerKeyBinding(mainGuiKey);
        initTriggerKeyBindings(EmotionsClientConfig.keyboundTriggers.get());
    }

    public static void initTriggerKeyBindings(int amount) {
        triggerKeys = new KeyMapping[amount];
        for (int i = 0; i < amount; i++) {
            KeyMapping key = new KeyMapping("key.emotions.trigger_key_" + i, InputConstants.UNKNOWN.getValue(), KEY_CATEGORY);
            triggerKeys[i] = key;
            ClientRegistry.registerKeyBinding(key);
        }
    }

    @SubscribeEvent
    public static void onKeyInput(InputEvent.KeyInputEvent e) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level == null) return;
        onInput(minecraft);
    }

    @SubscribeEvent
    public static void onMouseInput(InputEvent.MouseInputEvent e) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level == null) return;
        onInput(minecraft);
    }

    public static void onInput(Minecraft mc) {
        if (mc.screen != null) return;
        if (mainGuiKey.isDown()) {
            Minecraft.getInstance().setScreen(new EmotionsMainScreen());
        }
    }

}
