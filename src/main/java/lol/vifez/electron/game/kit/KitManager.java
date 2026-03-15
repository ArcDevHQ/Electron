package lol.vifez.electron.game.kit;

import lol.vifez.electron.Practice;
import lol.vifez.electron.game.kit.enums.KitType;
import lol.vifez.electron.util.SerializationUtil;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/*
 * Electron © Vifez
 * Developed by Vifez
 * Copyright (c) 2025 Vifez. All rights reserved.
 */

@Getter
public class KitManager {

    private static final ItemStack[] EMPTY_ITEMS = new ItemStack[0];
    private static final ItemStack DEFAULT_ICON = new ItemStack(Material.BOOK);

    private final Map<String, Kit> kits = new ConcurrentHashMap<>();

    public KitManager() {
        loadKits();
    }

    public void loadKits() {
        kits.clear();

        ConfigurationSection section = Practice.getInstance()
                .getKitsFile()
                .getConfiguration()
                .getConfigurationSection("kits");

        if (section == null) {
            return;
        }

        for (String key : section.getKeys(false)) {
            ConfigurationSection kitSection = section.getConfigurationSection(key);
            if (kitSection == null) {
                continue;
            }

            Kit kit = deserializeKit(key, kitSection);
            kits.put(key.toLowerCase(), kit);
        }
    }

    public Kit getKit(String name) {
        if (name == null) {
            return null;
        }

        return kits.get(name.toLowerCase());
    }

    public boolean hasKit(String name) {
        return getKit(name) != null;
    }

    public Collection<Kit> getAllKits() {
        return kits.values();
    }

    public void save(Kit kit) {
        if (kit == null) {
            return;
        }

        kits.put(kit.getName().toLowerCase(), kit);

        ConfigurationSection root = getOrCreateKitsRoot();
        root.set(kit.getName(), null);

        ConfigurationSection section = root.createSection(kit.getName());
        serializeKit(kit, section);

        Practice.getInstance().getKitsFile().save();
    }

    public void saveAll() {
        ConfigurationSection config = Practice.getInstance()
                .getKitsFile()
                .getConfiguration();

        config.set("kits", null);
        ConfigurationSection root = config.createSection("kits");

        for (Kit kit : kits.values()) {
            ConfigurationSection section = root.createSection(kit.getName());
            serializeKit(kit, section);
        }

        Practice.getInstance().getKitsFile().save();
    }

    public void delete(Kit kit) {
        if (kit == null) {
            return;
        }

        kits.remove(kit.getName().toLowerCase());

        Practice.getInstance()
                .getKitsFile()
                .getConfiguration()
                .set("kits." + kit.getName(), null);

        Practice.getInstance().getKitsFile().save();
    }

    public void delete(String name) {
        Kit kit = getKit(name);
        if (kit != null) {
            delete(kit);
        }
    }

    public void close() {
        saveAll();
    }

    private void serializeKit(Kit kit, ConfigurationSection section) {
        section.set("description", kit.getDescription());
        section.set("contents", SerializationUtil.serializeItemStackArray(kit.getContents()));
        section.set("armorContents", SerializationUtil.serializeItemStackArray(kit.getArmorContents()));
        section.set("icon", SerializationUtil.serializeItemStack(kit.getIcon()));
        section.set("color", kit.getColor().name());
        section.set("kitType", kit.getKitType().name());
        section.set("weight", kit.getWeight());
        section.set("ranked", kit.isRanked());
    }

    private Kit deserializeKit(String name, ConfigurationSection section) {
        Kit kit = new Kit(name);

        kit.setDescription(section.getStringList("description"));
        kit.setContents(deserializeItemArray(section, "contents"));
        kit.setArmorContents(deserializeItemArray(section, "armorContents"));
        kit.setIcon(deserializeIcon(section));
        kit.setColor(parseEnum(section, "color", ChatColor.class, ChatColor.AQUA));
        kit.setKitType(parseEnum(section, "kitType", KitType.class, KitType.REGULAR));
        kit.setWeight(section.getInt("weight", 0));
        kit.setRanked(section.getBoolean("ranked", false));

        return kit;
    }

    private ItemStack[] deserializeItemArray(ConfigurationSection section, String key) {
        String data = section.getString(key);
        if (data == null || data.isEmpty()) {
            return EMPTY_ITEMS;
        }

        try {
            return SerializationUtil.deserializeItemStackArray(data);
        } catch (Exception e) {
            logWarning("Failed to load " + key, e);
            return EMPTY_ITEMS;
        }
    }

    private ItemStack deserializeIcon(ConfigurationSection section) {
        String data = section.getString("icon");
        if (data == null || data.isEmpty()) {
            return DEFAULT_ICON.clone();
        }

        try {
            return SerializationUtil.deserializeItemStack(data);
        } catch (Exception ignored) {
            try {
                return new ItemStack(Material.valueOf(data.toUpperCase()));
            } catch (Exception e) {
                logWarning("Failed to load icon", e);
                return DEFAULT_ICON.clone();
            }
        }
    }

    private <T extends Enum<T>> T parseEnum(
            ConfigurationSection section,
            String key,
            Class<T> enumClass,
            T fallback
    ) {
        String value = section.getString(key);
        if (value == null) {
            return fallback;
        }

        try {
            return Enum.valueOf(enumClass, value.toUpperCase());
        } catch (IllegalArgumentException ignored) {
            return fallback;
        }
    }

    private ConfigurationSection getOrCreateKitsRoot() {
        ConfigurationSection root = Practice.getInstance()
                .getKitsFile()
                .getConfiguration()
                .getConfigurationSection("kits");

        if (root == null) {
            root = Practice.getInstance()
                    .getKitsFile()
                    .getConfiguration()
                    .createSection("kits");
        }

        return root;
    }

    private void logWarning(String message, Exception e) {
        Practice.getInstance()
                .getLogger()
                .warning(message + ": " + e.getMessage());
    }
}