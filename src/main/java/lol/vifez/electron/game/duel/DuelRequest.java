package lol.vifez.electron.game.duel;

import lol.vifez.electron.Practice;
import lol.vifez.electron.game.arena.Arena;
import lol.vifez.electron.game.kit.Kit;
import lol.vifez.electron.game.match.Match;
import lol.vifez.electron.profile.Profile;
import lol.vifez.electron.util.CC;
import lombok.Data;
import org.bukkit.Bukkit;

import java.util.Objects;

/**
 * Copyright (c) 2025 Vifez. All rights reserved.
 * Unauthorized use or distribution is prohibited.
 * Project: Essence
 */

@Data
public class DuelRequest {

    private final Practice instance;
    private final Profile sender;
    private final Profile target;
    private final Kit kit;
    private final long requestedAt;

    public DuelRequest(Practice instance, Profile sender, Profile target, Kit kit, long requestedAt) {
        this.instance = instance;
        this.sender = sender;
        this.target = target;
        this.kit = kit;
        this.requestedAt = requestedAt;
    }

    public boolean isExpired() {
        return System.currentTimeMillis() - requestedAt > 30_000;
    }

    public void accept() {
        Arena arena = instance.getArenaManager()
                .getAllAvailableArenas(kit)
                .stream()
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);

        CC.sendMessage(sender.getPlayer(), "&a" + target.getName() + " has accepted your duel request.");
        CC.sendMessage(target.getPlayer(), "&aYou have accepted " + sender.getName() + "'s duel request.");

        if (arena == null) {
            sender.setDuelRequest(null);
            target.setDuelRequest(null);

            CC.sendMessage(sender.getPlayer(), "&cError: There aren't any available arenas at the moment."); // ????
            CC.sendMessage(target.getPlayer(), "&cError: There aren't any available arenas at the moment."); // vifez ru retarded
            return;
        }

        Bukkit.getScheduler().runTask(instance, () -> {
            Match match = new Match(instance, sender, target, kit, arena, false);
            instance.getMatchManager().start(match);
        });
    }
}