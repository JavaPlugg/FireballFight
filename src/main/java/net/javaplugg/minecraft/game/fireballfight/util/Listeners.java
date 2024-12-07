package net.javaplugg.minecraft.game.fireballfight.util;

import net.javaplugg.minecraft.game.fireballfight.FireballFight;
import net.javaplugg.minecraft.game.fireballfight.listener.IGameListener;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;

public class Listeners {

    public static void register(IGameListener listener) {
        Bukkit.getPluginManager().registerEvents(listener, FireballFight.getPlugin());
        listener.onRegister();
    }

    public static void unregister(IGameListener listener) {
        HandlerList.unregisterAll(listener);
        listener.onUnregister();
    }
}
