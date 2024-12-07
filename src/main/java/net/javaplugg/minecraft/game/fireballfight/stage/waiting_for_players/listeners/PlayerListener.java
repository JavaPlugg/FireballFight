package net.javaplugg.minecraft.game.fireballfight.stage.waiting_for_players.listeners;

import fr.mrmicky.fastboard.FastBoardBase;
import fr.mrmicky.fastboard.adventure.FastBoard;
import net.javaplugg.minecraft.game.fireballfight.FireballFight;
import net.javaplugg.minecraft.game.fireballfight.listener.IGameListener;
import net.javaplugg.minecraft.game.fireballfight.localization.Localization;
import net.javaplugg.minecraft.game.fireballfight.stage.waiting_for_players.timer.Timer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.time.Clock;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@SuppressWarnings("unused")
public class PlayerListener implements IGameListener {

    private final Timer timer = new Timer(20 * 5, () -> FireballFight.getGame().nextStage());
    private final Set<FastBoard> boards = new HashSet<>();
    private boolean enabled = false;

    public PlayerListener() {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!enabled) {
                    cancel();
                    return;
                }
                for (FastBoard board : boards) {
                    if (board.isDeleted()) {
                        continue;
                    }
                    board.updateTitle(Localization.getAnimation("server.name").getCurrentFrame());
                    String date = LocalDateTime.now(Clock.offset(Clock.systemDefaultZone(), Duration.ofHours(3))).format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                    board.updateLines(
                            Component.text(date).color(NamedTextColor.GRAY),
                            Component.empty(),
                            timer.getTimeLeft() != 0 ?
                                    Localization.getText("waiting_for_players.until_start").append(Component.text(": ")).append(Component.text(timer.getTimeSeconds()).color(NamedTextColor.YELLOW)) :
                                    Localization.getAnimation("waiting_for_players.waiting_for_players").getCurrentFrame(),
                            Component.empty(),
                            Localization.getText("server.ip").append(Component.text("   "))
                    );
                }
            }
        }.runTaskTimer(FireballFight.getPlugin(), 0, 1);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        player.setGameMode(GameMode.ADVENTURE);
        player.setLevel(0);
        player.setExp(0);
        player.setTotalExperience(0);

        Collection<? extends Player> players = Bukkit.getOnlinePlayers();

        if (players.size() > 2) {
            player.kick(Localization.getText("server.arena_full"));
            return;
        }

        event.getPlayer().teleport(FireballFight.getGame().getGameMap().getSpawnLocation());

        if (players.size() >= 2) {
            timer.start();
        }
        boards.add(new FastBoard(player));
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        for (FastBoard board : boards) {
            if (board.getPlayer().equals(player)) {
                boards.remove(board);
                break;
            }
        }
        timer.stopAndReset();
    }

    @Override
    public void onRegister() {
        enabled = true;
    }

    @Override
    public void onUnregister() {
        enabled = false;
        boards.forEach(FastBoardBase::delete);
        timer.stopAndReset();
    }
}
