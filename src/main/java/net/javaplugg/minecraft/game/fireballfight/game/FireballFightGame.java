package net.javaplugg.minecraft.game.fireballfight.game;

import lombok.Getter;
import lombok.SneakyThrows;
import net.javaplugg.minecraft.game.fireballfight.FireballFight;
import net.javaplugg.minecraft.game.fireballfight.localization.Localization;
import net.javaplugg.minecraft.game.fireballfight.map.GameMap;
import net.javaplugg.minecraft.game.fireballfight.map.GameMapFactory;
import net.javaplugg.minecraft.game.fireballfight.stage.IGameStage;
import net.javaplugg.minecraft.game.fireballfight.stage.end.StageEnd;
import net.javaplugg.minecraft.game.fireballfight.stage.game.StageGame;
import net.javaplugg.minecraft.game.fireballfight.stage.waiting_for_players.StageWaitingForPlayers;
import net.javaplugg.minecraft.game.fireballfight.util.FileUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class FireballFightGame {

    @Getter
    private GameMap gameMap;

    private final BukkitRunnable tabUpdater = new BukkitRunnable() {
        @Override
        public void run() {
            Bukkit.getOnlinePlayers().forEach(player -> player.sendPlayerListHeaderAndFooter(
                    Localization.getAnimation("server.name").getCurrentFrame().append(Component.text("\n")),
                    Component.text("\n   ").append(Localization.getText("server.ip")).append(Component.text("   "))
            ));
        }
    };

    public FireballFightGame() {
        List<IGameStage> stages = new ArrayList<>();
        stages.add(new StageWaitingForPlayers());
        stages.add(new StageGame());
        stages.add(new StageEnd());
        gameStageIterator = stages.iterator();
    }

    @SneakyThrows
    public void load() {
        Path server = Bukkit.getServer().getWorldContainer().toPath();
        Path maps = server.resolve("maps");
        Path world = server.resolve("world");

        File map = FileUtils.getRandomFile(maps);
        FileUtils.cleanDirectory(world);
        FileUtils.copyDirectoryContentsToDirectory(map, world);
    }

    public void start() {
        tabUpdater.runTaskTimer(FireballFight.getPlugin(), 0, 1);
        createGameMap();
        worldSettings();
        nextStage();
    }

    public void end() {
        tabUpdater.cancel();
    }

    private void worldSettings() {
        World world = FireballFight.getWorld();
        world.setGameRule(GameRule.DO_WEATHER_CYCLE, false);
        world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
        world.setStorm(false);
        world.setThundering(false);
        world.setTime(6000);
    }

    private void createGameMap() {
        File server = Bukkit.getServer().getWorldContainer();
        File world = server.toPath().resolve("world").toFile();
        gameMap = GameMapFactory.create(world);
    }

    private final Iterator<IGameStage> gameStageIterator;
    private IGameStage currentGameStage = null;

    public void nextStage() {
        Bukkit.getScheduler().runTask(FireballFight.getPlugin(), () -> {
            if (currentGameStage != null) {
                currentGameStage.end();
            }
            if (gameStageIterator.hasNext()) {
                currentGameStage = gameStageIterator.next();
                currentGameStage.begin();
            } else {
                end();
            }
        });
    }
}
