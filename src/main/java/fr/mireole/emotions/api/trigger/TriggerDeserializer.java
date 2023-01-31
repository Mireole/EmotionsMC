package fr.mireole.emotions.api.trigger;

import com.google.gson.*;
import fr.mireole.emotions.Emotions;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;

/**
 * Used to deserialize a trigger.
 * The basic Gson deserializer is not sufficient because it does not support inheritance, or we want to be able to deserialize any child of {@link Trigger}
 */
public class TriggerDeserializer implements JsonDeserializer<Trigger> {

    @Override
    public Trigger deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        JsonPrimitive className = (JsonPrimitive) jsonObject.get("className");

        String className1 = className.getAsString();
        Class<?> clazz;
        try {
            clazz = Class.forName(className1);
            if (!Trigger.class.isAssignableFrom(clazz)) {
                throw new JsonSyntaxException("The specified class is not an instance of Trigger");
            }
        } catch (ClassNotFoundException e) {
            Emotions.LOGGER.error("Could not deserialize trigger from json : " + json.getAsString(), e);
            return null;
        }
        try {
            return (Trigger) clazz.getMethod("deserialize", JsonObject.class).invoke(null, jsonObject);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }
}
