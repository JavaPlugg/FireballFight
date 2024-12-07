package net.javaplugg.minecraft.game.fireballfight;

import lombok.Getter;
import net.javaplugg.minecraft.game.fireballfight.game.FireballFightGame;
import net.javaplugg.minecraft.game.fireballfight.util.RunnableUtils;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

public class FireballFight extends JavaPlugin {

    @Getter
    private static FireballFightGame game;
    @Getter
    private static FireballFight plugin;
    @Getter
    private static World world;

    @Override
    public void onLoad() {
        game = new FireballFightGame();
        game.load();
    }

    @Override
    public void onEnable() {
        plugin = this;
        RunnableUtils.waitWhileAndThen(
                () -> Bukkit.getWorlds().isEmpty(),
                () -> {
                    world = Bukkit.getWorlds().getFirst();
                    game.start();
                }
        );
    }

    
}
