package lol.vifez.electron.game.queue.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import lol.vifez.electron.Practice;
import lol.vifez.electron.game.kit.Kit;
import lol.vifez.electron.profile.Profile;
import lol.vifez.electron.util.CC;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/*
 * Electron © Vifez
 * Developed by Vifez
 * Copyright (c) 2025 Vifez. All rights reserved.
 */

@CommandAlias("forcequeue")
@CommandPermission("practice.admin")
public class ForceQueueCommand extends BaseCommand {

    @Default
    public void onForceQueue(Player sender, String targetName, String kitName) {

        Player target = Bukkit.getPlayerExact(targetName);
        if (target == null) {
            sender.sendMessage(CC.translate("&cPlayer is offline."));
            return;
        }

        Practice instance = Practice.getInstance();

        Kit kit = instance.getKitManager().getKit(kitName);
        if (kit == null) {
            sender.sendMessage(CC.translate("&cKit does not exist."));
            return;
        }

        Profile targetProfile = instance.getProfileManager().getProfile(target.getUniqueId());
        if (targetProfile == null) {
            sender.sendMessage(CC.translate("&cUnable to find the profile."));
            return;
        }

        if (instance.getQueueManager().isInQueue(target.getUniqueId())) {
            sender.sendMessage(CC.translate("&cThat player is already in a queue."));
            return;
        }

        instance.getQueueManager().addPlayer(target, kit, false);

        target.sendMessage(CC.translate("&fYou have been added to the &b" + kit.getName() + " &fqueue."));
        sender.sendMessage(CC.translate("&aAdded &f" + target.getName() + " &ato the &b" + kit.getName() + " &aqueue."));
    }
}