package lol.vifez.electron.game.queue.task;

import lol.vifez.electron.Practice;
import lol.vifez.electron.game.queue.Queue;
import lol.vifez.electron.util.ActionBar;
import lol.vifez.electron.util.CC;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Map;
import java.util.UUID;

public class ActionBarTask extends BukkitRunnable {

    private final Practice instance;

    public ActionBarTask(Practice instance) {
        this.instance = instance;
    }

    @Override
    public void run() {
        Map<UUID, Queue> playersQueue = instance.getQueueManager().getPlayersQueue();

        for (Map.Entry<UUID, Queue> entry : playersQueue.entrySet()) {
            UUID playerId = entry.getKey();
            Queue queue = entry.getValue();

            Player player = Bukkit.getPlayer(playerId);
            if (player != null && player.isOnline()) {
                String type = queue.isRanked() ? "&cRanked" : "&bUnranked";
                String kitName = queue.getKit().getName();
                String formattedTime = queue.getQueueTime(playerId);
                String message = type + " " + kitName + " &7| &fQueued for " + formattedTime;
                ActionBar.sendActionBar(player, CC.translate(message));
            }
        }
    }
}
