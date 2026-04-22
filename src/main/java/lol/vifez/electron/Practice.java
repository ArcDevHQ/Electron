package lol.vifez.electron;

import co.aikar.commands.BukkitCommandManager;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.LongSerializationPolicy;
import lol.vifez.electron.chat.MessageCommand;
import lol.vifez.electron.chat.ReplyCommand;
import lol.vifez.electron.commands.admin.BuildModeCommand;
import lol.vifez.electron.commands.admin.ElectronCommand;
import lol.vifez.electron.commands.admin.EloCommand;
import lol.vifez.electron.commands.admin.RenameCommand;
import lol.vifez.electron.commands.admin.SetSpawnCommand;
import lol.vifez.electron.commands.staff.MoreCommand;
import lol.vifez.electron.commands.user.RematchCommand;
import lol.vifez.electron.commands.user.SurrenderCommand;
import lol.vifez.electron.config.BoardConfig;
import lol.vifez.electron.game.arena.ArenaManager;
import lol.vifez.electron.game.arena.commands.ArenaCommand;
import lol.vifez.electron.game.arena.commands.ArenasCommand;
import lol.vifez.electron.game.divisions.commands.DivisionsCommand;
import lol.vifez.electron.game.duel.command.DuelCommand;
import lol.vifez.electron.game.kit.KitManager;
import lol.vifez.electron.game.kit.commands.KitCommands;
import lol.vifez.electron.game.match.MatchListener;
import lol.vifez.electron.game.match.MatchManager;
import lol.vifez.electron.game.match.task.MatchTask;
import lol.vifez.electron.game.queue.QueueManager;
import lol.vifez.electron.game.queue.command.ForceQueueCommand;
import lol.vifez.electron.game.queue.listener.QueueListener;
import lol.vifez.electron.hotbar.HotbarListener;
import lol.vifez.electron.hotbar.HotbarManager;
import lol.vifez.electron.leaderboard.Leaderboard;
import lol.vifez.electron.leaderboard.command.LeaderboardCommand;
import lol.vifez.electron.listeners.DeathListener;
import lol.vifez.electron.listeners.EnderpearlListener;
import lol.vifez.electron.listeners.SpawnListener;
import lol.vifez.electron.mongo.MongoAPI;
import lol.vifez.electron.navigator.command.NavigatorCommand;
import lol.vifez.electron.profile.ProfileManager;
import lol.vifez.electron.profile.repository.ProfileRepository;
import lol.vifez.electron.scoreboard.BoardAdapter;
import lol.vifez.electron.settings.command.SettingsCommand;
import lol.vifez.electron.util.AutoRespawn;
import lol.vifez.electron.util.CC;
import lol.vifez.electron.util.ConfigFile;
import lol.vifez.electron.util.SerializationUtil;
import lol.vifez.electron.util.VersionChecker;
import lol.vifez.electron.util.adapter.ItemStackArrayTypeAdapter;
import lol.vifez.electron.util.assemble.Assemble;
import lol.vifez.electron.util.menu.MenuAPI;
import lol.vifez.electron.util.placeholderapi.ElectronPlaceholders;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;

/*
 * Electron © Vifez
 * Developed by Vifez
 * Copyright (c) 2025 Vifez. All rights reserved.
 */

public final class Practice extends JavaPlugin {

    private static final String DEPRECATION_ENABLED_PATH = "DEPRECATION.WARNING.ENABLED";
    private static final int DEPRECATION_DELAY_SECONDS = 10;

    @Getter
    private static Practice instance;

    @Getter private ConfigFile arenasFile;
    @Getter private ConfigFile kitsFile;
    @Getter private BoardConfig boardConfig;

    @Getter private MongoAPI mongoAPI;
    @Getter private Gson gson;
    @Getter private ProfileManager profileManager;
    @Getter private ArenaManager arenaManager;
    @Getter private KitManager kitManager;
    @Getter private MatchManager matchManager;
    @Getter private QueueManager queueManager;
    @Getter private Leaderboard leaderboards;
    @Getter private HotbarManager hotbarManager;

    @Getter @Setter private Location spawnLocation;

    @Getter
    private boolean fullyLoaded;

    @Override
    public void onEnable() {
        instance = this;
        fullyLoaded = false;

        saveDefaultConfig();
        loadScoreboardConfig();
        initializeConfigFiles();

        if (shouldShowDeprecationWarning()) {
            displayDeprecationWarning();
            scheduleStartupAfterWarning();
            return;
        }

        completeStartup();
    }

    private void scheduleStartupAfterWarning() {
        new BukkitRunnable() {
            @Override
            public void run() {
                completeStartup();
            }
        }.runTaskLater(this, DEPRECATION_DELAY_SECONDS * 20L);
    }

    private void completeStartup() {
        if (fullyLoaded) {
            return;
        }

        initializeServices();
        initializeManagers();
        registerCommands();
        initializeListeners();
        initializeDesign();
        displayStartupInfo();
        VersionChecker.check();

        fullyLoaded = true;
    }

    private boolean shouldShowDeprecationWarning() {
        return getConfig().getBoolean(DEPRECATION_ENABLED_PATH, true);
    }

    private void displayDeprecationWarning() {
        sendMessage(" ");
        sendMessage("&c&lDEPRECATION WARNING");
        sendMessage(" ");
        sendMessage("&7This project is deprecated and will no longer receive");
        sendMessage("&7updates, bug fixes, security patches, or official support.");
        sendMessage(" ");
        sendMessage("&7You may choose to continue developing or maintaining this");
        sendMessage("&7project independently, but you do so entirely at your own risk.");
        sendMessage(" ");
        sendMessage("&e&lRISK NOTICE");
        sendMessage("&7This software may contain unresolved bugs, outdated code,");
        sendMessage("&7security risks, and compatibility issues with modern systems.");
        sendMessage("&7Running it in production without your own review is not advised.");
        sendMessage(" ");
        sendMessage("&cArc Development assumes no responsibility for any damage,");
        sendMessage("&cdata loss, instability, or abuse resulting from continued use");
        sendMessage("&cor modification of this deprecated project.");
        sendMessage(" ");
        sendMessage("&7This warning can be disabled in &cconfig.yml &7by setting");
        sendMessage("&7&cDEPRECATION.WARNING.ENABLED: false");
        sendMessage(" ");
        sendMessage("&7Electron will continue loading in &c10 seconds&7...");
        sendMessage(" ");
    }

    private void loadScoreboardConfig() {
        File file = new File(getDataFolder(), "scoreboard.yml");
        if (!file.exists()) {
            saveResource("scoreboard.yml", false);
        }

        boardConfig = new BoardConfig();
    }

    private void initializeConfigFiles() {
        arenasFile = new ConfigFile(this, "arenas.yml");
        kitsFile = new ConfigFile(this, "kits.yml");
    }

    private void initializeServices() {
        initializeGson();
        initializeMongo();
        initializeSpawnLocation();
        initializePlaceholderAPI();
    }

    private void initializeGson() {
        gson = new GsonBuilder()
                .serializeNulls()
                .setPrettyPrinting()
                .setLongSerializationPolicy(LongSerializationPolicy.STRING)
                .registerTypeAdapter(ItemStack[].class, new ItemStackArrayTypeAdapter())
                .create();
    }

    private void initializeMongo() {
        String uri = getConfig().getString("MONGO.URI");
        String dbName = getConfig().getString("MONGO.DATABASE");
        mongoAPI = new MongoAPI(uri, dbName);
    }

    private void initializeSpawnLocation() {
        spawnLocation = SerializationUtil.deserializeLocation(
                getConfig().getString("SETTINGS.SPAWN-LOCATION", "world,0,100,0,0,0")
        );
    }

    private void initializePlaceholderAPI() {
        if (getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            new ElectronPlaceholders(this).register();
        }
    }

    private void initializeManagers() {
        matchManager = new MatchManager();
        new MatchTask(matchManager).runTaskTimer(this, 0L, 20L);

        profileManager = new ProfileManager(new ProfileRepository(mongoAPI, gson));
        arenaManager = new ArenaManager();
        kitManager = new KitManager();
        queueManager = new QueueManager();
        leaderboards = new Leaderboard(profileManager);
        hotbarManager = new HotbarManager();
    }

    private void registerCommands() {
        BukkitCommandManager manager = new BukkitCommandManager(this);

        manager.registerCommand(new ArenaCommand(arenaManager));
        manager.registerCommand(new ArenasCommand());
        manager.registerCommand(new KitCommands());
        manager.registerCommand(new ElectronCommand());
        manager.registerCommand(new BuildModeCommand());
        manager.registerCommand(new EloCommand());
        manager.registerCommand(new SetSpawnCommand());
        manager.registerCommand(new LeaderboardCommand());
        manager.registerCommand(new MessageCommand());
        manager.registerCommand(new ReplyCommand());
        manager.registerCommand(new MoreCommand());
        manager.registerCommand(new DuelCommand());
        manager.registerCommand(new SettingsCommand());
        manager.registerCommand(new NavigatorCommand());
        manager.registerCommand(new DivisionsCommand());
        manager.registerCommand(new SurrenderCommand());
        manager.registerCommand(new RenameCommand());
        manager.registerCommand(new RematchCommand());
        manager.registerCommand(new ForceQueueCommand());
    }

    private void initializeListeners() {
        Bukkit.getPluginManager().registerEvents(new DeathListener(), this);
        new SpawnListener();
        new MatchListener(this);
        new QueueListener(this);
        new AutoRespawn();
        new HotbarListener();
        new EnderpearlListener();
        new MenuAPI(this);
    }

    private void initializeDesign() {
        if (getConfig().getBoolean("SCOREBOARD.ENABLED")) {
            new Assemble(this, new BoardAdapter());
        }
    }

    private void displayStartupInfo() {
        sendMessage(" ");
        sendMessage("&b&lELECTRON &7[&bv" + getDescription().getVersion() + "]");
        sendMessage("&7Lightweight practice core");
        sendMessage("&7Developed by &bVifez");
        sendMessage(" ");
        sendMessage("&fDownload this resource here");
        sendMessage("&bhttps://github.com/Vifez-Series/electron");
        sendMessage(" ");
        sendMessage("&fKits: &b" + kitManager.getKits().size());
        sendMessage("&fArenas: &b" + arenaManager.getArenas().size());
        sendMessage(" ");
    }

    private void sendMessage(String message) {
        Bukkit.getConsoleSender().sendMessage(CC.translate(message));
    }

    @Override
    public void onDisable() {
        if (profileManager != null) {
            profileManager.close();
        }

        if (arenaManager != null) {
            arenaManager.close();
        }

        if (kitManager != null) {
            kitManager.close();
        }

        if (mongoAPI != null) {
            mongoAPI.close();
        }
    }
}