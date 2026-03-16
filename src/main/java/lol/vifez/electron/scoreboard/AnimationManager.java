package lol.vifez.electron.scoreboard;

import lol.vifez.electron.Practice;
import lol.vifez.electron.config.BoardConfig;
import lombok.Getter;

import java.util.List;

public class AnimationManager {

    @Getter
    private final List<String> frames;
    private final int intervalTicks;
    private final Practice plugin;
    private int index;

    public AnimationManager() {
        this.plugin = Practice.getInstance();
        BoardConfig config = plugin.getBoardConfig();
        this.frames = config.getStringList("ANIMATION.LINES");
        this.intervalTicks = Math.max(1, config.getInt("ANIMATION.INTERVAL"));

        if (!frames.isEmpty()) {
            startAnimationTask();
        }
    }

    private void startAnimationTask() {
        plugin.getServer().getScheduler().runTaskTimer(plugin, this::advanceFrame, intervalTicks, intervalTicks);
    }

    private void advanceFrame() {
        if (frames.isEmpty()) {
            return;
        }
        index = (index + 1) % frames.size();
    }

    public String getCurrentFrame() {
        if (frames.isEmpty()) {
            return "";
        }
        return frames.get(index);
    }
}
