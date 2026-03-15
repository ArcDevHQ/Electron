package lol.vifez.electron.hotbar;

import lol.vifez.electron.Practice;
import lol.vifez.electron.util.ConfigFile;
import lol.vifez.electron.util.ItemBuilder;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.EnumMap;
import java.util.Map;

/*
 * Electron © Vifez
 * Developed by Vifez
 * Copyright (c) 2025 Vifez. All rights reserved.
 */

@Getter
public class HotbarManager {

    private final Map<Hotbar, ItemStack> items = new EnumMap<>(Hotbar.class);
    private final ConfigFile hotbarFile;

    public HotbarManager() {
        this.hotbarFile = new ConfigFile(Practice.getInstance(), "hotbar.yml");
        loadAll();
    }

    public void loadAll() {
        items.clear();

        for (Hotbar hotbar : Hotbar.values()) {
            load(hotbar);
        }
    }

    private void load(Hotbar hotbar) {

        String path = hotbar.getPath();

        Material material = Material.valueOf(
                hotbarFile.getString(path + ".MATERIAL")
        );

        String name = hotbarFile.getString(path + ".NAME");
        boolean enabled = hotbarFile.getBoolean(path + ".ENABLED");

        if (!enabled) {
            items.put(hotbar, null);
            return;
        }

        ItemStack item = new ItemBuilder(material)
                .name(name)
                .build();

        items.put(hotbar, item);
    }

    public ItemStack getItem(Hotbar hotbar) {
        return items.get(hotbar);
    }

    public ItemStack[] getSpawnItems() {

        ItemStack[] items = new ItemStack[9];

        for (Hotbar hotbar : Hotbar.values()) {

            if (!hotbar.getPath().contains("LOBBY")) continue;

            ItemStack item = this.items.get(hotbar);
            if (item == null) continue;

            int slot = hotbarFile.getInt(hotbar.getPath() + ".SLOT") - 1;

            if (slot < 0 || slot >= 9) continue;

            items[slot] = item;
        }

        return items;
    }

    public ItemStack[] getQueueItems() {

        ItemStack[] items = new ItemStack[9];

        for (Hotbar hotbar : Hotbar.values()) {

            if (!hotbar.getPath().contains("IN-QUEUE")) continue;

            ItemStack item = this.items.get(hotbar);
            if (item == null) continue;

            int slot = hotbarFile.getInt(hotbar.getPath() + ".SLOT") - 1;

            if (slot < 0 || slot >= 9) continue;

            items[slot] = item;
        }

        return items;
    }

}