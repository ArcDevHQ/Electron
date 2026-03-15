package lol.vifez.electron.game.queue.menu.ranked;

import lol.vifez.electron.Practice;
import lol.vifez.electron.game.kit.Kit;
import lol.vifez.electron.game.queue.RankedAccess;
import lol.vifez.electron.profile.Profile;
import lol.vifez.electron.util.CC;
import lol.vifez.electron.util.ItemBuilder;
import lol.vifez.electron.util.menu.Menu;
import lol.vifez.electron.util.menu.button.Button;
import lol.vifez.electron.util.menu.button.impl.EasyButton;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

/*
 * Electron © Vifez
 * Developed by Vifez
 * Copyright (c) 2025 Vifez. All rights reserved.
 */

@RequiredArgsConstructor
public class RankedMenu extends Menu {

    private final Practice instance;

    @Override
    public String getTitle(Player player) {
        return "&7Select a ranked kit...";
    }

    @Override
    public int getSize() {
        return 45;
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        Profile profile = instance.getProfileManager().getProfile(player.getUniqueId());
        if (profile == null) {
            return buttons;
        }

        if (!RankedAccess.canAccess(player, profile, true)) {
            player.closeInventory();
            return buttons;
        }

        List<Kit> rankedKits = instance.getKitManager().getKits().values().stream()
                .filter(Kit::isRanked)
                .collect(Collectors.toList());

        int[] kitSlots = {
                10, 11, 12, 13, 14, 15, 16,
                19, 20, 21, 22, 23, 24, 25,
                28, 29, 30
        };

        for (int i = 0; i < rankedKits.size() && i < kitSlots.length; i++) {
            buttons.put(kitSlots[i], new RankedKitButton(instance, player, rankedKits.get(i)));
        }

        int[] borderSlots = {
                0, 1, 2, 3, 5, 6, 7, 8,
                9, 17, 18, 26, 27, 35,
                36, 37, 38, 39, 40, 41, 42, 43, 44
        };

        for (int slot : borderSlots) {
            buttons.put(slot, new EasyButton(
                    new ItemBuilder(Material.STAINED_GLASS_PANE)
                            .durability((short) 14)
                            .name("&7")
                            .build(),
                    true,
                    false,
                    () -> {}
            ));
        }

        buttons.put(4, new EasyButton(
                new ItemBuilder(Material.FIREWORK)
                        .name("&c&lRandom Ranked Queue")
                        .lore("&7Queue for a random ranked kit")
                        .build(),
                true,
                false,
                () -> {
                    if (rankedKits.isEmpty()) {
                        CC.sendMessage(player, "&cNo ranked kits are available.");
                        return;
                    }

                    if (!RankedAccess.canAccess(player, profile, true)) {
                        player.closeInventory();
                        return;
                    }

                    Kit randomKit = rankedKits.get(new Random().nextInt(rankedKits.size()));
                    instance.getQueueManager().addPlayer(player, randomKit, true);

                    CC.sendMessage(player, " ");
                    CC.sendMessage(player, "&c&lRanked Queue");
                    CC.sendMessage(player, "&c• &7Kit: &c" + randomKit.getName());
                    CC.sendMessage(player, "&c• &7Searching for a &cplayer...");
                    CC.sendMessage(player, " ");

                    player.closeInventory();
                }
        ));

        return buttons;
    }
}