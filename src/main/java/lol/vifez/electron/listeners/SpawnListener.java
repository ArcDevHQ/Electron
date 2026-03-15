package lol.vifez.electron.listeners;

import lol.vifez.electron.Practice;
import lol.vifez.electron.profile.Profile;
import lol.vifez.electron.util.CC;
import lol.vifez.electron.hotbar.Hotbar;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;

/* 
 * Electron © Vifez
 * Developed by Vifez
 * Copyright (c) 2025 Vifez. All rights reserved.
*/

public class SpawnListener implements Listener {

    public SpawnListener() {
        Practice.getInstance().getServer().getPluginManager().registerEvents(this, Practice.getInstance());
    }

    @EventHandler
    public void onItemDrop(PlayerDropItemEvent event) {
        Profile profile = Practice.getInstance().getProfileManager().getProfile(event.getPlayer().getUniqueId());

        if (profile.isEditMode()) {
            event.getItemDrop().remove();
            return;
        }

        event.setCancelled(!profile.inMatch() && !profile.isBuildMode());
    }

    @EventHandler
    public void onItemPickup(PlayerPickupItemEvent event) {
        Profile profile = Practice.getInstance().getProfileManager().getProfile(event.getPlayer().getUniqueId());
        event.setCancelled(!profile.inMatch() && !profile.isBuildMode());
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Profile profile = Practice.getInstance().getProfileManager().getProfile(event.getPlayer().getUniqueId());

        if (profile.inMatch() && Practice.getInstance().getMatchManager().getMatch(profile.getUuid()).getArena().getType().contains("build")) {
            event.setCancelled(false);
        } else {
            event.setCancelled(!profile.inMatch() && !profile.isBuildMode());
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Profile profile = Practice.getInstance().getProfileManager().getProfile(event.getPlayer().getUniqueId());

        if (profile.inMatch() && Practice.getInstance().getMatchManager().getMatch(profile.getUuid()).getArena().getType().contains("build")) {
            event.setCancelled(false);
        } else {
            event.setCancelled(!profile.inMatch() && !profile.isBuildMode());
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        Profile profile = Practice.getInstance().getProfileManager().getProfile(player.getUniqueId());

        event.setCancelled(!profile.inMatch() && !profile.isEditMode() && !profile.isBuildMode());
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        Player player = (Player) event.getWhoClicked();
        Profile profile = Practice.getInstance().getProfileManager().getProfile(player.getUniqueId());

        event.setCancelled(!profile.inMatch() && !profile.isEditMode() && !profile.isBuildMode());
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            Profile profile = Practice.getInstance().getProfileManager().getProfile(player.getUniqueId());

            if (event.getCause() == EntityDamageEvent.DamageCause.VOID) {
                event.setCancelled(true);
                player.teleport(Practice.getInstance().getSpawnLocation());
                return;
            }

            event.setCancelled(!profile.inMatch());
        }
    }

    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        if (Practice.getInstance().getProfileManager().getProfile(event.getEntity().getUniqueId()) != null
                && Practice.getInstance().getProfileManager().getProfile(event.getEntity().getUniqueId()).inMatch()) return;

        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            player.setSaturation(20);
            player.setFoodLevel(20);
        }

        event.setCancelled(true);
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        Profile profile = Practice.getInstance().getProfileManager().getProfile(event.getPlayer().getUniqueId());

        if (profile.isEditMode()) {
            profile.setEditMode(false);
            event.getPlayer().getInventory().setContents(Practice.getInstance().getHotbarManager().getSpawnItems());
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();
        Profile profile = Practice.getInstance().getProfileManager().getProfile(player.getUniqueId());

        if (!event.getInventory().equals(player.getInventory())
                && event.getInventory().getTitle() != null
                && event.getInventory().getTitle().equalsIgnoreCase(CC.colorize("&bKit Editor"))) {

            if (profile.isEditMode()) {
                profile.setEditMode(false);
                event.getPlayer().getInventory().setContents(Practice.getInstance().getHotbarManager().getSpawnItems());
            }
        }
    }
}