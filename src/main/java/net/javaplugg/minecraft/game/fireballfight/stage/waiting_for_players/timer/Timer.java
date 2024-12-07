package net.javaplugg.minecraft.game.fireballfight.stage.waiting_for_players.timer;

import lombok.Getter;
import net.javaplugg.minecraft.game.fireballfight.FireballFight;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.scheduler.BukkitRunnable;

public class Timer {

    private final long time;
    @Getter
    private long timeLeft = 0;
    private final Runnable onFinish;
    private BukkitRunnable runnable;

    public Timer(long time, Runnable onFinish) {
        this.time = time;
        this.onFinish = onFinish;
        assignRunnable();
        Bukkit.getOnlinePlayers().forEach(player -> {
            player.setExp(0);
            player.setLevel(0);
            player.setTotalExperience(0);
        });
    }

    public void start() {
        runnable.runTaskTimerAsynchronously(FireballFight.getPlugin(), 0, 1);
    }

    public void stopAndReset() {
        try {
            runnable.cancel();
        } catch (Exception ignored) {
        }
        timeLeft = 0;
        Bukkit.getOnlinePlayers().forEach(player -> {
            player.setLevel(0);
            player.setExp(0);
            player.setTotalExperience(0);
        });
        assignRunnable();
    }

    public long getTimeSeconds() {
        return (int) Math.ceil(timeLeft / 20.0);
    }

    private void assignRunnable() {
        runnable = new BukkitRunnable() {
            private long t = time;

            @Override
            public void run() {
                t--;
                timeLeft = t;
                Bukkit.getOnlinePlayers().forEach(player -> {
                    int level = (int) Math.ceil(timeLeft / 20.0);
                    player.setLevel(level);
                    float f = timeLeft * 1.0f / time;
                    player.setExp(f);
                    if (timeLeft % 20 != 0) {
                        return;
                    }
                    player.playSound(
                            player.getLocation(),
                            Sound.ENTITY_EXPERIENCE_ORB_PICKUP,
                            100, f / 2 + 0.5f
                    );
                });
                if (t <= 0) {
                    onFinish.run();
                    cancel();
                }
            }
        };
    }
}