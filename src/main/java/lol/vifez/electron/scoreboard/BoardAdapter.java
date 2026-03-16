package lol.vifez.electron.scoreboard;

import lol.vifez.electron.Practice;
import lol.vifez.electron.config.BoardConfig;
import lol.vifez.electron.profile.Profile;
import lol.vifez.electron.util.assemble.AssembleAdapter;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

/*
 * Electron © Vifez
 * Developed by Vifez
 * Copyright (c) 2025 Vifez. All rights reserved.
 */

public class BoardAdapter implements AssembleAdapter {

    private final BoardConfig boardConfig;
    private final AnimationManager animationManager;
    private final BoardRenderer boardRenderer;

    public BoardAdapter() {
        this.boardConfig = Practice.getInstance().getBoardConfig();
        this.animationManager = new AnimationManager();
        this.boardRenderer = new BoardRenderer(boardConfig, animationManager);
    }

    @Override
    public String getTitle(Player player) {
        Profile profile = Practice.getInstance().getProfileManager().getProfile(player.getUniqueId());
        if (!isEnabled(profile)) {
            return "";
        }

        String title = boardConfig.getString("SCOREBOARD.TITLE");
        return title == null ? "" : title.replace("%animation%", animationManager.getCurrentFrame());
    }

    @Override
    public List<String> getLines(Player player) {
        Profile profile = Practice.getInstance().getProfileManager().getProfile(player.getUniqueId());
        if (!isEnabled(profile)) {
            return Collections.emptyList();
        }

        return boardRenderer.render(player, profile);
    }

    private boolean isEnabled(Profile profile) {
        return profile != null
                && profile.isScoreboardEnabled()
                && boardConfig.getBoolean("SCOREBOARD.ENABLED");
    }
}