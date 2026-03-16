package lol.vifez.electron.game.match;

import lol.vifez.electron.Practice;
import lol.vifez.electron.game.match.enums.MatchState;
import lol.vifez.electron.profile.Profile;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;

public class MatchListener implements Listener {

    private final Practice plugin;

    public MatchListener(Practice plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onDamageWhileStart(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getEntity();
        Profile profile = plugin.getProfileManager().getProfile(player.getUniqueId());
        if (profile == null || !profile.inMatch()) {
            return;
        }

        MatchState state = profile.getMatch().getMatchState();
        if (state == MatchState.STARTING || state == MatchState.ENDING || state == MatchState.ENDED) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        Profile profile = plugin.getProfileManager().getProfile(player.getUniqueId());
        if (profile == null || !profile.inMatch()) {
            return;
        }

        Profile killer = profile.getMatch().getOpponent(profile);

        event.getDrops().clear();
        event.setDeathMessage(null);
        event.setNewExp(0);
        event.setNewLevel(0);
        event.setNewTotalExp(0);
        event.setKeepInventory(false);
        event.setKeepLevel(false);

        profile.getMatch().setWinner(killer);
        plugin.getMatchManager().end(profile.getMatch());
    }
}
