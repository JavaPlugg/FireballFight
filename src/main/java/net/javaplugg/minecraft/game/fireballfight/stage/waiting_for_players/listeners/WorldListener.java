package net.javaplugg.minecraft.game.fireballfight.stage.waiting_for_players.listeners;

import net.javaplugg.minecraft.game.fireballfight.listener.IGameListener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

@SuppressWarnings("unused")
public class WorldListener implements IGameListener {

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        event.setCancelled(true);
    }

    @Override
    public void onRegister() {
    }

    @Override
    public void onUnregister() {
    }
}
