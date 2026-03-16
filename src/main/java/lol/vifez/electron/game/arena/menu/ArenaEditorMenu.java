package lol.vifez.electron.game.arena.menu;

import lol.vifez.electron.Practice;
import lol.vifez.electron.game.arena.Arena;
import lol.vifez.electron.game.arena.ArenaManager;
import lol.vifez.electron.util.CC;
import lol.vifez.electron.util.ItemBuilder;
import lol.vifez.electron.util.menu.Menu;
import lol.vifez.electron.util.menu.button.Button;
import lol.vifez.electron.util.menu.button.impl.EasyButton;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.*;

/* 
 * Electron © Vifez
 * Developed by Vifez
 * Copyright (c) 2025 Vifez. All rights reserved.
*/

public class ArenaEditorMenu extends Menu {

    private final Arena arena;
    private final ArenaManager arenaManager;

    public ArenaEditorMenu(Arena arena, ArenaManager arenaManager) {
        this.arena = arena;
        this.arenaManager = arenaManager;
    }

    @Override
    public String getTitle(Player player) {
        return "&8Editing &b" + arena.getName();
    }

    @Override
    public int getSize() {
        return 45;
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> map = new HashMap<>();

        map.put(11, new EasyButton(
                new ItemBuilder(Material.WOOD_AXE)
                        .name("&bSet Min Corner")
                        .lore(Arrays.asList(
                                "&7Currently: " + (arena.getPositionOne() != null ? "&aSet" : "&cNot Set"),
                                " ",
                                "&fClick to set the arena's min corner."
                        ))
                        .build(),
                true, false, () -> {
            arena.setPositionOne(player.getLocation());
            player.sendMessage(CC.translate("&aMinimum corner set for &b" + arena.getName()));
            new ArenaEditorMenu(arena, arenaManager).openMenu(player);
        }
        ));

        map.put(12, new EasyButton(
                new ItemBuilder(Material.LEVER)
                        .name("&bSet Spawn B")
                        .lore(Arrays.asList(
                                "&7Currently: " + (arena.getSpawnB() != null ? "&aSet" : "&cNot Set"),
                                " ",
                                "&fClick to set this arena's Spawn B."
                        ))
                        .build(),
                true, false, () -> {
            arena.setSpawnB(player.getLocation());
            player.sendMessage(CC.translate("&aSpawn B set for &b" + arena.getName() + "&a."));
            new ArenaEditorMenu(arena, arenaManager).openMenu(player);
        }
        ));

        map.put(13, new EasyButton(
                new ItemBuilder(Material.REDSTONE_TORCH_ON)
                        .name("&bSet Spawn A")
                        .lore(Arrays.asList(
                                "&7Currently: " + (arena.getSpawnA() != null ? "&aSet" : "&cNot Set"),
                                " ",
                                "&fClick to set this arena's Spawn A."
                        ))
                        .build(),
                true, false, () -> {
            arena.setSpawnA(player.getLocation());
            player.sendMessage(CC.translate("&aSpawn A set for &b" + arena.getName() + "&a."));
            new ArenaEditorMenu(arena, arenaManager).openMenu(player);
        }
        ));

        map.put(14, new EasyButton(
                new ItemBuilder(Material.STONE_AXE)
                        .name("&bSet Max Corner")
                        .lore(Arrays.asList(
                                "&7Currently: " + (arena.getPositionTwo() != null ? "&aSet" : "&cNot Set"),
                                " ",
                                "&fClick to set the arena's max corner."
                        ))
                        .build(),
                true, false, () -> {
            arena.setPositionTwo(player.getLocation());
            player.sendMessage(CC.translate("&aMaximum corner set for &b" + arena.getName()));
            new ArenaEditorMenu(arena, arenaManager).openMenu(player);
        }
        ));

        map.put(15, new EasyButton(
                new ItemBuilder(Material.BOOK)
                        .name("&bManage Kits")
                        .lore(Arrays.asList(
                                "&7Current kits: &f" + (arena.getKits().isEmpty() ? "&cNone" : String.join(", ", arena.getKits())),
                                " ",
                                "&fClick to open kits menu."
                        ))
                        .build(),
                true, false, () -> new ArenaKitsMenu(arena, arenaManager).openMenu(player)
        ));

        map.put(22, new EasyButton(
                new ItemBuilder(Material.ENDER_PEARL)
                        .name("&bTeleport to Arena")
                        .lore("&fClick to teleport to this arena.")
                        .build(),
                true, false, () -> {
            arenaManager.teleport(arena, player);
        }
        ));

        map.put(39, new EasyButton(
                new ItemBuilder(Material.INK_SACK)
                        .durability((short)1)
                        .name("&cDelete Arena")
                        .lore(Arrays.asList(" ", "&fClick to delete this arena."))
                        .build(),
                true, false, () -> {
            arenaManager.delete(arena);
            player.closeInventory();
            player.sendMessage(CC.translate("&cDeleted arena &b" + arena.getName()));
        }
        ));

        map.put(40, new EasyButton(
                new ItemBuilder(Material.ARROW)
                        .name("&cGo back")
                        .build(),
                true, false, () -> new ArenasMenu(arenaManager).openMenu(player)
        ));

        map.put(41, new EasyButton(
                new ItemBuilder(Material.INK_SACK)
                        .durability((short)10)
                        .name("&aSave Arena")
                        .lore(Arrays.asList(" ", "&fClick to save this arena."))
                        .build(),
                true, false, () -> {
            arenaManager.save(arena);
            Practice.getInstance().getArenaManager().close();
            player.sendMessage(CC.translate("&aSaved &b" + arena.getName() + "&a to arenas.yml"));
            player.closeInventory();
        }
        ));

        int[] border = {0, 1, 2, 3, 4, 5, 6, 7, 8, 36, 37, 38, 42, 43, 44};
        for (int i : border) {
            map.put(i, new EasyButton(
                    new ItemBuilder(Material.STAINED_GLASS_PANE)
                            .durability((short) 15)
                            .name("&7")
                            .build(),
                    true, false, () -> {}
            ));
        }

        return map;
    }
}