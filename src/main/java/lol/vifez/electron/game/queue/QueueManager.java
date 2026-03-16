package lol.vifez.electron.game.queue;

import lol.vifez.electron.Practice;
import lol.vifez.electron.game.arena.Arena;
import lol.vifez.electron.game.kit.Kit;
import lol.vifez.electron.game.match.Match;
import lol.vifez.electron.profile.Profile;
import lol.vifez.electron.util.CC;
import lol.vifez.electron.game.queue.task.ActionBarTask;
import lol.vifez.electron.game.queue.task.QueueTask;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/*
 * Electron © Vifez
 * Developed by Vifez
 * Copyright (c) 2025 Vifez. All rights reserved.
 */

@Getter
public class QueueManager {

    private final Practice instance;
    private final Map<String, Queue> queueMap;
    private final Map<UUID, Queue> playersQueue;

    public QueueManager() {
        this.instance = Practice.getInstance();
        this.queueMap = new ConcurrentHashMap<>();
        this.playersQueue = new HashMap<>();

        loadQueues();

        new QueueTask(this).runTaskTimerAsynchronously(instance, 20L, 20L);
        new ActionBarTask(instance).runTaskTimer(instance, 0L, 2L);
    }

    private void loadQueues() {
        queueMap.clear();

        for (Kit kit : instance.getKitManager().getKits().values()) {
            queueMap.put(kit.getName(), new Queue(kit, false));

            if (kit.isRanked()) {
                queueMap.put("ranked_" + kit.getName(), new Queue(kit, true));
            }
        }
    }

    public Queue getQueue(Kit kit, boolean ranked) {
        if (kit == null) {
            return null;
        }

        return queueMap.get((ranked ? "ranked_" : "") + kit.getName());
    }

    public Queue getQueue(UUID uuid) {
        return playersQueue.get(uuid);
    }

    public int getAllQueueSize() {
        int size = 0;

        for (Queue queue : queueMap.values()) {
            size += queue.getQueueSize();
        }

        return size;
    }

    public List<UUID> getPlayersInQueue(Kit kit, boolean ranked) {
        Queue queue = getQueue(kit, ranked);
        if (queue == null) {
            return Collections.emptyList();
        }

        return new ArrayList<>(queue.getPlayerJoinTimes().keySet());
    }

    public boolean isInQueue(UUID uuid) {
        return playersQueue.containsKey(uuid);
    }

    public void addPlayer(Player player, Kit kit, boolean ranked) {
        if (player == null || kit == null) {
            return;
        }

        Queue queue = getQueue(kit, ranked);
        if (queue == null) {
            return;
        }

        removePlayer(player);

        playersQueue.put(player.getUniqueId(), queue);
        queue.addPlayer(player.getUniqueId());

        Profile profile = instance.getProfileManager().getProfile(player.getUniqueId());
        if (profile != null) {
            profile.setCurrentQueue(ranked ? "ranked_" + kit.getName() : kit.getName());
        }

        player.getInventory().setArmorContents(null);
        player.getInventory().setContents(instance.getHotbarManager().getQueueItems());
    }

    public void removePlayer(Player player) {
        if (player == null) {
            return;
        }

        Queue queue = playersQueue.remove(player.getUniqueId());
        if (queue != null) {
            queue.removePlayer(player.getUniqueId());
        }

        Profile profile = instance.getProfileManager().getProfile(player.getUniqueId());
        if (profile != null) {
            profile.setCurrentQueue("");
        }

        player.getInventory().setArmorContents(null);
        player.getInventory().setContents(instance.getHotbarManager().getSpawnItems());
    }

    public void move(Queue queue) {
        if (queue == null) {
            return;
        }

        List<UUID> queuedPlayers = new ArrayList<>(queue.getPlayerJoinTimes().keySet());
        if (queuedPlayers.size() < 2) {
            return;
        }

        queuedPlayers.sort(Comparator.comparingLong(queue.getPlayerJoinTimes()::get));

        Player first = Bukkit.getPlayer(queuedPlayers.get(0));
        Player second = Bukkit.getPlayer(queuedPlayers.get(1));

        if (first == null || second == null) {
            return;
        }

        Profile profileOne = instance.getProfileManager().getProfile(first.getUniqueId());
        Profile profileTwo = instance.getProfileManager().getProfile(second.getUniqueId());

        if (profileOne == null || profileTwo == null) {
            return;
        }

        if (queue.isRanked()) {
            int difference = Math.abs(profileOne.getElo(queue.getKit()) - profileTwo.getElo(queue.getKit()));
            if (difference > 250) {
                return;
            }
        }

        Arena arena = instance.getArenaManager()
                .getAllAvailableArenas(queue.getKit())
                .stream()
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);

        if (arena == null) {
            CC.sendMessage(first, "&cNo available arenas for " + queue.getKit().getName());
            CC.sendMessage(second, "&cNo available arenas for " + queue.getKit().getName());
            first.getInventory().setContents(instance.getHotbarManager().getSpawnItems());
            second.getInventory().setContents(instance.getHotbarManager().getSpawnItems());
            return;
        }

        queue.removePlayer(first.getUniqueId());
        queue.removePlayer(second.getUniqueId());

        playersQueue.remove(first.getUniqueId());
        playersQueue.remove(second.getUniqueId());

        if (profileOne != null) {
            profileOne.setCurrentQueue("");
        }

        if (profileTwo != null) {
            profileTwo.setCurrentQueue("");
        }

        Bukkit.getScheduler().runTask(instance, () -> {
            Match match = new Match(
                    instance,
                    profileOne,
                    profileTwo,
                    queue.getKit(),
                    arena,
                    queue.isRanked()
            );
            instance.getMatchManager().start(match);
        });
    }
}