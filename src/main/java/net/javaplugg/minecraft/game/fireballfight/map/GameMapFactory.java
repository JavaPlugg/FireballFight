package net.javaplugg.minecraft.game.fireballfight.map;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.SneakyThrows;
import net.javaplugg.minecraft.game.fireballfight.FireballFight;
import org.bukkit.Location;

import java.io.File;
import java.nio.file.Files;
import java.util.HashSet;
import java.util.Set;

public class GameMapFactory {

    @SneakyThrows
    public static GameMap create(File map) {
        Gson gson = new Gson();
        String json = Files.readString(map.toPath().resolve("fireball-fight-map.json"));
        JsonObject jsonObject = gson.fromJson(json, JsonObject.class);
        Location spawnLocation = parseLocation(jsonObject.get("spawn").getAsString());
        Location teamRedSpawnLocation = parseLocation(jsonObject.get("red").getAsString());
        Location teamBlueSpawnLocation = parseLocation(jsonObject.get("blue").getAsString());

        Location cageLocation1 = parseLocation(jsonObject.get("cage_location_1").getAsString());
        Location cageLocation2 = parseLocation(jsonObject.get("cage_location_2").getAsString());

        Set<String> allowed = new HashSet<>();
        jsonObject.getAsJsonArray("allowed").forEach(jsonElement -> allowed.add(jsonElement.getAsString()));

        Set<Location> teamRedBedLocations = new HashSet<>();
        Set<Location> teamBlueBedLocations = new HashSet<>();

        teamRedBedLocations.add(parseLocation(jsonObject.get("red_bed_1").getAsString()));
        teamRedBedLocations.add(parseLocation(jsonObject.get("red_bed_2").getAsString()));
        teamBlueBedLocations.add(parseLocation(jsonObject.get("blue_bed_1").getAsString()));
        teamBlueBedLocations.add(parseLocation(jsonObject.get("blue_bed_2").getAsString()));

        return new GameMap(
                spawnLocation,
                teamRedSpawnLocation,
                teamBlueSpawnLocation,
                allowed,
                cageLocation1,
                cageLocation2,
                teamRedBedLocations,
                teamBlueBedLocations
        );
    }

    private static Location parseLocation(String str) {
        String[] arr = str.split(";");
        if (arr.length != 3 && arr.length != 5) {
            throw new IllegalArgumentException("Cannot parse location \"" + str + "\"");
        }
        double x = Double.parseDouble(arr[0]);
        double y = Double.parseDouble(arr[1]);
        double z = Double.parseDouble(arr[2]);
        Location location = new Location(FireballFight.getWorld(), x, y, z);
        if (arr.length == 5) {
            float yaw = Float.parseFloat(arr[3]);
            float pitch = Float.parseFloat(arr[4]);
            location.setYaw(yaw);
            location.setPitch(pitch);
        }
        return location;
    }
}
