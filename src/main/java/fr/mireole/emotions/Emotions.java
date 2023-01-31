package fr.mireole.emotions;

import fr.mireole.emotions.api.skin.SkinManager;
import fr.mireole.emotions.api.trigger.Triggers;
import fr.mireole.emotions.client.EmotionsClientConfig;
import fr.mireole.emotions.client.KeyMappings;
import fr.mireole.emotions.network.EmotionsNetwork;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(Emotions.MOD_ID)
public class Emotions {
    public static final String MOD_ID = "emotions";

    // Directly reference a log4j logger.
    public static final Logger LOGGER = LogManager.getLogger();

    public Emotions() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onClientInit);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onCommonSetup);

        MinecraftForge.EVENT_BUS.register(this);

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, EmotionsClientConfig.CLIENT_SPEC, "emotions.toml");
    }

    public void onCommonSetup(final FMLCommonSetupEvent e) {
        EmotionsNetwork.init();
    }

    private void onClientInit(FMLClientSetupEvent event) {
        KeyMappings.init();
        SkinManager.loadSkins();
        Triggers.addDefaultTriggers();
        Triggers.loadEnabledTriggers();
    }

}
