package net.javaplugg.minecraft.game.fireballfight.listener;

import org.bukkit.event.Listener;

public interface IGameListener extends Listener {

    void onRegister();
    void onUnregister();
}
