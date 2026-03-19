package lol.vifez.electron.game.match.task;

import lol.vifez.electron.Practice;
import lol.vifez.electron.game.match.Match;
import lol.vifez.electron.game.match.MatchManager;
import lol.vifez.electron.game.match.enums.MatchState;
import lol.vifez.electron.game.match.event.MatchStartEvent;
import lol.vifez.electron.profile.Profile;
import lol.vifez.electron.util.CC;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.scheduler.BukkitRunnable;

public class MatchTask extends BukkitRunnable {

    private final Practice plugin = Practice.getInstance();
    private final MatchManager matchManager;

    public MatchTask(MatchManager matchManager) {
        this.matchManager = matchManager;
    }

    @Override
    public void run() {
        for (Match match : matchManager.getActiveMatches()) {
            if (match.getMatchState() != MatchState.STARTING) continue;
            if (match.isCountdownRunning()) continue;

            match.setCountdownRunning(true);

            final Profile p1 = match.getPlayerOne();
            final Profile p2 = match.getPlayerTwo();

            sendOpponentFound(match, p1, p2);
            sendOpponentFound(match, p2, p1);

            BukkitRunnable countdownTask = new BukkitRunnable() {
                int countdown = match.getCountdownTime();

                @Override
                public void run() {
                    if (match.getMatchState() != MatchState.STARTING) {
                        match.setCountdownRunning(false);
                        match.setCountdownTask(null);
                        cancel();
                        return;
                    }
                    PlayerWrapper first = new PlayerWrapper(p1.getPlayer());
                    PlayerWrapper second = new PlayerWrapper(p2.getPlayer());
                    if (!first.isOnline() || !second.isOnline()) {
                        match.setCountdownRunning(false);
                        match.setCountdownTask(null);
                        cancel();
                        return;
                    }

                    match.setCurrentCountdown(countdown);

                    if (countdown > 0) {
                        tickCountdown(p1, countdown);
                        tickCountdown(p2, countdown);
                        countdown--;
                        return;
                    }

                    match.allowMovement(first.player);
                    match.allowMovement(second.player);

                    match.setMatchState(MatchState.STARTED);

                    Bukkit.getPluginManager().callEvent(new MatchStartEvent(match.getPlayerOne(), match.getPlayerTwo(), match));

                    match.setCountdownRunning(false);
                    match.setCountdownTask(null);
                    cancel();
                }
            };

            match.setCountdownTask(countdownTask.runTaskTimer(plugin, 0L, 20L));
        }
    }

    private void sendOpponentFound(Match match, Profile self, Profile opponent) {
        PlayerWrapper player = new PlayerWrapper(self.getPlayer());
        PlayerWrapper opponentPlayer = new PlayerWrapper(opponent.getPlayer());
        if (!player.isOnline() || !opponentPlayer.isOnline()) {
            return;
        }
        player.player.sendMessage(" ");
        player.player.sendMessage(CC.colorize("&b&lOPPONENT FOUND"));
        player.player.sendMessage(CC.colorize("&fKit: &b" + match.getKit().getName()));
        player.player.sendMessage(CC.colorize("&fOpponent: &c" + opponentPlayer.player.getName()));
        player.player.sendMessage(" ");
        player.player.playSound(player.player.getLocation(), Sound.ORB_PICKUP, 1.0f, 1.0f);
    }

    private void tickCountdown(Profile profile, int countdown) {
        PlayerWrapper player = new PlayerWrapper(profile.getPlayer());
        if (!player.isOnline()) {
            return;
        }
        player.player.sendMessage(CC.colorize("&7Match Starting In &b" + countdown + "s"));
        player.player.playSound(player.player.getLocation(), Sound.NOTE_PIANO, 0.5f, 0.5f);
    }

    private static final class PlayerWrapper {
        private final org.bukkit.entity.Player player;

        private PlayerWrapper(org.bukkit.entity.Player player) {
            this.player = player;
        }

        private boolean isOnline() {
            return player != null && player.isOnline();
        }
    }
}
