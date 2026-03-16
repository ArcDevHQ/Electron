package lol.vifez.electron.profile;

import lol.vifez.electron.Practice;
import lol.vifez.electron.game.duel.DuelRequest;
import lol.vifez.electron.game.kit.Kit;
import lol.vifez.electron.game.divisions.Divisions;
import lol.vifez.electron.game.match.Match;
import lol.vifez.electron.game.queue.Queue;
import lol.vifez.electron.util.CC;
import lol.vifez.electron.util.MessageBuilder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.md_5.bungee.api.chat.ClickEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RequiredArgsConstructor
@Getter
@Setter
public class Profile {

    private final UUID uuid;

    private Player lastMessagedPlayer;
    private transient DuelRequest duelRequest;

    private String name = "";
    private String currentQueue = "";

    private Divisions division = Divisions.SILVER_I;

    private int wins = 0, losses = 0, winStreak = 0;

    private boolean editMode;
    private boolean buildMode;

    private final Map<String, ItemStack[]> kitLoadout = new HashMap<>();
    private final Map<String, Integer> kitWins = new HashMap<>();
    private final Map<String, Integer> eloMap = new HashMap<>();

    private boolean scoreboardEnabled = true;
    private boolean privateMessagingEnabled = true;
    private boolean duelRequestsEnabled = true;
    private String worldTime = "DAY";

    private Player rematchOpponent;
    private Kit rematchKit;

    public Player getPlayer() {
        Player player = Bukkit.getPlayer(uuid);
        return player != null && player.isOnline() ? player : null;
    }

    public int getPing() {
        Player player = getPlayer();
        if (player == null) {
            return -1;
        }

        try {
            Object handle = player.getClass().getMethod("getHandle").invoke(player);
            return (int) handle.getClass().getField("ping").get(handle);
        } catch (Exception ignored) {
            return -1;
        }
    }

    public int getElo(Kit kit) {
        return eloMap.getOrDefault(kit.getName(), 1000);
    }

    public void setElo(Kit kit, int elo) {
        eloMap.put(kit.getName(), elo);
    }

    public boolean inMatch() {
        return getMatch() != null;
    }

    public boolean hasUnlockedRanked() {
        return wins >= 10;
    }

    public Match getMatch() {
        return Practice.getInstance().getMatchManager().getMatch(uuid);
    }

    public Queue getQueue() {
        return Practice.getInstance().getQueueManager().getQueue(uuid);
    }

    public void checkDivision(Kit kit) {
        int elo = getElo(kit);
        Divisions newDivision = division;

        for (Divisions current : Divisions.values()) {
            if (elo >= current.getMinimumElo()) {
                newDivision = current;
            } else {
                break;
            }
        }

        if (newDivision != division) {
            division = newDivision;
            Player player = getPlayer();
            if (player != null) {
                CC.sendMessage(player, "&aYou are now in " + newDivision.getPrettyName() + " &adivision!");
            }
        }
    }

    public void sendDuelRequest(Player target, Kit kit) {
        Player sender = getPlayer();
        if (sender == null) {
            return;
        }
        Profile targetProfile = Practice.getInstance().getProfileManager().getProfile(target.getUniqueId());

        if (!duelRequestsEnabled) {
            CC.sendMessage(sender, "&cYou cannot send duel requests right now.");
            return;
        }

        if (!targetProfile.isDuelRequestsEnabled()) {
            CC.sendMessage(sender, "&c" + target.getName() + " is not accepting duel requests.");
            return;
        }

        if (duelRequest != null && !duelRequest.isExpired()) {
            long seconds = (System.currentTimeMillis() - duelRequest.getRequestedAt()) / 1000;
            CC.sendMessage(sender, "&cYou already sent a request. Wait " + seconds + "s.");
            return;
        }

        DuelRequest request = new DuelRequest(
                Practice.getInstance(),
                this,
                targetProfile,
                kit,
                System.currentTimeMillis()
        );

        this.duelRequest = request;
        targetProfile.setDuelRequest(request);

        CC.sendMessage(sender, "&b&lDuel sent" +
                "\n&f• Opponent: &b" + target.getName() +
                "\n&f• Kit: &b" + kit.getName());

        new MessageBuilder("&b&lDuel Request" +
                "\n&f• From: &b" + name +
                "\n&f• Kit: &b" + kit.getName() +
                "\n&a&l[CLICK TO ACCEPT]")
                .hover(true)
                .clickable(true)
                .hoverText("&bClick to accept")
                .clickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/duel accept " + name))
                .sendMessage(target);
    }
}
