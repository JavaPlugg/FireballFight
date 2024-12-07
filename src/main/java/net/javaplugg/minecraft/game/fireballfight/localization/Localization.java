package net.javaplugg.minecraft.game.fireballfight.localization;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.javaplugg.minecraft.game.fireballfight.resources.Resources;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

import java.util.ArrayList;
import java.util.List;

public class Localization {

    private static final MiniMessage miniMessage = MiniMessage.miniMessage();
    private static final JsonObject jsonObject;

    static {
        Gson gson = new Gson();
        String json = Resources.readStringResourceFile("localization.json");
        jsonObject = gson.fromJson(json, JsonObject.class);
    }

    public static Component getText(String key) {
        try {
            String[] keys = key.split("\\.");
            JsonElement jsonElement = jsonObject;
            for (String s : keys) {
                assert jsonElement != null;
                jsonElement = jsonElement.getAsJsonObject().get(s);
            }
            if (!jsonElement.isJsonPrimitive()) {
                throw new RuntimeException("Specified key does not lead to text \"" + key + "\"");
            }
            return miniMessage.deserialize(jsonElement.getAsString());
        } catch (Exception e) {
            throw new RuntimeException("Invalid json key \"" + key + "\"", e);
        }
    }

    public static Animation getAnimation(String key) {
        try {
            String[] keys = key.split("\\.");
            JsonElement jsonElement = jsonObject;
            for (String s : keys) {
                jsonElement = jsonElement.getAsJsonObject().get(s);
            }
            if (jsonElement.isJsonPrimitive()) {
                throw new RuntimeException("Specified key does not lead to animation \"" + key + "\"");
            }
            JsonObject animation = jsonElement.getAsJsonObject();
            int period = animation.get("period").getAsInt();
            JsonArray array = animation.getAsJsonArray("frames");
            List<Component> frames = new ArrayList<>();
            array.forEach(e -> frames.add(miniMessage.deserialize(e.getAsString())));
            return new Animation(period, frames);
        } catch (Exception e) {
            throw new RuntimeException("Invalid json key \"" + key + "\"", e);
        }
    }
}
