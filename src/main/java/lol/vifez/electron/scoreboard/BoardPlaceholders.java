package lol.vifez.electron.scoreboard;

import lol.vifez.electron.Practice;
import lol.vifez.electron.config.BoardConfig;
import lol.vifez.electron.game.elo.EloUtil;
import lol.vifez.electron.game.kit.Kit;
import lol.vifez.electron.game.match.Match;
import lol.vifez.electron.game.queue.Queue;
import lol.vifez.electron.profile.Profile;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.LinkedHashMap;
import java.util.Map;

public class BoardPlaceholders {

    private final AnimationManager animationManager;
    private final BoardConfig boardConfig;
    private final Practice plugin;

    public BoardPlaceholders(AnimationManager animationManager, BoardConfig boardConfig) {
        this.animationManager = animationManager;
        this.boardConfig = boardConfig;
        this.plugin = Practice.getInstance();
    }

    public String apply(String text, Player player, Profile profile, Match match, Queue queue) {
        Map<String, String> placeholders = createPlaceholders(player, profile, match, queue);

        String result = text;
        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            if (result.contains(entry.getKey())) {
                result = result.replace(entry.getKey(), entry.getValue());
            }
        }
        return result;
    }

    private Map<String, String> createPlaceholders(Player player, Profile profile, Match match, Queue queue) {
        Map<String, String> placeholders = new LinkedHashMap<>();

        String globalElo = String.valueOf(EloUtil.getGlobalElo(profile));
        String elo = resolveElo(profile, match, queue, globalElo);

        placeholders.put("<elo>", elo);
        placeholders.put("<global-elo>", globalElo);
        placeholders.put("<division>", profile.getDivision().getPrettyName());
        placeholders.put("<footer>", valueOrDefault(boardConfig.getString("SCOREBOARD.FOOTER"), ""));
        placeholders.put("%animation%", animationManager.getCurrentFrame());

        placeholders.put("<online>", String.valueOf(Bukkit.getOnlinePlayers().size()));
        placeholders.put("<in-queue>", String.valueOf(plugin.getQueueManager().getAllQueueSize()));
        placeholders.put("<playing>", String.valueOf(plugin.getMatchManager().getTotalPlayersInMatches()));
        placeholders.put("<in-fight>", String.valueOf(plugin.getMatchManager().getTotalPlayersInMatches()));

        placeholders.put("<ping>", String.valueOf(profile.getPing()));
        placeholders.put("<username>", player.getName());

        MatchContext context = new MatchContext(profile, match);
        placeholders.put("<opponent>", context.opponentName);
        placeholders.put("<opponent-ping>", context.opponentPing);
        placeholders.put("<duration>", context.duration);
        placeholders.put("<difference>", context.hitDifference);
        placeholders.put("<diffrence>", context.hitDifference);
        placeholders.put("<their-hits>", context.theirHits);
        placeholders.put("<your-hits>", context.yourHits);
        placeholders.put("<starting-c>", context.startingCountdown);
        placeholders.put("<winner>", context.winner);
        placeholders.put("<loser>", context.loser);

        placeholders.put("<kit>", getKitDisplay(match, queue));
        placeholders.put("<time>", getTimeDisplay(profile, match, queue));

        return placeholders;
    }

    private String resolveElo(Profile profile, Match match, Queue queue, String globalElo) {
        if (match != null) {
            return String.valueOf(profile.getElo(match.getKit()));
        }
        if (queue != null) {
            return String.valueOf(profile.getElo(queue.getKit()));
        }
        return globalElo;
    }

    private String getKitDisplay(Match match, Queue queue) {
        if (match != null) {
            return match.getKit().getName();
        }

        if (queue != null) {
            Kit queueKit = queue.getKit();
            String typeTag = queue.isRanked() ? "&c[R]" : "&7[UR]";
            return queueKit.getName() + " " + typeTag;
        }

        return "None";
    }

    private String getTimeDisplay(Profile profile, Match match, Queue queue) {
        if (match != null) {
            return match.getDuration();
        }

        if (queue != null) {
            return queue.getQueueTime(profile.getUuid());
        }

        return "0";
    }

    private String valueOrDefault(String value, String defaultValue) {
        return value == null ? defaultValue : value;
    }

    private String formatHits(int hits) {
        if (hits > 0) {
            return "&a" + hits;
        }
        if (hits < 0) {
            return "&c" + hits;
        }
        return "&e0";
    }

    private final class MatchContext {
        private final String opponentName;
        private final String opponentPing;
        private final String duration;
        private final String hitDifference;
        private final String theirHits;
        private final String yourHits;
        private final String startingCountdown;
        private final String winner;
        private final String loser;

        private MatchContext(Profile profile, Match match) {
            if (match == null) {
                opponentName = "None";
                opponentPing = "0";
                duration = "0";
                hitDifference = "&e0";
                theirHits = "0";
                yourHits = "0";
                startingCountdown = "0";
                winner = "None";
                loser = "None";
                return;
            }

            Profile opponent = match.getOpponent(profile);
            opponentName = opponent != null ? opponent.getName() : "None";
            opponentPing = opponent != null ? String.valueOf(opponent.getPing()) : "0";
            duration = match.getDuration();
            int yourHitsCount = match.getHitsMap().getOrDefault(profile.getUuid(), 0);
            int theirHitsCount = opponent != null ? match.getHitsMap().getOrDefault(opponent.getUuid(), 0) : 0;
            hitDifference = formatHits(yourHitsCount - theirHitsCount);
            theirHits = String.valueOf(theirHitsCount);
            yourHits = String.valueOf(yourHitsCount);
            startingCountdown = String.valueOf(match.getCurrentCountdown());
            winner = match.getWinner() != null ? match.getWinner().getName() : "None";
            Profile possibleLoser = match.getWinner() != null ? match.getOpponent(match.getWinner()) : null;
            loser = possibleLoser != null ? possibleLoser.getName() : "None";
        }
    }
}
