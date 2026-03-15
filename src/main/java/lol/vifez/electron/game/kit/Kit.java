package lol.vifez.electron.game.kit;

import lol.vifez.electron.game.kit.enums.KitType;
import lol.vifez.electron.util.ItemBuilder;
import lombok.Data;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

/*
 * Electron © Vifez
 * Developed by Vifez
 * Copyright (c) 2025 Vifez. All rights reserved.
 */

@Data
public class Kit {

    private static final ItemStack[] EMPTY_ITEMS = new ItemStack[0];
    private static final ItemStack DEFAULT_ICON = new ItemStack(Material.BOOK);

    private final String name;

    private List<String> description = new ArrayList<>();
    private ItemStack[] contents = EMPTY_ITEMS;
    private ItemStack[] armorContents = EMPTY_ITEMS;
    private ItemStack icon = DEFAULT_ICON.clone();

    private ChatColor color = ChatColor.AQUA;
    private KitType kitType = KitType.REGULAR;

    private int weight;
    private boolean ranked;

    public ItemStack getDisplayItem() {
        return new ItemBuilder(icon.clone())
                .name(color + name)
                .lore(description)
                .build();
    }
}