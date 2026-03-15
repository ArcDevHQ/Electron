package lol.vifez.electron.game.divisions.menus;

import lol.vifez.electron.util.DivisionUtil;
import lol.vifez.electron.game.divisions.Divisions;
import lol.vifez.electron.util.ItemBuilder;
import lol.vifez.electron.util.menu.Menu;
import lol.vifez.electron.util.menu.button.Button;
import lol.vifez.electron.util.menu.button.impl.EasyButton;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
 * Electron © Vifez
 * Developed by Vifez
 * Copyright (c) 2025 Vifez. All rights reserved.
 */

public class DivisionsMenu extends Menu {

    private static final int ITEMS_PER_PAGE = 15;
    private final int page;

    public DivisionsMenu(int page) {
        this.page = page;
    }

    @Override
    public String getTitle(Player player) {
        return "&7Divisions &7[&b" + page + "&7]";
    }

    @Override
    public int getSize() {
        return 45;
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();
        List<Divisions> divisions = DivisionUtil.getPage(page, ITEMS_PER_PAGE);

        int row = 1;
        int col = 0;
        int startSlot = 11;

        for (Divisions division : divisions) {
            int slot = startSlot + col + ((row - 1) * 9);

            buttons.put(slot, new EasyButton(
                    new ItemBuilder(division.getMaterial())
                            .name(division.getPrettyName())
                            .lore("&7Minimum Elo: &b" + division.getMinimumElo())
                            .build(),
                    true,
                    false,
                    () -> {}
            ));

            col++;
            if (col == 5) {
                col = 0;
                row++;
            }
        }

        if (page > 1) {
            buttons.put(36, nav("&cPrevious Page", page - 1, player));
        }

        if (DivisionUtil.hasNextPage(page, ITEMS_PER_PAGE)) {
            buttons.put(44, nav("&aNext Page", page + 1, player));
        }

        return buttons;
    }

    private Button nav(String name, int targetPage, Player player) {
        return new EasyButton(
                new ItemBuilder(Material.ARROW)
                        .name(name)
                        .lore("&7Click to view page " + targetPage)
                        .build(),
                true,
                false,
                () -> new DivisionsMenu(targetPage).openMenu(player)
        );
    }
}