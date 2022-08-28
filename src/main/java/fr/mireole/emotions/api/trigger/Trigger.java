package fr.mireole.emotions.api.trigger;

import fr.mireole.emotions.Emotions;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod;

import java.io.Serializable;
import java.util.Objects;
import java.util.function.Supplier;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = Emotions.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Trigger implements Serializable {
    private final String name;
    private final Category category;
    private final Supplier<Boolean> condition;
    private final TranslatableComponent translatableName;
    private TranslatableComponent description;
    private boolean inverted;

    public Trigger(String name, Category category, Supplier<Boolean> callable) {
        this(name, category, callable, new TranslatableComponent("trigger.emotions." + name), new TranslatableComponent("trigger.emotions." + name + ".description"));
    }

    public Trigger(String name, Category category, Supplier<Boolean> callable, TranslatableComponent translatableName, TranslatableComponent description) {
        this.name = name;
        this.category = category;
        this.condition = callable;
        this.translatableName = translatableName;
        this.description = description;
    }

    public boolean isActive() {
        return condition.get() ^ inverted;
    }

    public String getName() {
        return name;
    }

    public Category getCategory() {
        return category;
    }

    public TranslatableComponent getTranslatableName() {
        return translatableName;
    }

    public TranslatableComponent getDescription() {
        return description;
    }

    public boolean isInverted() {
        return inverted;
    }

    public void invert() {
        this.setInverted(!inverted);
    }

    public void setInverted(boolean inverted) {
        this.inverted = inverted;
        this.description = new TranslatableComponent("trigger.emotions." + (inverted ? "not_" : "") + name + ".description");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Trigger trigger = (Trigger) o;
        return isInverted() == trigger.isInverted() && name.equals(trigger.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, isInverted());
    }

    @Override
    public String toString() {
        return name + ":" + (isInverted() ? "inverted" : "normal");
    }

    public Trigger copy() {
        return new Trigger(name, category, condition, translatableName, description);
    }

    public record Category(String name) {

        public String getName() {
            return name;
        }

    }

}