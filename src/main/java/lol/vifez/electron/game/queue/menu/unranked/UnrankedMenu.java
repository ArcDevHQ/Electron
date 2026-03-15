package lol.vifez.electron.game.queue.menu.unranked;

import lol.vifez.electron.Practice;
import lol.vifez.electron.game.kit.Kit;
import lol.vifez.electron.util.CC;
import lol.vifez.electron.util.ItemBuilder;
import lol.vifez.electron.util.menu.Menu;
import lol.vifez.electron.util.menu.button.Button;
import lol.vifez.electron.util.menu.button.impl.EasyButton;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

/*
 * Electron © Vifez
 * Developed by Vifez
 * Copyright (c) 2025 Vifez. All rights reserved.
 */

@RequiredArgsConstructor
public class UnrankedMenu extends Menu {

    private final Practice instance;

    @Override
    public String getTitle(Player player) {
        return "&7Select a kit...";
    }

    @Override
    public int getSize() {
        return 45;
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        Kit[] kits = instance.getKitManager().getKits().values().toArray(new Kit[0]);
        int[] kitSlots = {
                10, 11, 12, 13, 14, 15, 16,
                19, 20, 21, 22, 23, 24, 25,
                28, 29, 30
        };

        for (int i = 0; i < kits.length && i < kitSlots.length; i++) {
            buttons.put(kitSlots[i], new KitButton(instance, player, kits[i]));
        }

        int[] borderSlots = {
                0, 1, 2, 3, 5, 6, 7, 8,
                9, 17, 18, 26, 27, 35,
                36, 37, 38, 39, 40, 41, 42, 43, 44
        };

        for (int slot : borderSlots) {
            buttons.put(slot, new EasyButton(
                    new ItemBuilder(Material.STAINED_GLASS_PANE)
                            .durability((short) 15)
                            .name("&7")
                            .build(),
                    true,
                    false,
                    () -> {}
            ));
        }

        buttons.put(4, new EasyButton(
                new ItemBuilder(Material.FIREWORK)
                        .name("&b&lRandom Queue")
                        .lore("&7Select a random kit")
                        .build(),
                true,
                false,
                () -> {
                    if (kits.length == 0) {
                        CC.sendMessage(player, "&cNo kits available to queue.");
                        return;
                    }

                    Kit randomKit = kits[(int) (Math.random() * kits.length)];
                    instance.getQueueManager().addPlayer(player, randomKit, false);

                    CC.sendMessage(player, " ");
                    CC.sendMessage(player, "&b&lUnranked Queue");
                    CC.sendMessage(player, "&b• &7Kit: &b" + randomKit.getName());
                    CC.sendMessage(player, "&b• &7Searching for a &bplayer...");
                    CC.sendMessage(player, " ");

                    player.closeInventory();
                }
        ));

        return buttons;
    }
}