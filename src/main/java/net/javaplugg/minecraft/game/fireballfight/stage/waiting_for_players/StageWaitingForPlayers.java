package net.javaplugg.minecraft.game.fireballfight.stage.waiting_for_players;

import net.javaplugg.minecraft.game.fireballfight.FireballFight;
import net.javaplugg.minecraft.game.fireballfight.listener.IGameListener;
import net.javaplugg.minecraft.game.fireballfight.stage.IGameStage;
import net.javaplugg.minecraft.game.fireballfight.stage.waiting_for_players.listeners.InventoryListener;
import net.javaplugg.minecraft.game.fireballfight.stage.waiting_for_players.listeners.PlayerListener;
import net.javaplugg.minecraft.game.fireballfight.stage.waiting_for_players.listeners.WorldListener;
import net.javaplugg.minecraft.game.fireballfight.util.Listeners;
import net.javaplugg.minecraft.game.fireballfight.util.WorldUtils;

import java.util.ArrayList;
import java.util.List;

public class StageWaitingForPlayers implements IGameStage {

    private final List<IGameListener> listeners = new ArrayList<>();

    @Override
    public void begin() {
        FireballFight.getWorld().setPVP(false);
        listeners.add(new InventoryListener());
        listeners.add(new PlayerListener());
        listeners.add(new WorldListener());
        listeners.forEach(Listeners::register);
    }

    @Override
    public void end() {
        listeners.forEach(Listeners::unregister);
        FireballFight.getGame().getGameMap().destroyCage();
        WorldUtils.killItems();
    }
}
