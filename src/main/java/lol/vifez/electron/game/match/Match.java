package lol.vifez.electron.game.match;

import lol.vifez.electron.Practice;
import lol.vifez.electron.game.arena.Arena;
import lol.vifez.electron.game.kit.Kit;
import lol.vifez.electron.game.match.enums.MatchState;
import lol.vifez.electron.profile.Profile;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Getter
public class Match {

    private static final int DEFAULT_COUNTDOWN_TIME = 5;
    private static final int DEFAULT_CURRENT_COUNTDOWN = -1;

    private static final float WALK_SPEED_FROZEN = 0.0F;
    private static final float WALK_SPEED_NORMAL = 0.2F;
    private static final float FLY_SPEED_FROZEN = 0.0F;
    private static final float FLY_SPEED_NORMAL = 0.2F;

    private static final int FOOD_FROZEN = 0;
    private static final int FOOD_NORMAL = 20;

    private static final int FREEZE_JUMP_AMPLIFIER = 200;

    private final UUID matchId = UUID.randomUUID();

    private final Practice instance;
    private final Profile playerOne;
    private final Profile playerTwo;
    private final Kit kit;
    private final Arena arena;
    private final boolean ranked;

    @Setter private Profile winner;
    @Setter private MatchState matchState = MatchState.STARTING;

    @Setter private int countdownTime = DEFAULT_COUNTDOWN_TIME;
    @Setter private int currentCountdown = DEFAULT_CURRENT_COUNTDOWN;
    @Setter private boolean countdownRunning;
    @Setter private BukkitTask countdownTask;

    private final Instant startTime = Instant.now();
    private final Map<UUID, Integer> hitsMap = new HashMap<>();

    public Match(Practice instance, Profile playerOne, Profile playerTwo, Kit kit, Arena arena, boolean ranked) {
        this.instance = instance;
        this.playerOne = playerOne;
        this.playerTwo = playerTwo;
        this.kit = kit;
        this.arena = arena;
        this.ranked = ranked;

        hitsMap.put(playerOne.getUuid(), 0);
        hitsMap.put(playerTwo.getUuid(), 0);
    }

    public String getDuration() {
        Duration duration = Duration.between(startTime, Instant.now());
        long seconds = duration.getSeconds();
        long hours = seconds / 3600;
        long minutes = (seconds % 3600) / 60;
        long remainingSeconds = seconds % 60;

        return String.format("%02d:%02d:%02d", hours, minutes, remainingSeconds);
    }

    public Profile getOpponent(Profile profile) {
        UUID uuid = profile.getUuid();

        if (uuid.equals(playerOne.getUuid())) {
            return playerTwo;
        }
        if (uuid.equals(playerTwo.getUuid())) {
            return playerOne;
        }

        return null;
    }

    public void denyMovement(Player player) {
        if (player == null) {
            return;
        }

        player.setHealth(player.getMaxHealth());
        player.setWalkSpeed(WALK_SPEED_FROZEN);
        player.setFlySpeed(FLY_SPEED_FROZEN);
        player.setFoodLevel(FOOD_FROZEN);
        player.setSprinting(false);
        player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, Integer.MAX_VALUE, FREEZE_JUMP_AMPLIFIER));
        player.setGameMode(GameMode.SURVIVAL);
    }

    public void allowMovement(Player player) {
        if (player == null) {
            return;
        }
        player.removePotionEffect(PotionEffectType.JUMP);
        player.setHealth(player.getMaxHealth());
        player.setWalkSpeed(WALK_SPEED_NORMAL);
        player.setFlySpeed(FLY_SPEED_NORMAL);
        player.setFoodLevel(FOOD_NORMAL);
        player.setSprinting(true);
        player.setGameMode(GameMode.SURVIVAL);
    }

    public void teleportAndSetup(Profile profile, boolean firstSpawn) {
        if (profile == null) {
            return;
        }

        Player player = profile.getPlayer();
        if (player == null) {
            return;
        }

        player.teleport(firstSpawn ? arena.getSpawnA() : arena.getSpawnB());
        player.getActivePotionEffects().forEach(effect -> player.removePotionEffect(effect.getType()));

        denyMovement(player);

        ItemStack[] contents = profile.getKitLoadout().getOrDefault(kit.getName(), kit.getContents());
        player.getInventory().setContents(contents);
        player.getInventory().setArmorContents(kit.getArmorContents());
        player.updateInventory();
    }
}
