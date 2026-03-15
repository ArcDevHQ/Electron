package lol.vifez.electron.hotbar;

import lol.vifez.electron.Practice;
import lol.vifez.electron.game.kit.menu.editor.KitSelectMenu;
import lol.vifez.electron.game.queue.RankedAccess;
import lol.vifez.electron.leaderboard.menu.LeaderboardMenu;
import lol.vifez.electron.navigator.menu.NavigatorMenu;
import lol.vifez.electron.profile.Profile;
import lol.vifez.electron.game.queue.Queue;
import lol.vifez.electron.game.queue.menu.QueuesMenu;
import lol.vifez.electron.game.queue.menu.RankedMenu;
import lol.vifez.electron.game.queue.menu.UnrankedMenu;
import lol.vifez.electron.settings.menu.OptionsMenu;
import lol.vifez.electron.util.CC;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.EnumMap;
import java.util.Map;

/*
 * Electron © Vifez
 * Developed by Vifez
 * Copyright (c) 2025 Vifez. All rights reserved.
 */

public final class HotbarListener implements Listener {

    private final Practice instance;
    private final Map<Hotbar, HotbarAction> actions = new EnumMap<>(Hotbar.class);

    public HotbarListener() {
        this.instance = Practice.getInstance();
        registerActions();
        instance.getServer().getPluginManager().registerEvents(this, instance);
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (!isRightClick(event.getAction())) {
            return;
        }

        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInHand();
        if (item == null) {
            return;
        }

        for (Hotbar hotbar : Hotbar.values()) {
            ItemStack hotbarItem = instance.getHotbarManager().getItem(hotbar);
            if (hotbarItem == null || !hotbarItem.isSimilar(item)) {
                continue;
            }

            HotbarAction action = actions.get(hotbar);
            if (action != null) {
                event.setCancelled(true);
                action.execute(player);
            }
            return;
        }
    }

    private void registerActions() {
        actions.put(Hotbar.UNRANKED, player ->
                new UnrankedMenu(instance).openMenu(player));

        actions.put(Hotbar.RANKED, player -> {
            Profile profile = instance.getProfileManager().getProfile(player.getUniqueId());
            if (profile == null) {
                CC.sendMessage(player, "&cProfile not found!");
                return;
            }

            if (!RankedAccess.canAccess(player, profile, true)) {
                return;
            }

            new RankedMenu(instance).openMenu(player);
        });

        actions.put(Hotbar.LEADERBOARDS, player ->
                new LeaderboardMenu(instance).openMenu(player));

        actions.put(Hotbar.QUEUES, player ->
                new QueuesMenu(instance).openMenu(player));

        actions.put(Hotbar.KIT_EDITOR, player ->
                new KitSelectMenu(instance).openMenu(player));

        actions.put(Hotbar.NAVIGATOR, player -> {
            Profile profile = instance.getProfileManager().getProfile(player.getUniqueId());
            if (profile == null) {
                CC.sendMessage(player, "&cProfile not found!");
                return;
            }

            new NavigatorMenu(instance).openMenu(player);
        });

        actions.put(Hotbar.SETTINGS, player -> {
            Profile profile = instance.getProfileManager().getProfile(player.getUniqueId());
            if (profile == null) {
                CC.sendMessage(player, "&cProfile not found!");
                return;
            }

            new OptionsMenu().openMenu(player);
        });

        actions.put(Hotbar.LEAVE_QUEUE, player -> {
            Queue queue = instance.getQueueManager().getQueue(player.getUniqueId());
            if (queue != null) {
                queue.remove(player);
            }

            player.getInventory().setContents(instance.getHotbarManager().getSpawnItems());
            player.getInventory().setArmorContents(null);
            CC.sendMessage(player, "&cYou left the queue!");
        });
    }

    private boolean isRightClick(Action action) {
        return action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK;
    }

    @FunctionalInterface
    private interface HotbarAction {
        void execute(Player player);
    }
}