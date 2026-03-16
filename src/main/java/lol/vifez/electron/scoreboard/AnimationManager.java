package lol.vifez.electron.scoreboard;

import lol.vifez.electron.Practice;
import lol.vifez.electron.config.BoardConfig;
import lombok.Getter;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/* 
 * Electron © Vifez
 * Developed by Vifez
 * Copyright (c) 2025 Vifez. All rights reserved.
*/

public class AnimationManager {

    @Getter
    private final List<String> frames;
    private final int interval;
    private final AtomicInteger currentIndex = new AtomicInteger(0);

    public AnimationManager() {
        BoardConfig config = Practice.getInstance().getBoardConfig();
        frames = config.getStringList("ANIMATION.LINES");
        interval = config.getInt("ANIMATION.INTERVAL");

        startAnimationTask();
    }

    private void startAnimationTask() {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (frames.isEmpty()) return;
                currentIndex.set((currentIndex.get() + 1) % frames.size());
            }
        }.runTaskTimer(Practice.getInstance(), interval, interval);
    }

    public String getCurrentFrame() {
        if (frames.isEmpty()) return "";
        return frames.get(currentIndex.get());
    }
}