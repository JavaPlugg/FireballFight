package net.javaplugg.minecraft.game.fireballfight.util;

import net.javaplugg.minecraft.game.fireballfight.FireballFight;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;

import java.util.function.Predicate;

public class WorldUtils {

    public static void clearArea(Location l1, Location l2) {
        int x1 = Math.min(l1.getBlockX(), l2.getBlockX());
        int y1 = Math.min(l1.getBlockY(), l2.getBlockY());
        int z1 = Math.min(l1.getBlockZ(), l2.getBlockZ());
        int x2 = Math.max(l1.getBlockX(), l2.getBlockX());
        int y2 = Math.max(l1.getBlockY(), l2.getBlockY());
        int z2 = Math.max(l1.getBlockZ(), l2.getBlockZ());
        World world = l1.getWorld();
        for (int x = x1; x <= x2; x++) {
            for (int y = y1; y <= y2; y++) {
                for (int z = z1; z <= z2; z++) {
                    world.getBlockAt(x, y, z).setType(Material.AIR);
                }
            }
        }
    }

    public static void killItems() {
        killItems(material -> true);
    }

    public static void killItems(Predicate<Material> filter) {
        FireballFight.getWorld().getEntities().stream()
                .filter(entity -> entity.getType().equals(EntityType.ITEM))
                .filter(entity -> filter.test(((Item) entity).getItemStack().getType()))
                .forEach(Entity::remove);
    }
}
