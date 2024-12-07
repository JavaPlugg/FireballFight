package net.javaplugg.minecraft.game.fireballfight.map;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.javaplugg.minecraft.game.fireballfight.util.WorldUtils;
import org.bukkit.Location;

import java.util.Set;

@RequiredArgsConstructor
public class GameMap {

    @Getter
    private final Location spawnLocation;
    @Getter
    private final Location teamRedSpawnLocation;
    @Getter
    private final Location teamBlueSpawnLocation;
    private final Set<String> allowed;

    private final Location cageLocation1;
    private final Location cageLocation2;

    @Getter
    private final Set<Location> teamRedBedLocations;
    @Getter
    private final Set<Location> teamBlueBedLocations;

    public void destroyCage() {
        WorldUtils.clearArea(cageLocation1, cageLocation2);
    }

    public boolean isAllowed(Location location) {
        String loc = location.getBlockX() + ";" + location.getBlockY() + ";" + location.getBlockZ();
        return allowed.contains(loc);
    }
}
