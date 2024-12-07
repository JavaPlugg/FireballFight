package net.javaplugg.minecraft.game.fireballfight.stage.game;

import net.javaplugg.minecraft.game.fireballfight.FireballFight;
import net.javaplugg.minecraft.game.fireballfight.listener.IGameListener;
import net.javaplugg.minecraft.game.fireballfight.map.GameMap;
import net.javaplugg.minecraft.game.fireballfight.stage.IGameStage;
import net.javaplugg.minecraft.game.fireballfight.stage.game.listeners.GameListener;
import net.javaplugg.minecraft.game.fireballfight.stage.game.listeners.JoinQuitListener;
import net.javaplugg.minecraft.game.fireballfight.stage.game.player.FireballFightPlayer;
import net.javaplugg.minecraft.game.fireballfight.stage.game.player.Team;
import net.javaplugg.minecraft.game.fireballfight.util.Listeners;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import java.util.ArrayList;
import java.util.List;

public class StageGame implements IGameStage {

    private final List<IGameListener> listeners = new ArrayList<>();
    private FireballFightPlayer playerRed;
    private FireballFightPlayer playerBlue;

    private FireballFightPlayer getPlayer(Player player) {
        if (player == playerRed.getPlayer()) {
            return playerRed;
        }
        return playerBlue;
    }

    @Override
    public void begin() {
        FireballFight.getWorld().setPVP(true);
        Player[] players = Bukkit.getOnlinePlayers().toArray(new Player[]{});
        for (Player player : players) {
            player.setGameMode(GameMode.SURVIVAL);
            player.getInventory().clear();
        }
        Player red = players[0];
        Player blue = players[1];
        GameMap gameMap = FireballFight.getGame().getGameMap();
        red.teleport(gameMap.getTeamRedSpawnLocation());
        blue.teleport(gameMap.getTeamBlueSpawnLocation());
        playerRed = new FireballFightPlayer(red, Team.RED, gameMap.getTeamRedSpawnLocation());
        playerBlue = new FireballFightPlayer(blue, Team.BLUE, gameMap.getTeamBlueSpawnLocation());

        Component redName = Component.text(red.getName(), NamedTextColor.RED);
        red.displayName(redName);
        red.playerListName(redName);
        Component blueName = Component.text(blue.getName(), NamedTextColor.BLUE);
        blue.displayName(blueName);
        blue.playerListName(blueName);

        listeners.add(new JoinQuitListener());
        listeners.add(new GameListener(playerRed, playerBlue, this));
        listeners.forEach(Listeners::register);

        Bukkit.getOnlinePlayers().forEach(this::giveItems);
    }

    @Override
    public void end() {
        listeners.forEach(Listeners::unregister);
    }

    public void giveItems(Player player) {
        Inventory inventory = player.getInventory();
        Team team = getPlayer(player).getTeam();

        ItemStack helmet = new ItemStack(Material.LEATHER_HELMET);
        ItemStack chestplate = new ItemStack(Material.LEATHER_CHESTPLATE);
        ItemStack leggings = new ItemStack(Material.LEATHER_LEGGINGS);
        ItemStack boots = new ItemStack(Material.LEATHER_BOOTS);

        setArmorColor(helmet, team);
        setArmorColor(chestplate, team);
        setArmorColor(leggings, team);
        setArmorColor(boots, team);

        inventory.setItem(39, helmet);
        inventory.setItem(38, chestplate);
        inventory.setItem(37, leggings);
        inventory.setItem(36, boots);

        inventory.setItem(0, new ItemStack(Material.STONE_SWORD));
        inventory.setItem(1, new ItemStack(Material.WOODEN_PICKAXE));
        inventory.setItem(2, new ItemStack(Material.FIRE_CHARGE, 6));
        inventory.setItem(3, new ItemStack(Material.COOKED_PORKCHOP, 16));

        ItemStack itemStack;
        if (team.equals(Team.RED)) {
            itemStack = new ItemStack(Material.RED_WOOL, 64);
        } else {
            itemStack = new ItemStack(Material.BLUE_WOOL, 64);
        }
        inventory.setItem(4, itemStack);
        inventory.setItem(5, itemStack);
    }

    private void setArmorColor(ItemStack item, Team team) {
        LeatherArmorMeta meta = (LeatherArmorMeta) item.getItemMeta();
        meta.setColor(team.equals(Team.RED) ? Color.RED : Color.BLUE);
        item.setItemMeta(meta);
    }
}
