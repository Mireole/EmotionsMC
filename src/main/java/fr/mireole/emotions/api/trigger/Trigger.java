package fr.mireole.emotions.api.trigger;

import com.google.gson.JsonObject;
import fr.mireole.emotions.api.TriggersActionPair;
import fr.mireole.emotions.client.screen.widgets.ActiveTriggersList;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;

@OnlyIn(Dist.CLIENT)
public abstract class Trigger implements Serializable {
    // Used for serialization / deserialization
    @SuppressWarnings({"FieldCanBeLocal", "unused"})
    private final String className;
    protected boolean inverted;

    protected Trigger() {
        className = this.getClass().getName();
    }

    public abstract boolean isActive();

    public abstract String getName();

    public abstract Component getTranslatableName();

    public abstract Component getDescription();

    public boolean isInverted() {
        return inverted;
    }

    public void invert() {
        this.setInverted(!inverted);
    }

    public void setInverted(boolean inverted) {
        this.inverted = inverted;
    }

    public abstract @NotNull Trigger copy();

    public abstract ActiveTriggersList.Entry getNewEntry(ActiveTriggersList activeTriggersList, TriggersActionPair pair, boolean chained);

    /*
        * Deserialize a trigger from a JsonObject, called on trigger loading
        @param json the JsonObject to deserialize
        @return the new trigger
     */
    @SuppressWarnings("unused")
    public static Trigger deserialize(JsonObject json) {
        Trigger foundTrigger = Triggers.getTriggerByName(json.get("name").getAsString());
        if (foundTrigger == null) return null;
        Trigger trigger = foundTrigger.copy();
        trigger.setInverted(json.get("inverted").getAsBoolean());
        return trigger;
    }

}