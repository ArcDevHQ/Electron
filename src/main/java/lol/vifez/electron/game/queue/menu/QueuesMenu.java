package lol.vifez.electron.game.queue.menu;

import lol.vifez.electron.Practice;
import lol.vifez.electron.game.queue.menu.ranked.RankedMenu;
import lol.vifez.electron.game.queue.menu.unranked.UnrankedMenu;
import lol.vifez.electron.util.ItemBuilder;
import lol.vifez.electron.util.menu.Menu;
import lol.vifez.electron.util.menu.button.Button;
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
public class QueuesMenu extends Menu {

    private final Practice instance;

    public QueuesMenu(Practice instance) {
        this.instance = instance;
    }

    @Override
    public String getTitle(Player player) {
        return "&7Select a queue type...";
    }

    @Override
    public int getSize() {
        return 27;
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();


        buttons.put(12, new EasyButton(
                new ItemBuilder(Material.IRON_SWORD)
                        .name("&b&lUnranked Queue")
                        .lore("&7Click to choose an unranked kit")
                        .build(),
                true,
                false,
                () -> new UnrankedMenu(instance).openMenu(player)
        ));

        buttons.put(14, new EasyButton(
                new ItemBuilder(Material.DIAMOND_SWORD)
                        .name("&c&lRanked Queue")
                        .lore("&7Click to choose a ranked kit")
                        .build(),
                true,
                false,
                () -> new RankedMenu(instance).openMenu(player)
        ));

        for (int i = 0; i < getSize(); i++) {
            if (!buttons.containsKey(i)) {
                buttons.put(i, new EasyButton(
                        new ItemBuilder(Material.STAINED_GLASS_PANE)
                                .durability((short) 15)
                                .name("&7")
                                .build(),
                        true,
                        false,
                        () -> {}
                ));
            }
        }

        return buttons;
    }
}