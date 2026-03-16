package lol.vifez.electron.game.queue;

import lol.vifez.electron.game.kit.Kit;
import lombok.Getter;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

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
        return joinTime == null ? "00:00" : formatMinutesSeconds(joinTime);
    }

    public String getQueueTime(UUID playerId) {
        Long joinTime = playerJoinTimes.get(playerId);
        if (joinTime == null) {
            return "0s";
        }
        long seconds = elapsedSeconds(joinTime);
        if (seconds < 60) {
            return seconds + "s";
        }
        if (seconds < 3600) {
            return (seconds / 60) + "m";
        }
        return (seconds / 3600) + "h";
    }

    private String formatMinutesSeconds(long joinTime) {
        long seconds = elapsedSeconds(joinTime);
        long minutes = seconds / 60;
        long remainder = seconds % 60;
        return String.format("%02d:%02d", minutes, remainder);
    }

    private long elapsedSeconds(long joinTime) {
        return (System.currentTimeMillis() - joinTime) / 1000;
    }
}
