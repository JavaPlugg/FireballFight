package net.javaplugg.minecraft.game.fireballfight.stage.game.listeners;

import net.javaplugg.minecraft.game.fireballfight.FireballFight;
import net.javaplugg.minecraft.game.fireballfight.listener.IGameListener;
import net.javaplugg.minecraft.game.fireballfight.localization.Localization;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.time.Duration;

@SuppressWarnings("unused")
public class JoinQuitListener implements IGameListener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        event.getPlayer().kick(Localization.getText("server.arena_full"));
    }

    @EventHandler
    public void onLogin(PlayerLoginEvent event) {
        event.setResult(PlayerLoginEvent.Result.KICK_OTHER);
        event.kickMessage(Localization.getText("server.arena_full"));
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Bukkit.getOnlinePlayers().forEach(player -> player.showTitle(Title.title(
                Localization.getText("game.win"),
                Component.empty(),
                Title.Times.times(
                        Duration.ofMillis(500),
                        Duration.ofMillis(2500),
                        Duration.ofMillis(1000)
                )
        )));
        FireballFight.getGame().nextStage();
    }

    @Override
    public void onRegister() {
    }

    @Override
    public void onUnregister() {
    }
}
