package fr.mireole.emotions.api.action;

import fr.mireole.emotions.client.screen.TriggersScreen;
import net.minecraft.client.gui.components.Widget;
import net.minecraft.client.gui.components.events.GuiEventListener;

import java.io.Serializable;

public abstract class Action implements Widget, GuiEventListener, Serializable {
    // Used for serialization / deserialization
    @SuppressWarnings({"FieldCanBeLocal", "unused"})
    private final String className;

    public Action(){
        className = this.getClass().getName();
    }

    public abstract void setComponentPos(TriggersScreen screen, int index, int left, int top, int width, int height);

    public abstract void execute();

    public abstract void tick();

    public abstract boolean isValid();
}
