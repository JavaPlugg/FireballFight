package net.javaplugg.minecraft.game.fireballfight.stage.game.player;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.entity.Player;

@Getter
public class FireballFightPlayer {

    private final Player player;
    private final Team team;
    private final Location spawnLocation;
    @Setter
    private boolean canRespawn = true;

    public FireballFightPlayer(Player player, Team team, Location spawnLocation) {
        this.player = player;
        this.team = team;
        this.spawnLocation = spawnLocation;
    }
}
