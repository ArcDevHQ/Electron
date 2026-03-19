package lol.vifez.electron.profile.listener;

import lol.vifez.electron.Practice;
import lol.vifez.electron.game.match.Match;
import lol.vifez.electron.profile.Profile;
import lol.vifez.electron.profile.ProfileManager;
import lol.vifez.electron.profile.menu.ProfileMenu;
import lol.vifez.electron.util.CC;
import lol.vifez.electron.hotbar.Hotbar;
import lol.vifez.electron.util.VersionChecker;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.List;
import java.util.UUID;

public class ProfileListener implements Listener {

    private final Practice instance;

    public ProfileListener(Practice instance) {
        this.instance = instance;
        instance.getServer().getPluginManager().registerEvents(this, instance);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPreLogin(AsyncPlayerPreLoginEvent event) {
        if (instance.getProfileManager() == null) {
            return;
        }

        ProfileManager manager = instance.getProfileManager();
        UUID uuid = event.getUniqueId();

        manager.getRepository()
                .getData(uuid.toString(), Profile.class)
                .thenAccept(profile -> {
                    if (profile == null) {
                        profile = new Profile(uuid);
                    }

                    profile.setName(event.getName());
                    manager.save(profile);
                }).join();
    }

    @EventHandler
    public void onPlayerRightClick(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();

        if (!(event.getRightClicked() instanceof Player)) {
            return;
        }
        Player target = (Player) event.getRightClicked();

        Profile playerProfile = instance.getProfileManager().getProfile(player.getUniqueId());
        if (playerProfile == null) {
            return;
        }

        if (playerProfile.getQueue() != null || playerProfile.inMatch()) {
            return;
        }

        Profile targetProfile = instance.getProfileManager().getProfile(target.getUniqueId());
        if (targetProfile == null) {
            return;
        }

        new ProfileMenu(targetProfile).openMenu(player);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        if (instance.getSpawnLocation() != null) {
            player.teleport(instance.getSpawnLocation());
        }

        player.getInventory().setArmorContents(null);
        player.getInventory().setContents(Practice.getInstance().getHotbarManager().getSpawnItems());

        player.sendTitle(CC.translate("&b&lPRACTICE"), CC.translate("&7Welcome to Practice (EU)"));

        if (instance.getConfig().getBoolean("SETTINGS.JOIN-MESSAGE.ENABLED", true)) {
            List<String> messages = instance.getConfig().getStringList("SETTINGS.JOIN-MESSAGE.TEXT");
            for (String message : messages) {
                player.sendMessage(CC.translate(message));
            }
            VersionChecker.notify(player);
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        Profile profile = instance.getProfileManager().getProfile(player.getUniqueId());

        if (profile != null) {
            if (profile.inMatch()) {
                Match match = profile.getMatch();
                Profile opponent = match.getOpponent(profile);

                if (opponent != null && opponent.getPlayer() != null) {
                    match.setWinner(opponent);
                    CC.sendMessage(opponent.getPlayer(), "&aYour opponent has left the match! You win by default.");
                }

                instance.getMatchManager().end(match);
            }

            instance.getProfileManager().save(profile);
        }

        String quitMessage = instance.getConfig().getString("settings.quit-message", "%player% has left the game.");
        quitMessage = quitMessage.replace("%player%", player.getName());

        event.setQuitMessage(CC.translate(quitMessage));
    }
}
