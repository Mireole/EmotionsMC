package fr.mireole.emotions.api.trigger;

import fr.mireole.emotions.Emotions;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod;

import java.util.function.BiConsumer;
import java.util.function.Supplier;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = Emotions.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Trigger {
    private final String name;
    private final Category category;
    private final Supplier<Boolean> condition;
    private final TranslatableComponent component;

    public Trigger(String name, Category category, Supplier<Boolean> callable) {
        this(name, category, callable, new TranslatableComponent("trigger.emotions." + name));
    }

    public Trigger(String name, Category category, Supplier<Boolean> callable, TranslatableComponent component) {
        this.name = name;
        this.category = category;
        this.condition = callable;
        this.component = component;
    }

    public boolean isActive() {
        return condition.get();
    }

    public String getName() {
        return name;
    }

    public Category getCategory() {
        return category;
    }

    public TranslatableComponent getComponent() {
        return component;
    }

    public record Category(String name) {

        public String getName() {
            return name;
        }

    }

    public record Action(String skinName, Boolean slim){

    }


}