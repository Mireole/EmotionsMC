package fr.mireole.emotions.client;

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
import org.lwjgl.glfw.GLFW;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(modid = Emotions.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class KeyMappings {
    public static final String KEY_CATEGORY = "key.categories.emotions";
    public static KeyMapping mainGuiKey;

    public static void init(){
        mainGuiKey = new KeyMapping("key.emotions.main_gui_key", GLFW.GLFW_KEY_0, KEY_CATEGORY);
        ClientRegistry.registerKeyBinding(mainGuiKey);
    }

    @SubscribeEvent
    public static void onKeyInput(InputEvent.KeyInputEvent e){
        Minecraft minecraft = Minecraft.getInstance();
        if(minecraft.level == null) return;
        onInput(minecraft);
    }

    @SubscribeEvent
    public static void onMouseInput(InputEvent.MouseInputEvent e){
        Minecraft minecraft = Minecraft.getInstance();
        if(minecraft.level == null) return;
        onInput(minecraft);
    }

    public static void onInput(Minecraft mc){
        if(mc.screen != null) return;
        if(mainGuiKey.isDown()){
            Minecraft.getInstance().setScreen(new EmotionsMainScreen());
        }
    }

}
