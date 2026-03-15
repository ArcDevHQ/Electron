package lol.vifez.electron.game.kit.menu.editor;

import lol.vifez.electron.Practice;
import lol.vifez.electron.game.kit.Kit;
import lol.vifez.electron.profile.Profile;
import lol.vifez.electron.util.CC;
import lol.vifez.electron.util.ItemBuilder;
import lol.vifez.electron.hotbar.Hotbar;
import lol.vifez.electron.util.menu.Menu;
import lol.vifez.electron.util.menu.button.Button;
import lol.vifez.electron.util.menu.button.impl.DisplayButton;
import lol.vifez.electron.util.menu.button.impl.EasyButton;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

/*
 * Electron © Vifez
 * Developed by Vifez
 * Copyright (c) 2025 Vifez. All rights reserved.
 */

public class KitEditMenu extends Menu {

    private static final Practice instance = Practice.getInstance();
    private final Kit kit;

    public KitEditMenu(Kit kit) {
        this.kit = kit;
    }

    @Override
    public String getTitle(Player player) {
        return "&7Editing... [" + kit.getName() + "]";
    }

    @Override
    public void onOpen(Player player) {
        Profile profile = instance.getProfileManager().getProfile(player.getUniqueId());
        profile.setEditMode(true);
        CC.sendMessage(player, "&fStarted editing &b" + kit.getName() + " &flayout");
    }

    @Override
    public void onClose(Player player) {
        Profile profile = instance.getProfileManager().getProfile(player.getUniqueId());
        if (profile.isEditMode()) {
            profile.setEditMode(false);
            CC.sendMessage(player, "&cExited kit edit mode.");
            player.getInventory().setContents(Practice.getInstance().getHotbarManager().getSpawnItems());
        }
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> map = new HashMap<>();
        Profile profile = instance.getProfileManager().getProfile(player.getUniqueId());

        map.put(11, new EasyButton(
                new ItemBuilder(Material.INK_SACK)
                        .name("&2&lConfirm")
                        .durability((short) 10)
                        .lore("&r", "&fKit: &b" + kit.getName(), "&fClick to save your kit changes")
                        .build(),
                true, false, () -> {
            profile.getKitLoadout().put(kit.getName(), player.getInventory().getContents());
            player.closeInventory();
            CC.sendMessage(player, "&aSuccessfully saved " + kit.getColor() + kit.getName() + " &alayout");
        }));

        map.put(13, new EasyButton(
                new ItemBuilder(Material.REDSTONE_COMPARATOR)
                        .name("&e&lRestore default")
                        .lore("&r", "&fKit: &b" + kit.getName(), "&fClick to restore kit to default")
                        .build(),
                true, false, () -> {
            CC.sendMessage(player, "&eRestored kit to default");
            player.getInventory().setContents(kit.getContents());
        }));

        map.put(15, new EasyButton(
                new ItemBuilder(Material.INK_SACK)
                        .name("&c&lCancel")
                        .durability((short) 1)
                        .lore("&r", "&fKit: &b" + kit.getName(), "&fClick to cancel your changes")
                        .build(),
                true, false, () -> {
            CC.sendMessage(player, "&cCancelled layout changes");
            player.closeInventory();
        }));

        map.put(22, new EasyButton(
                new ItemBuilder(Material.ARROW)
                        .name("&cGo back")
                        .build(),
                true, false, () -> {
            new KitSelectMenu(instance).openMenu(player);
            player.getInventory().setContents(Practice.getInstance().getHotbarManager().getSpawnItems());
                }));

        for (int i = 0; i < getSize(); i++) {
            map.putIfAbsent(i, new DisplayButton(
                    new ItemBuilder(Material.STAINED_GLASS_PANE)
                            .durability((short) 15)
                            .name("&7")
                            .build()
            ));
        }

        return map;
    }
}