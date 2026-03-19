package lol.vifez.electron.game.match;

import lol.vifez.electron.Practice;
import lol.vifez.electron.game.elo.EloUtil;
import lol.vifez.electron.hotbar.Hotbar;
import lol.vifez.electron.game.kit.Kit;
import lol.vifez.electron.game.match.enums.MatchState;
import lol.vifez.electron.game.match.event.MatchEndEvent;
import lol.vifez.electron.hotbar.HotbarManager;
import lol.vifez.electron.profile.Profile;
import lol.vifez.electron.util.CC;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class MatchManager {

    private final Practice plugin = Practice.getInstance();
    private final HotbarManager hotbarManager = plugin.getHotbarManager();
    private final Map<UUID, Match> matchByPlayer = new ConcurrentHashMap<>();
    private final Map<UUID, Match> activeMatches = new ConcurrentHashMap<>();

    public Match getMatch(UUID uuid) {
        return matchByPlayer.get(uuid);
    }

    public Collection<Match> getActiveMatches() {
        return activeMatches.values();
    }

    public void add(Match match) {
        activeMatches.put(match.getMatchId(), match);
        matchByPlayer.put(match.getPlayerOne().getUuid(), match);
        matchByPlayer.put(match.getPlayerTwo().getUuid(), match);
    }

    public void remove(Match match) {
        match.getArena().setBusy(false);

        activeMatches.remove(match.getMatchId());
        matchByPlayer.remove(match.getPlayerOne().getUuid());
        matchByPlayer.remove(match.getPlayerTwo().getUuid());
    }

    public int getTotalPlayersInMatches() {
        int total = 0;
        for (Match match : activeMatches.values()) {
            if (match.getMatchState() == MatchState.STARTED) total += 2;
        }
        return total;
    }

    public int getPlayersInKitMatches(Kit kit) {
        int total = 0;
        for (Match match : activeMatches.values()) {
            if (match.getMatchState() == MatchState.STARTED && match.getKit().equals(kit)) total += 2;
        }
        return total;
    }

    public void start(Match match) {
        match.setMatchState(MatchState.STARTING);
        match.getArena().setBusy(true);

        match.teleportAndSetup(match.getPlayerOne(), true);
        match.teleportAndSetup(match.getPlayerTwo(), false);
    }

    public void end(Match match) {
        if (match.getMatchState() == MatchState.ENDING || match.getMatchState() == MatchState.ENDED) {
            return;
        }

        match.setMatchState(MatchState.ENDING);

        if (match.getCountdownTask() != null) {
            match.getCountdownTask().cancel();
            match.setCountdownTask(null);
        }
        match.setCountdownRunning(false);

        Profile winner = match.getWinner();
        Profile loser = (winner == null) ? null : match.getOpponent(winner);

        if (winner != null && loser != null) {
            updateRecords(winner, loser, match.getKit());
            if (match.isRanked()) {
                updateEloForRankedMatch(winner, loser, match.getKit());
            }
        }

        Profile[] profiles = (winner == null || loser == null)
                ? new Profile[]{match.getPlayerOne(), match.getPlayerTwo()}
                : new Profile[]{winner, loser};

        for (Profile profile : profiles) {
            if (profile == null) continue;

            Player player = profile.getPlayer();
            if (player == null) continue;

            CC.sendMessage(player, winner == null
                    ? "&cMatch has ended!"
                    : "&aMatch finished!"
            );

            player.playSound(player.getLocation(), Sound.NOTE_PLING, 0.5f, 0.5f);
            resetPlayerAfterMatch(player);
        }

        Bukkit.getPluginManager().callEvent(
                new MatchEndEvent(match.getPlayerOne(), match.getPlayerTwo(), match)
        );

        match.setMatchState(MatchState.ENDED);
        remove(match);

        for (Profile profile : profiles) {
            if (profile != null) {
                plugin.getProfileManager().save(profile);
            }
        }
    }

    private void resetPlayerAfterMatch(Player player) {
        player.getInventory().setContents(hotbarManager.getSpawnItems());
        player.getInventory().setArmorContents(null);
        if (plugin.getSpawnLocation() != null) {
            player.teleport(plugin.getSpawnLocation());
        }
        player.getActivePotionEffects().forEach(effect -> player.removePotionEffect(effect.getType()));
        player.updateInventory();
    }

    private void updateRecords(Profile winner, Profile loser, Kit kit) {
        winner.setWins(winner.getWins() + 1);
        loser.setLosses(loser.getLosses() + 1);

        winner.setWinStreak(winner.getWinStreak() + 1);
        loser.setWinStreak(0);

        winner.getKitWins().merge(kit.getName(), 1, Integer::sum);
    }

    private void updateEloForRankedMatch(Profile winner, Profile loser, Kit kit) {
        int winnerElo = winner.getElo(kit);
        int loserElo = loser.getElo(kit);

        int newWinnerElo = EloUtil.getNewRating(winnerElo, loserElo, true);
        int newLoserElo = EloUtil.getNewRating(loserElo, winnerElo, false);

        winner.setElo(kit, newWinnerElo);
        loser.setElo(kit, newLoserElo);

        winner.checkDivision(kit);
        loser.checkDivision(kit);

        int wDiff = newWinnerElo - winnerElo;
        int lDiff = newLoserElo - loserElo;

        CC.sendMessage(winner.getPlayer(), "&aYou won! &7ELO: &b" + (wDiff >= 0 ? "&a+" : "&c") + wDiff);
        CC.sendMessage(loser.getPlayer(), "&cYou lost! &7ELO: " + (lDiff >= 0 ? "&a+" : "&c") + lDiff);
    }
}
