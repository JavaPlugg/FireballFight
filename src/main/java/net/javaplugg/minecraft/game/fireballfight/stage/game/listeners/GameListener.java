package net.javaplugg.minecraft.game.fireballfight.stage.game.listeners;

import fr.mrmicky.fastboard.FastBoardBase;
import fr.mrmicky.fastboard.adventure.FastBoard;
import net.javaplugg.minecraft.game.fireballfight.FireballFight;
import net.javaplugg.minecraft.game.fireballfight.listener.IGameListener;
import net.javaplugg.minecraft.game.fireballfight.localization.Localization;
import net.javaplugg.minecraft.game.fireballfight.stage.game.StageGame;
import net.javaplugg.minecraft.game.fireballfight.stage.game.player.FireballFightPlayer;
import net.javaplugg.minecraft.game.fireballfight.stage.game.player.Team;
import net.javaplugg.minecraft.game.fireballfight.util.LanguageUtils;
import net.javaplugg.minecraft.game.fireballfight.util.RunnableUtils;
import net.javaplugg.minecraft.game.fireballfight.util.WorldUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.title.Title;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.time.Clock;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
public class GameListener implements IGameListener {

    private final LegacyComponentSerializer lcs = LegacyComponentSerializer.legacySection();
    private final FireballFightPlayer playerRed;
    private final FireballFightPlayer playerBlue;
    private final StageGame stageGame;
    private final Set<FastBoard> boards = Bukkit.getOnlinePlayers().stream().map(FastBoard::new).collect(Collectors.toSet());
    private final List<String> allowedToBreak = new ArrayList<>();
    private boolean enabled = false;

    private FireballFightPlayer getPlayer(Player player) {
        if (player == playerRed.getPlayer()) {
            return playerRed;
        }
        return playerBlue;
    }

    public GameListener(FireballFightPlayer playerRed, FireballFightPlayer playerBlue, StageGame stageGame) {
        this.playerRed = playerRed;
        this.playerBlue = playerBlue;
        this.stageGame = stageGame;

        new BukkitRunnable() {
            @Override
            public void run() {
                boards.forEach(board -> {
                    try {
                        if (!enabled) {
                            boards.forEach(FastBoardBase::delete);
                            cancel();
                            return;
                        }
                        board.updateTitle(Localization.getAnimation("server.name").getCurrentFrame());
                        String date = LocalDateTime.now(Clock.offset(Clock.systemUTC(), Duration.ofHours(3))).format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                        board.updateLines(
                                Component.text(date, NamedTextColor.GRAY),
                                Component.empty(),
                                playerRed.getPlayer().displayName()
                                        .append(Component.text(" "))
                                        .append(lcs.deserialize(playerRed.isCanRespawn() ? "§a§l✓§r" : "§c✗§r")),
                                playerBlue.getPlayer().displayName()
                                        .append(Component.text(" "))
                                        .append(lcs.deserialize(playerBlue.isCanRespawn() ? "§a§l✓§r" : "§c✗§r")),
                                Component.empty(),
                                Localization.getText("server.ip").append(Component.text("   "))
                        );
                    } catch (Exception ignored) {
                    }
                });
            }
        }.runTaskTimer(FireballFight.getPlugin(), 0, 1);
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Location location = event.getBlock().getLocation();
        String serialized = location.getBlockX() + ";" + location.getBlockY() + ";" + location.getBlockZ();
        allowedToBreak.add(serialized);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Location location = event.getBlock().getLocation();
        String serialized = location.getBlockX() + ";" + location.getBlockY() + ";" + location.getBlockZ();
        if (!FireballFight.getGame().getGameMap().isAllowed(location) && !allowedToBreak.contains(serialized)) {
            event.setCancelled(true);
            return;
        }

        Title brokenBedTitle = Title.title(
                Localization.getText("game.bed_broken"),
                Component.empty(),
                Title.Times.times(
                        Duration.ofMillis(500),
                        Duration.ofMillis(2500),
                        Duration.ofMillis(1000)
                )
        );


        // Красная кровать
        Set<Location> teamRedBedLocations = FireballFight.getGame().getGameMap().getTeamRedBedLocations();
        if (containsByBlock(teamRedBedLocations, location)) {
            if (getPlayer(event.getPlayer()).getTeam().equals(Team.RED)) {
                event.setCancelled(true);
            } else {
                teamRedBedLocations.forEach(bedBlock -> bedBlock.getBlock().setType(Material.AIR));
                playerRed.setCanRespawn(false);
                playerRed.getPlayer().showTitle(brokenBedTitle);
                Bukkit.getOnlinePlayers().forEach(player -> player.playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 1, 1));
                WorldUtils.killItems(material -> material.equals(Material.RED_BED) || material.equals(Material.BLUE_BED));
            }
            return;
        }

        // Синяя кровать
        Set<Location> teamBlueBedLocations = FireballFight.getGame().getGameMap().getTeamBlueBedLocations();
        if (containsByBlock(teamBlueBedLocations, location)) {
            if (getPlayer(event.getPlayer()).getTeam().equals(Team.BLUE)) {
                event.setCancelled(true);
            } else {
                teamBlueBedLocations.forEach(bedBlock -> bedBlock.getBlock().setType(Material.AIR));
                playerBlue.setCanRespawn(false);
                playerBlue.getPlayer().showTitle(brokenBedTitle);
                Bukkit.getOnlinePlayers().forEach(player -> player.playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 1, 1));
                WorldUtils.killItems(material -> material.equals(Material.RED_BED) || material.equals(Material.BLUE_BED));
            }
        }
    }

    private boolean containsByBlock(Set<Location> set, Location location) {
        for (Location loc : set) {
            if (loc.getBlockX() == location.getBlockX() &&
                    loc.getBlockY() == location.getBlockY() &&
                    loc.getBlockZ() == location.getBlockZ()) {
                return true;
            }
        }
        return false;
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (event.getTo().getY() > 100.0) {
            Vector vector = player.getVelocity();
            Location location = player.getLocation();
            player.setVelocity(new Vector(vector.getX(), 0, vector.getZ()));
            player.teleport(new Location(location.getWorld(), location.getX(), 100, location.getY(), location.getYaw(), location.getPitch()));
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, SoundCategory.MASTER, 1.0f, 1.0f);
            return;
        }
        if (event.getTo().getY() < 0) {
            player.damage(1_000_000);
        }
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        event.getDrops().clear();
        event.setDroppedExp(0);

        Player player = event.getEntity();

        // Задержка в 1 тик чтобы игра успела обработать смерть
        Bukkit.getScheduler().runTaskLater(FireballFight.getPlugin(), () -> {
            if (!getPlayer(player).isCanRespawn()) {
                Location location = player.getLocation();
                player.spigot().respawn();
                player.teleport(location);
                player.showTitle(Title.title(
                        Localization.getText("game.lose"),
                        Component.empty(),
                        Title.Times.times(
                                Duration.ofMillis(500),
                                Duration.ofSeconds(5),
                                Duration.ofSeconds(1)
                        )
                ));
                FireballFightPlayer fireballFightPlayer = getPlayer(player);
                FireballFightPlayer winner;
                if (fireballFightPlayer.getTeam().equals(Team.RED)) {
                    winner = playerBlue;
                } else {
                    winner = playerRed;
                }
                winner.getPlayer().showTitle(Title.title(
                        Localization.getText("game.win"),
                        Component.empty(),
                        Title.Times.times(
                                Duration.ofMillis(500),
                                Duration.ofSeconds(5),
                                Duration.ofSeconds(1)
                        )
                ));
                FireballFight.getGame().nextStage();
                return;
            }
            player.spigot().respawn();
            player.teleport(new Location(player.getWorld(), 0, 80, 0));
            player.setGameMode(GameMode.SPECTATOR);
            player.showTitle(Title.title(
                    Localization.getText("game.you_died"),
                    Localization.getText("game.respawn_after_seconds").append(Component.text(" 5 ", NamedTextColor.WHITE)).append(LanguageUtils.secondsAccusative(5).color(NamedTextColor.RED)),
                    Title.Times.times(
                            Duration.ofMillis(50),
                            Duration.ofSeconds(1),
                            Duration.ofMillis(50)
                    )
            ));

            try {
                for (int i = 4; i > 0; i--) {
                    final int finalI = i;
                    RunnableUtils.runLater(20L * (5 - i), () -> player.showTitle(Title.title(
                            Localization.getText("game.you_died"),
                            Localization.getText("game.respawn_after_seconds").append(Component.text(" " + finalI + " ", NamedTextColor.WHITE)).append(LanguageUtils.secondsAccusative(finalI).color(NamedTextColor.RED)),
                            Title.Times.times(
                                    Duration.ofMillis(50),
                                    Duration.ofSeconds(1),
                                    Duration.ofMillis(50)
                            )
                    )));
                }
                RunnableUtils.runLater(20 * 5, () -> {
                    if (getPlayer(player).getTeam().equals(Team.RED)) {
                        player.teleport(playerRed.getSpawnLocation());
                    } else {
                        player.teleport(playerBlue.getSpawnLocation());
                    }
                    stageGame.giveItems(player);
                    player.setGameMode(GameMode.SURVIVAL);
                });
            } catch (Exception ignored) {
            }
        }, 1);
    }

    @EventHandler
    public void onFireballLaunch(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Action action = event.getAction();
        ItemStack item = event.getItem();

        if (!(action == Action.RIGHT_CLICK_AIR) && !(action == Action.RIGHT_CLICK_BLOCK)) {
            return;
        }
        if (item == null || item.getType() != Material.FIRE_CHARGE) {
            return;
        }

        Fireball fireball = player.launchProjectile(Fireball.class);
        fireball.setMetadata("sender", new FixedMetadataValue(FireballFight.getPlugin(), player.getName()));
        fireball.setVelocity(player.getLocation().getDirection());

        if (item.getAmount() > 1) {
            item.setAmount(item.getAmount() - 1);
        } else {
            player.getInventory().setItemInMainHand(new ItemStack(Material.AIR));
        }

        event.setCancelled(true);
    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        if (!(event.getEntity() instanceof Fireball fireball)) {
            return;
        }
        event.blockList().clear();
        List<Entity> entities = fireball.getNearbyEntities(20, 20, 20);

        try {
            entities.forEach(entity -> {
                Location entityLocation = entity.getLocation();
                Location fireballLocation = fireball.getLocation();
                double power = Math.pow(1.0 / (entityLocation.distance(fireballLocation) + 1), 0.4);
                Vector location = entityLocation.clone().subtract(fireballLocation).toVector().normalize();
                Vector vector = new Vector(location.getX() * 2, Math.abs(location.getY()) + 1.5, location.getZ() * 2);
                vector.multiply(power);
                entity.setVelocity(entity.getVelocity().clone().setY(0).add(vector));

                if (!(entity instanceof Player player)) {
                    return;
                }
                if (!(player.getLastDamageCause() instanceof EntityDamageByEntityEvent entityDamageByEntityEvent)) {
                    return;
                }

                if (entityDamageByEntityEvent.getDamager().getMetadata("sender").getFirst().asString().equals(player.getName())) {
                    player.setHealth(Math.max(0, Math.min(19.99, player.getHealth() + entityDamageByEntityEvent.getDamage())));
                }
            });
        } catch (Exception ignored) {
        }
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (!event.getCause().equals(EntityDamageEvent.DamageCause.FALL)) {
            return;
        }
        event.setCancelled(true);
    }

    @EventHandler
    @SuppressWarnings("deprecation")
    public void onItemPickup(EntityPickupItemEvent event) {
        ItemStack itemStack = event.getItem().getItemStack();
        Material material = itemStack.getType();
        if (!material.equals(Material.RED_WOOL) && !material.equals(Material.BLUE_WOOL)) {
            return;
        }
        Entity entity = event.getEntity();
        if (!(entity instanceof Player player)) {
            return;
        }
        Team team = getPlayer(player).getTeam();

        itemStack.setType(team.equals(Team.RED) ? Material.RED_WOOL : Material.BLUE_WOOL);
    }

    @Override
    public void onRegister() {
        enabled = true;
    }

    @Override
    public void onUnregister() {
        enabled = false;
    }
}
