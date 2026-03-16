package lol.vifez.electron.game.arena.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import lol.vifez.electron.game.arena.Arena;
import lol.vifez.electron.game.arena.ArenaManager;
import lol.vifez.electron.util.CC;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/* 
 * Electron © Vifez
 * Developed by Vifez
 * Copyright (c) 2025 Vifez. All rights reserved.
*/

@CommandAlias("arena")
@CommandPermission("electron.admin")
public class ArenaCommand extends BaseCommand {

    private final ArenaManager arenaManager;

    public ArenaCommand(ArenaManager arenaManager) {
        this.arenaManager = arenaManager;
    }

    @Default
    public void onArenaCommand(CommandSender sender) {
        sender.sendMessage(CC.translate(" "));
        sender.sendMessage(CC.translate("&b&lArena Commands"));
        sender.sendMessage(CC.translate(" "));
        sender.sendMessage(CC.translate("&f • &b/arena create &f<arena> &7- &fCreate an arena"));
        sender.sendMessage(CC.translate("&f • &b/arena delete &f<arena> &7- &fDelete an arena"));
        sender.sendMessage(CC.translate("&f • &b/arena setPos1 &f<arena> &7- &fSet a position for players to teleport for arena"));
        sender.sendMessage(CC.translate("&f • &b/arena setPos2 &f<arena> &7- &fSet a position for players to teleport for arena"));
        sender.sendMessage(CC.translate("&f • &b/arena setmin &f<arena> &7- &fSet the minimum corner for arena"));
        sender.sendMessage(CC.translate("&f • &b/arena setmax &f<arena> &7- &fSet the maximum corner for arena"));
        sender.sendMessage(CC.translate("&f • &b/arena addkit &f<arena> <kit> &7- &fAdd kit to an arena"));
        sender.sendMessage(CC.translate("&f • &b/arena removekit &f<arena> <kit> &7- &fRemove kit from an arena"));
        sender.sendMessage(CC.translate("&f • &b/arena kits &f<arena> &7- &fList of kits allowed in an arena"));
        sender.sendMessage(CC.translate("&f • &b/arena status &f<arena> &7- &fCheck the status of an arena"));
        sender.sendMessage(CC.translate("&f • &b/arena tp &f<arena> &7- &fTeleport to an arena"));
        sender.sendMessage(CC.translate("&f • &b/arena save - &fSave all arenas"));
        sender.sendMessage(CC.translate("&f • &b/arenas - &fManage the arenas"));
        sender.sendMessage(CC.translate(" "));
    }

    @Subcommand("create")
    @CommandPermission("electron.admin")
    public void createArena(CommandSender sender, @Single String arenaName, @Single String type) {
        if (arenaManager.getArena(arenaName) != null) {
            sender.sendMessage(CC.translate("&cArena already exists!"));
            return;
        }

        Arena arena = new Arena(arenaName);
        arena.setBusy(false);
        arenaManager.save(arena);

        sender.sendMessage(CC.translate("&7[&b" + arenaName + "&7] &aArena created"));
    }

    @Subcommand("delete")
    @CommandPermission("electron.admin")
    public void deleteArena(CommandSender sender, @Single String arenaName) {
        Arena arena = arenaManager.getArena(arenaName);
        if (arena == null) {
            sender.sendMessage(CC.translate("&cArena not found!"));
            return;
        }

        arenaManager.delete(arena);
        sender.sendMessage(CC.translate("&7[&b" + arenaName + "&7] &cArena deleted"));
    }

    @Subcommand("status")
    @CommandPermission("electron.admin")
    public void statusArena(CommandSender sender, @Single String arenaName) {
        Arena arena = arenaManager.getArena(arenaName);
        if (arena == null) {
            sender.sendMessage(CC.translate("&cArena not found!"));
            return;
        }

        String statusMessage = CC.translate("&fArena&7: &b" + arena.getName() + "\n")
                + CC.translate("&7Spawn A: " + (arena.getPositionOne() != null ? "&aSet" : "&cNot Set")) + "\n"
                + CC.translate("&7Spawn B: " + (arena.getPositionTwo() != null ? "&aSet" : "&cNot Set")) + "\n"
                + CC.translate("&7Busy: " + (arena.isBusy() ? "&cYes" : "&aNo"));
        sender.sendMessage(statusMessage);
    }

    @Subcommand("kits")
    @CommandPermission("electron.admin")
    public void kitsArena(CommandSender sender, @Single String arenaName) {
        Arena arena = arenaManager.getArena(arenaName);
        if (arena == null) {
            sender.sendMessage(CC.translate("&cArena not found!"));
            return;
        }

        String kitsMessage = CC.translate("&fKits&7: &b" + arena.getName() + "\n");
        for (String kit : arena.getKits()) {
            kitsMessage += CC.translate("&f • " + kit + "\n");
        }
        sender.sendMessage(kitsMessage);
    }

    @Subcommand("addkit")
    @CommandPermission("electron.admin")
    public void addKitArena(CommandSender sender, @Single String arenaName, @Single String kitName) {
        Arena arena = arenaManager.getArena(arenaName);
        if (arena == null) {
            sender.sendMessage(CC.translate("&cArena not found!"));
            return;
        }

        arena.getKits().add(kitName);
        sender.sendMessage(CC.translate("&7[&b" + arenaName + "&7] &b" + kitName + " &fkit was added"));
    }

    @Subcommand("removekit")
    @CommandPermission("electron.admin")
    public void removeKitArena(CommandSender sender, @Single String arenaName, @Single String kitName) {
        Arena arena = arenaManager.getArena(arenaName);
        if (arena == null) {
            sender.sendMessage(CC.translate("&cArena not found!"));
            return;
        }

        arena.getKits().remove(kitName);
        sender.sendMessage(CC.translate("&7[&b" + arenaName + "&7] &b" + kitName + " &fwas removed"));
    }

    @Subcommand("setpos1|setfirstposition")
    @CommandPermission("electron.admin")
    public void setFirstPositionArena(Player sender, @Single String arenaName) {
        Arena arena = arenaManager.getArena(arenaName);
        if (arena == null) {
            sender.sendMessage(CC.translate("&cArena not found!"));
            return;
        }

        arena.setSpawnA(sender.getLocation());
        sender.sendMessage(CC.translate("&7[&b" + arenaName + "&7] &fPosition &b1 &fset"));
    }

    @Subcommand("setpos2|setsecondposition")
    @CommandPermission("electron.admin")
    public void setSecondPositionArena(Player sender, @Single String arenaName) {
        Arena arena = arenaManager.getArena(arenaName);
        if (arena == null) {
            sender.sendMessage(CC.translate("&cArena not found!"));
            return;
        }

        arena.setSpawnB(sender.getLocation());
        sender.sendMessage(CC.translate("&7[&b" + arenaName + "&7] &fPosition &b2 &fset"));
    }

    @Subcommand("setmin")
    @CommandPermission("electron.admin")
    public void setMinArena(Player sender, @Single String arenaName) {
        Arena arena = arenaManager.getArena(arenaName);
        if (arena == null) {
            sender.sendMessage(CC.translate("&cArena not found!"));
            return;
        }

        arena.setPositionOne(sender.getLocation());
        sender.sendMessage(CC.translate("&7[&b" + arenaName + "&7] &fSet minimum point"));
    }

    @Subcommand("setmax")
    @CommandPermission("electron.admin")
    public void setMaxArena(Player sender, @Single String arenaName) {
        Arena arena = arenaManager.getArena(arenaName);
        if (arena == null) {
            sender.sendMessage(CC.translate("&cArena not found!"));
            return;
        }

        arena.setPositionTwo(sender.getLocation());
        sender.sendMessage(CC.translate("&7[&b" + arenaName + "&7] &fSet maximum point"));
    }

    @Subcommand("tp|teleport")
    @CommandPermission("electron.admin")
    public void teleportToArena(Player sender, @Single String arenaName) {
        Arena arena = arenaManager.getArena(arenaName);
        if (arena == null) {
            sender.sendMessage(CC.translate("&cArena not found!"));
            return;
        }

        arenaManager.teleport(arena, sender);
    }

    @Subcommand("save")
    @CommandPermission("electron.admin")
    public void saveArenas(CommandSender sender) {
        arenaManager.close();
        sender.sendMessage(CC.translate("&fSaved all &barenas"));
    }
}