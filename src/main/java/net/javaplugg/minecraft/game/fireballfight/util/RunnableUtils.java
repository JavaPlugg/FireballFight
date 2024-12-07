package net.javaplugg.minecraft.game.fireballfight.util;

import net.javaplugg.minecraft.game.fireballfight.FireballFight;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.function.Supplier;

public class RunnableUtils {

    public static void waitWhileAndThen(Supplier<Boolean> condition, Runnable action) {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!condition.get()) {
                    cancel();
                    action.run();
                }
            }
        }.runTaskTimer(FireballFight.getPlugin(), 0, 1);
    }

    public static void runLater(long delay, Runnable runnable) {
        Bukkit.getScheduler().runTaskLater(FireballFight.getPlugin(), runnable, delay);
    }
}
