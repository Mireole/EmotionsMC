package fr.mireole.emotions.api.trigger;

import com.google.gson.*;
import fr.mireole.emotions.Emotions;
import fr.mireole.emotions.api.action.Action;
import fr.mireole.emotions.api.action.ActionDeserializer;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.io.FileUtils;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

@OnlyIn(Dist.CLIENT)
public class Triggers {
    public static final Path TRIGGERS_PATH = Minecraft.getInstance().gameDirectory.toPath().resolve("skins/triggers.json");
    private static final ArrayList<Trigger> TRIGGERS = new ArrayList<>();
    private static final LinkedHashMap<Trigger, Action> ENABLED_TRIGGERS = new LinkedHashMap<>();
    public static final ArrayList<Trigger.Category> CATEGORIES = new ArrayList<>();

    public static void addDefaultTriggers() {
        assert Minecraft.getInstance().player != null;
        addTrigger(new Trigger("sneaking", Categories.MOVEMENT, () -> Minecraft.getInstance().player.isCrouching()));
        addTrigger(new Trigger("sprinting", Categories.MOVEMENT, () -> Minecraft.getInstance().player.isSprinting()));
    }

    /**
     * Add a trigger to the list of available triggers
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

    public static Trigger.Category addCategory(Trigger.Category category) {
        CATEGORIES.add(category);
        return category;
    }

    public static Trigger.Category getCategory(String name) {
        for (Trigger.Category category : CATEGORIES) {
            if (category.getName().equals(name)) {
                return category;
            }
        }
        return null;
    }

    public static Trigger.Category getCategory(int id) {
        return CATEGORIES.get(id);
    }

    public static void removeCategory(Trigger.Category category) {
        CATEGORIES.remove(category);
    }

    public static List<Trigger> getTriggersByCategory(Trigger.Category category) {
        List<Trigger> triggers = new ArrayList<>();
        for (Trigger trigger : TRIGGERS) {
            if (trigger.getCategory() == category) {
                triggers.add(trigger);
            }
        }
        return triggers;
    }

    public static ArrayList<Trigger> getTriggers() {
        return TRIGGERS;
    }

    public static LinkedHashMap<Trigger, Action> getEnabledTriggers() {
        return ENABLED_TRIGGERS;
    }

    public static void enableTrigger(Trigger trigger, Action action) {
        ENABLED_TRIGGERS.put(trigger.copy(), action);
    }

    public static void enableTrigger(Trigger trigger, Action action, boolean inverted) {
        Trigger newTrigger = trigger.copy();
        newTrigger.setInverted(inverted);
        ENABLED_TRIGGERS.put(newTrigger, action);
    }

    public static void disableTrigger(Trigger trigger, Action action) {
        ENABLED_TRIGGERS.remove(trigger, action);
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

    public static void loadEnabledTriggers(){
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(Action.class, new ActionDeserializer());
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
            JsonObject jsonObject = gson.fromJson(json, JsonObject.class);
            for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
                String[] split = entry.getKey().split(":"); // Splits the trigger's name (index 0) and its inversion (index 1)
                Trigger trigger = getTriggerByName(split[0]);
                Action action1 = gson.fromJson(entry.getValue(), Action.class);
                if (trigger != null) {
                    enableTrigger(trigger, action1, split[1].equals("inverted"));
                }
            }
        } catch (IOException | IllegalStateException | JsonSyntaxException | NullPointerException | ArrayIndexOutOfBoundsException e) {
            Emotions.LOGGER.error("Could not load triggers", e);
        }
    }

    public static class Categories {
        public static final Trigger.Category MOVEMENT = new Trigger.Category("movement");
    }

}

