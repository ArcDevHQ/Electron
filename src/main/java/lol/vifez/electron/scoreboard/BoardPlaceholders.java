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

/*
 * Electron © Vifez
 * Developed by Vifez
 * Copyright (c) 2025 Vifez. All rights reserved.
 */

public class BoardPlaceholders {

    private final AnimationManager animationManager;
    private final BoardConfig boardConfig;

    public BoardPlaceholders(AnimationManager animationManager, BoardConfig boardConfig) {
        this.animationManager = animationManager;
        this.boardConfig = boardConfig;
    }

    public String apply(String text, Player player, Profile profile, Match match, Queue queue) {
        Map<String, String> placeholders = createPlaceholders(player, profile, match, queue);

        String result = text;
        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            result = result.replace(entry.getKey(), entry.getValue());
        }

        return result;
    }

    private Map<String, String> createPlaceholders(Player player, Profile profile, Match match, Queue queue) {
        Practice plugin = Practice.getInstance();

        Map<String, String> placeholders = new LinkedHashMap<>();

        String globalElo = String.valueOf(EloUtil.getGlobalElo(profile));
        String elo = getRelevantElo(profile, match, queue, globalElo);
        String division = profile.getDivision().getPrettyName();
        String footer = getOrDefault(boardConfig.getString("SCOREBOARD.FOOTER"), "");
        String animation = animationManager.getCurrentFrame();

        String online = String.valueOf(Bukkit.getOnlinePlayers().size());
        String inQueue = String.valueOf(plugin.getQueueManager().getAllQueueSize());
        String playing = String.valueOf(plugin.getMatchManager().getTotalPlayersInMatches());
        String inFight = playing;

        String ping = String.valueOf(profile.getPing());
        String username = player.getName();

        Profile opponent = match != null ? match.getOpponent(profile) : null;
        int yourHits = match != null ? match.getHitsMap().getOrDefault(profile.getUuid(), 0) : 0;
        int theirHits = match != null && opponent != null
                ? match.getHitsMap().getOrDefault(opponent.getUuid(), 0)
                : 0;

        String opponentName = opponent != null ? opponent.getName() : "None";
        String opponentPing = opponent != null ? String.valueOf(opponent.getPing()) : "0";
        String duration = match != null ? match.getDuration() : "0";
        String startingCountdown = match != null ? String.valueOf(match.getCurrentCountdown()) : "0";
        String winner = match != null && match.getWinner() != null ? match.getWinner().getName() : "None";
        String loser = getMatchLoser(match);

        String kit = getKitDisplay(match, queue);
        String time = getTimeDisplay(profile, match, queue);

        placeholders.put("<elo>", elo);
        placeholders.put("<global-elo>", globalElo);
        placeholders.put("<division>", division);
        placeholders.put("<footer>", footer);
        placeholders.put("%animation%", animation);

        placeholders.put("<online>", online);
        placeholders.put("<in-queue>", inQueue);
        placeholders.put("<playing>", playing);
        placeholders.put("<in-fight>", inFight);

        placeholders.put("<ping>", ping);
        placeholders.put("<username>", username);

        placeholders.put("<opponent>", opponentName);
        placeholders.put("<opponent-ping>", opponentPing);
        placeholders.put("<duration>", duration);
        placeholders.put("<difference>", formatHits(yourHits));
        placeholders.put("<their-hits>", String.valueOf(theirHits));
        placeholders.put("<your-hits>", String.valueOf(yourHits));
        placeholders.put("<starting-c>", startingCountdown);
        placeholders.put("<winner>", winner);
        placeholders.put("<loser>", loser);

        placeholders.put("<kit>", kit);
        placeholders.put("<time>", time);

        return placeholders;
    }

    private String getRelevantElo(Profile profile, Match match, Queue queue, String globalElo) {
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

    private String formatHits(int hits) {
        if (hits < 0) {
            return "&c" + hits;
        }

        if (hits == 0) {
            return "&e" + hits;
        }

        return "&a" + hits;
    }

    private String getMatchLoser(Match match) {
        if (match == null || match.getWinner() == null) {
            return "None";
        }

        Profile loser = match.getOpponent(match.getWinner());
        return loser != null ? loser.getName() : "None";
    }

    private String getOrDefault(String value, String defaultValue) {
        return value == null ? defaultValue : value;
    }
}