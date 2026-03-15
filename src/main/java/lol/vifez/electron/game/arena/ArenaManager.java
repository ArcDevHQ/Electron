package lol.vifez.electron.game.arena;

import lol.vifez.electron.Practice;
import lol.vifez.electron.game.kit.Kit;
import lol.vifez.electron.util.CC;
import lombok.var;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class ArenaManager {

    private final Map<String, Arena> arenas = new ConcurrentHashMap<>();

    public ArenaManager() {
        ConfigurationSection section = Practice.getInstance()
                .getArenasFile()
                .getConfiguration()
                .getConfigurationSection("arenas");

        if (section == null) return;

        for (String key : section.getKeys(false)) {

            String type = section.getString(key + ".type");
            String spawnA = section.getString(key + ".spawnA");
            String spawnB = section.getString(key + ".spawnB");
            String icon = section.getString(key + ".icon");
            String pos1 = section.getString(key + ".positionOne");
            String pos2 = section.getString(key + ".positionTwo");

            Arena arena = new Arena(
                    key,
                    type,
                    parseLocation(spawnA),
                    parseLocation(spawnB),
                    Material.getMaterial(icon),
                    parseLocation(pos1),
                    parseLocation(pos2)
            );

            arena.setKits(section.getStringList(key + ".kits"));
            arenas.put(key, arena);
        }
    }

    public Arena getArena(String name) {
        return arenas.values().stream()
                .filter(a -> a.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }

    public Collection<Arena> getArenas() {
        return arenas.values();
    }

    public Set<Arena> getAllAvailableArenas(Kit kit) {
        return arenas.values().stream()
                .filter(arena ->
                        arena.getKits().stream()
                                .anyMatch(k -> k.equalsIgnoreCase(kit.getName()))
                                && !arena.isBusy())
                .collect(Collectors.toSet());
    }

    public Arena getAvailableArena(Kit kit) {
        Set<Arena> available = getAllAvailableArenas(kit);
        if (available.isEmpty()) return null;

        int index = new Random().nextInt(available.size());
        return available.stream().skip(index).findFirst().orElse(null);
    }

    public void save(Arena arena) {
        arenas.put(arena.getName(), arena);
    }

    public void delete(Arena arena) {
        arenas.remove(arena.getName());

        Practice.getInstance()
                .getArenasFile()
                .getConfiguration()
                .set("arenas." + arena.getName(), null);

        Practice.getInstance().getArenasFile().save();
    }

    public void close() {

        arenas.values().forEach(arena -> {

            String path = "arenas." + arena.getName();

            Practice.getInstance().getArenasFile().getConfiguration().set(path + ".type", arena.getType());
            Practice.getInstance().getArenasFile().getConfiguration().set(path + ".spawnA", formatLocation(arena.getSpawnA()));
            Practice.getInstance().getArenasFile().getConfiguration().set(path + ".spawnB", formatLocation(arena.getSpawnB()));
            Practice.getInstance().getArenasFile().getConfiguration().set(path + ".icon", arena.getIcon().toString());
            Practice.getInstance().getArenasFile().getConfiguration().set(path + ".positionOne", formatLocation(arena.getPositionOne()));
            Practice.getInstance().getArenasFile().getConfiguration().set(path + ".positionTwo", formatLocation(arena.getPositionTwo()));
            Practice.getInstance().getArenasFile().getConfiguration().set(path + ".kits", arena.getKits());

        });

        Practice.getInstance().getArenasFile().save();
    }

    public void fixArena(Arena arena) {

        for (Block block : arena.getBlocksBuilt()) {
            if (block != null) block.setType(Material.AIR);
        }

        arena.getBlocksBuilt().clear();

        arena.getBlockBroken().forEach(Block::setType);
        arena.getBlockBroken().clear();
    }

    public void teleport(Arena arena, Player player) {

        if (arena.getSpawnA() != null) {
            player.teleport(arena.getSpawnA());
            return;
        }

        if (arena.getSpawnB() != null) {
            player.teleport(arena.getSpawnB());
            return;
        }

        player.sendMessage(CC.translate("&cNo spawn point set."));
    }

    public void cleanupKits(Arena arena) {

        Practice instance = Practice.getInstance();

        arena.getKits().removeIf(
                kit -> instance.getKitManager().getKit(kit) == null
        );
    }

    private Location parseLocation(String input) {

        if (input == null || input.isEmpty()) return null;

        String[] parts = input.split(",");

        if (parts.length < 6) return null;

        try {

            var world = Bukkit.getWorld(parts[0]);
            if (world == null) return null;

            double x = Double.parseDouble(parts[1]);
            double y = Double.parseDouble(parts[2]);
            double z = Double.parseDouble(parts[3]);
            float yaw = Float.parseFloat(parts[4]);
            float pitch = Float.parseFloat(parts[5]);

            return new Location(world, x, y, z, yaw, pitch);

        } catch (Exception ignored) {
            return null;
        }
    }

    private String formatLocation(Location location) {

        if (location == null) return null;

        return location.getWorld().getName() + "," +
                location.getX() + "," +
                location.getY() + "," +
                location.getZ() + "," +
                location.getYaw() + "," +
                location.getPitch();
    }
}