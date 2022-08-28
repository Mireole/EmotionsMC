package fr.mireole.emotions.api.action;

import com.google.gson.*;
import fr.mireole.emotions.Emotions;

import java.lang.reflect.Type;

/**
 * Used to deserialize an action.
 * The basic Gson deserializer is not sufficient because it does not support inheritance, or we want to be able to deserialize any child of {@link Action}
 */
public class ActionDeserializer implements JsonDeserializer<Action> {

    @Override
    public Action deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        JsonPrimitive className= (JsonPrimitive) jsonObject.get("className");

        String className1 = className.getAsString();

        Class<?> clazz;
        try {
            clazz = Class.forName(className1);
            if (!Action.class.isAssignableFrom(clazz)) {
                throw new JsonSyntaxException("The specified class is not an instance of Action");
            }
        } catch (ClassNotFoundException e) {
            Emotions.LOGGER.error("Could not deserialize action from json : " + json.getAsString(), e);
            return null;
        }
        return context.deserialize(jsonObject, clazz);
    }
}
