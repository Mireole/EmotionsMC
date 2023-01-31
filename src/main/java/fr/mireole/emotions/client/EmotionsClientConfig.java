package fr.mireole.emotions.client;

import net.minecraftforge.common.ForgeConfigSpec;

public class EmotionsClientConfig {
    public static final ForgeConfigSpec CLIENT_SPEC;
    public static ForgeConfigSpec.IntValue keyboundTriggers;

    static {
        ForgeConfigSpec.Builder configBuilder = new ForgeConfigSpec.Builder();
        setupConfig(configBuilder);
        CLIENT_SPEC = configBuilder.build();
    }

    private static void setupConfig(ForgeConfigSpec.Builder builder) {
        keyboundTriggers = builder.comment("The amount of keybinds that will be added to the game for trigger use. (Default 3, min 0, max 10)")
                .defineInRange("keybound_triggers", 3, 0, 10);
    }

}
