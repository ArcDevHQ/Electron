package lol.vifez.electron.game.queue.menu.unranked;

import lol.vifez.electron.Practice;
import lol.vifez.electron.game.kit.Kit;
import lol.vifez.electron.profile.Profile;
import lol.vifez.electron.util.CC;
import lol.vifez.electron.util.ItemBuilder;
import lol.vifez.electron.util.menu.button.impl.EasyButton;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;

/*
 * Electron © Vifez
 * Developed by Vifez
 * Copyright (c) 2025 Vifez. All rights reserved.
 */

public class KitButton extends EasyButton {

    public KitButton(Practice instance, Player player, Kit kit) {
        super(
                new ItemBuilder(kit.getDisplayItem())
                        .name("&b&l" + kit.getName())
                        .lore(
                                "&fPlaying&7: &b" + instance.getMatchManager().getPlayersInKitMatches(kit),
                                "&fIn Queue&7: &b" + instance.getQueueManager().getPlayersInQueue(kit, false).size(),
                                "",
                                "&aClick to queue"
                        )
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

                    instance.getQueueManager().addPlayer(player, kit, false);

                    CC.sendMessage(player, " ");
                    CC.sendMessage(player, "&b&lUnranked Queue");
                    CC.sendMessage(player, "&b• &7Kit: &b" + kit.getName());
                    CC.sendMessage(player, "&b• &7Searching for a &bplayer...");
                    CC.sendMessage(player, " ");

                    player.closeInventory();
                }
        );
    }
}