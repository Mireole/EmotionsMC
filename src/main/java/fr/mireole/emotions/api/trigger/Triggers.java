package fr.mireole.emotions.api.trigger;

import com.google.gson.*;
import fr.mireole.emotions.Emotions;
import fr.mireole.emotions.api.TriggersActionPair;
import fr.mireole.emotions.api.action.Action;
import fr.mireole.emotions.api.action.ActionDeserializer;
import fr.mireole.emotions.client.EmotionsClientConfig;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.io.FileUtils;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedList;

@OnlyIn(Dist.CLIENT)
public class Triggers {
    public static final Path TRIGGERS_PATH = Minecraft.getInstance().gameDirectory.toPath().resolve("skins/triggers.json");
    private static final ArrayList<Trigger> TRIGGERS = new ArrayList<>();
    private static final LinkedList<TriggersActionPair> ENABLED_TRIGGERS = new LinkedList<>();

    public static void addDefaultTriggers() {
        assert Minecraft.getInstance().player != null;
        addTrigger(new ConditionTrigger("sneaking", () -> Minecraft.getInstance().player.isCrouching()));
        addTrigger(new ConditionTrigger("sprinting", () -> Minecraft.getInstance().player.isSprinting()));
        addTrigger(new ConditionTrigger("on_ground", () -> Minecraft.getInstance().player.isOnGround()));
        addTrigger(new ConditionTrigger("dead", () -> Minecraft.getInstance().player.isDeadOrDying()));
        addTrigger(new ConditionTrigger("in_water", () -> Minecraft.getInstance().player.isInWater()));
        addTrigger(new ConditionTrigger("in_lava", () -> Minecraft.getInstance().player.isInLava()));
        addTrigger(new ConditionTrigger("burning", () -> Minecraft.getInstance().player.isOnFire()));
        addTrigger(new ConditionTrigger("swimming", () -> Minecraft.getInstance().player.isSwimming()));
        addTrigger(new ConditionTrigger("elytra_flying", () -> Minecraft.getInstance().player.isFallFlying()));
        addTrigger(new ConditionTrigger("freezing", () -> Minecraft.getInstance().player.isFreezing()));
        addTrigger(new ConditionTrigger("sleeping", () -> Minecraft.getInstance().player.isSleeping()));
        addTrigger(new ConditionTrigger("passenger", () -> Minecraft.getInstance().player.isPassenger()));
        addTrigger(new ConditionTrigger("crawling", () -> Minecraft.getInstance().player.isVisuallyCrawling()));
        addTrigger(new SkinTrigger());
        if (EmotionsClientConfig.keyboundTriggers.get() > 0) {
            addTrigger(new KeyTrigger());
        }
    }

    /**
     * Add a trigger to the list of available triggers
     *
     * @param trigger The trigger that will be added to the list
     */
    public static void addTrigger(Trigger trigger) {
        TRIGGERS.add(trigger);
    }

    /**
     * @param name The name of the trigger
     * @return null if the trigger doesn't exist, the trigger otherwise
     */
    public static Trigger getTriggerByName(String name) {
        for (Trigger trigger : TRIGGERS) {
            if (trigger.getName().equals(name)) {
                return trigger;
            }
        }
        return null;
    }


    public static ArrayList<Trigger> getTriggers() {
        return TRIGGERS;
    }

    public static LinkedList<TriggersActionPair> getEnabledTriggers() {
        return ENABLED_TRIGGERS;
    }

    public static void enableTrigger(Trigger trigger, Action action) {
        ENABLED_TRIGGERS.add(new TriggersActionPair(trigger.copy(), action));
    }

    public static void enableTrigger(TriggersActionPair pair) {
        ENABLED_TRIGGERS.add(pair);
    }

    public static void disableTrigger(TriggersActionPair pair) {
        ENABLED_TRIGGERS.remove(pair);
    }

    public static void saveEnabledTriggers() {
        GsonBuilder builder = new GsonBuilder().setPrettyPrinting();
        Gson gson = builder.create();
        String json = gson.toJson(ENABLED_TRIGGERS);
        try {
            FileUtils.writeStringToFile(TRIGGERS_PATH.toFile(), json, (Charset) null);
        } catch (IOException e) {
            Emotions.LOGGER.error("Could not save triggers", e);
        }
    }

    public static void loadEnabledTriggers() {
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(Action.class, new ActionDeserializer());
        builder.registerTypeAdapter(Trigger.class, new TriggerDeserializer());
        Gson gson = builder.create();
        if (!TRIGGERS_PATH.toFile().exists()) {
            try {
                Files.createFile(TRIGGERS_PATH);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }
        ENABLED_TRIGGERS.clear();
        try {
            String json = FileUtils.readFileToString(TRIGGERS_PATH.toFile(), (Charset) null);
            JsonArray jsonArray = gson.fromJson(json, JsonArray.class);
            jsonArray.forEach(jsonElement -> {
                JsonObject jsonObject = jsonElement.getAsJsonObject();
                JsonArray triggers = jsonObject.getAsJsonArray("triggers");
                LinkedList<Trigger> triggerList = new LinkedList<>();
                triggers.forEach(trigger -> {
                    Trigger trigger1 = gson.fromJson(trigger, Trigger.class);
                    triggerList.add(trigger1);
                });
                Action action = gson.fromJson(jsonObject.get("action"), Action.class);
                enableTrigger(new TriggersActionPair(triggerList, action));
            });
        } catch (IOException | IllegalStateException | JsonSyntaxException | NullPointerException |
                 ArrayIndexOutOfBoundsException e) {
            Emotions.LOGGER.error("Could not load triggers", e);
        }
    }

}

