package net.javaplugg.minecraft.game.fireballfight.stage.end.listeners;

import net.javaplugg.minecraft.game.fireballfight.listener.IGameListener;
import net.javaplugg.minecraft.game.fireballfight.localization.Localization;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;

@SuppressWarnings("unused")
public class JoinListener implements IGameListener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        event.getPlayer().kick(Localization.getText("server.arena_full"));
    }

    @EventHandler
    public void onLogin(PlayerLoginEvent event) {
        event.setResult(PlayerLoginEvent.Result.KICK_OTHER);
        event.kickMessage(Localization.getText("server.arena_full"));
    }

    @Override
    public void onRegister() {
    }

    @Override
    public void onUnregister() {
    }
}
