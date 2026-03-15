package lol.vifez.electron.game.queue;

import lol.vifez.electron.game.kit.Kit;
import lombok.Getter;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/*
 * Electron © Vifez
 * Developed by Vifez
 * Copyright (c) 2025 Vifez. All rights reserved.
 */

@Getter
public class Queue {

    private final Kit kit;
    private final boolean ranked;
    private final Map<UUID, Long> playerJoinTimes = new ConcurrentHashMap<>();

    public Queue(Kit kit, boolean ranked) {
        this.kit = kit;
        this.ranked = ranked;
    }

    public int getQueueSize() {
        return playerJoinTimes.size();
    }

    public boolean contains(UUID uuid) {
        return playerJoinTimes.containsKey(uuid);
    }

    public void addPlayer(UUID uuid) {
        playerJoinTimes.put(uuid, System.currentTimeMillis());
    }

    public void removePlayer(UUID uuid) {
        playerJoinTimes.remove(uuid);
    }

    public String getFormattedQueueTime(UUID playerId) {
        Long joinTime = playerJoinTimes.get(playerId);
        if (joinTime == null) {
            return "00:00";
        }

        long elapsed = System.currentTimeMillis() - joinTime;
        long seconds = elapsed / 1000;
        long minutes = seconds / 60;
        seconds %= 60;

        return String.format("%02d:%02d", minutes, seconds);
    }

    public String getQueueTime(UUID playerId) {
        Long joinTime = playerJoinTimes.get(playerId);
        if (joinTime == null) {
            return "0s";
        }

        long elapsedTime = System.currentTimeMillis() - joinTime;
        long seconds = elapsedTime / 1000;

        if (seconds < 60) {
            return seconds + "s";
        } else if (seconds < 3600) {
            return (seconds / 60) + "m";
        } else {
            return (seconds / 3600) + "h";
        }
    }
}