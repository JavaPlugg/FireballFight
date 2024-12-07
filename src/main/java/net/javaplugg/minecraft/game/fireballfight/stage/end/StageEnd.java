package net.javaplugg.minecraft.game.fireballfight.stage.end;

import net.javaplugg.minecraft.game.fireballfight.FireballFight;
import net.javaplugg.minecraft.game.fireballfight.stage.IGameStage;
import net.javaplugg.minecraft.game.fireballfight.stage.end.listeners.JoinListener;
import net.javaplugg.minecraft.game.fireballfight.util.Listeners;
import net.javaplugg.minecraft.game.fireballfight.util.RunnableUtils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;

public class StageEnd implements IGameStage {

    @Override
    public void begin() {
        Bukkit.getOnlinePlayers().forEach(player -> {
            player.getInventory().clear();
            player.setGameMode(GameMode.SPECTATOR);
        });
        FireballFight.getWorld().setPVP(false);
        Listeners.register(new JoinListener());
        RunnableUtils.runLater(20 * 15, () -> Bukkit.getServer().shutdown());
    }

    @Override
    public void end() {
    }
}
