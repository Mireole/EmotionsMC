package fr.mireole.emotions.api.trigger;

import com.google.common.collect.ArrayListMultimap;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.*;

@OnlyIn(Dist.CLIENT)
public class Triggers {
    private static final ArrayList<Trigger> TRIGGERS = new ArrayList<>();
    private static final ArrayListMultimap<Trigger, Trigger.Action> ENABLED_TRIGGERS = ArrayListMultimap.create();
    public static final ArrayList<Trigger.Category> CATEGORIES = new ArrayList<>();

    public static final Trigger SNEAKING = addTrigger(new Trigger("sneaking", Categories.MOVEMENT, () -> Minecraft.getInstance().player.isCrouching()));
    public static final Trigger SPRINTING = addTrigger(new Trigger("sprinting", Categories.MOVEMENT, () -> Minecraft.getInstance().player.isSprinting()));

    public static Trigger addTrigger(Trigger trigger) {
        TRIGGERS.add(trigger);
        return trigger;
    }

    public static Trigger getTrigger(String name) {
        for (Trigger trigger : TRIGGERS) {
            if (trigger.getName().equals(name)) {
                return trigger;
            }
        }
        return null;
    }

    public static Trigger getTrigger(int id) {
        return TRIGGERS.get(id);
    }

    public static Trigger getTrigger(Component component) {
        for (Trigger trigger : TRIGGERS) {
            if (trigger.getComponent().toString().equals(component.toString())) {
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

    public static ArrayListMultimap<Trigger, Trigger.Action> getEnabledTriggers() {
        return ENABLED_TRIGGERS;
    }

    public static void setAction(Trigger trigger, Trigger.Action oldAction, Trigger.Action newAction) {
        disableTrigger(trigger, oldAction);
        enableTrigger(trigger, newAction);
    }

    public static void enableTrigger(Trigger trigger, Trigger.Action action) {
        ENABLED_TRIGGERS.put(trigger, action);
    }

    public static void disableTrigger(Trigger trigger, Trigger.Action action) {
        ENABLED_TRIGGERS.remove(trigger, action);
    }

    public static class Categories {
        public static final Trigger.Category MOVEMENT = new Trigger.Category("movement");
    }

}

