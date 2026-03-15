package lol.vifez.electron.game.queue.menu.ranked;

import lol.vifez.electron.Practice;
import lol.vifez.electron.game.kit.Kit;
import lol.vifez.electron.game.queue.RankedAccess;
import lol.vifez.electron.profile.Profile;
import lol.vifez.electron.util.CC;
import lol.vifez.electron.util.ItemBuilder;
import lol.vifez.electron.util.menu.button.impl.EasyButton;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/*
 * Electron © Vifez
 * Developed by Vifez
 * Copyright (c) 2025 Vifez. All rights reserved.
 */

public class RankedKitButton extends EasyButton {

    public RankedKitButton(Practice instance, Player player, Kit kit) {
        super(
                new ItemBuilder(kit.getDisplayItem())
                        .name("&c&l" + kit.getName())
                        .lore(buildLore(instance, player, kit))
                        .flag(
                                ItemFlag.HIDE_ATTRIBUTES,
                                ItemFlag.HIDE_POTION_EFFECTS,
                                ItemFlag.HIDE_ENCHANTS
                        )
                        .build(),
                true,
                false,
                () -> {
                    Profile profile = instance.getProfileManager().getProfile(player.getUniqueId());
                    if (profile == null) {
                        return;
                    }

                    if (!RankedAccess.canAccess(player, profile, true)) {
                        player.closeInventory();
                        return;
                    }

                    instance.getQueueManager().addPlayer(player, kit, true);

                    CC.sendMessage(player, " ");
                    CC.sendMessage(player, "&c&lRanked Queue");
                    CC.sendMessage(player, "&c• &7Kit: &c" + kit.getName());
                    CC.sendMessage(player, "&c• &7Searching for a &cplayer...");
                    CC.sendMessage(player, " ");

                    player.closeInventory();
                }
        );
    }

    private static List<String> buildLore(Practice instance, Player player, Kit kit) {
        Profile profile = instance.getProfileManager().getProfile(player.getUniqueId());
        List<String> lore = new ArrayList<>();

        if (profile == null) {
            lore.add("&cProfile not found.");
            return lore;
        }

        int playing = instance.getMatchManager().getPlayersInKitMatches(kit);
        int inQueue = instance.getQueueManager().getPlayersInQueue(kit, true).size();

        if (kit.getDescription() != null && !kit.getDescription().isEmpty()) {
            lore.addAll(kit.getDescription());
            lore.add("");
        }

        lore.add("&fFighting: &c" + playing);
        lore.add("&fQueueing: &c" + inQueue);
        lore.add("");
        lore.add("&fYour Elo&7: &c" + profile.getElo(kit));
        lore.add("");
        lore.add("&c&lTop 3");

        List<Profile> topPlayers = instance.getProfileManager().getProfiles().values().stream()
                .filter(p -> p.getEloMap().containsKey(kit.getName()))
                .sorted(Comparator.comparingInt(p -> -p.getElo(kit)))
                .limit(3)
                .collect(Collectors.toList());

        for (int i = 0; i < 3; i++) {
            if (i < topPlayers.size()) {
                Profile topProfile = topPlayers.get(i);
                lore.add("&c" + (i + 1) + ". &f" + topProfile.getName() +
                        " &7(&c" + topProfile.getElo(kit) + "&7)");
            } else {
                lore.add("&c" + (i + 1) + ". N/A");
            }
        }

        lore.add("");
        lore.add("&aClick to queue!");
        return lore;
    }
}