package lol.vifez.electron.scoreboard;

import lol.vifez.electron.Practice;
import lol.vifez.electron.config.BoardConfig;
import lol.vifez.electron.game.kit.enums.KitType;
import lol.vifez.electron.game.match.Match;
import lol.vifez.electron.game.match.enums.MatchState;
import lol.vifez.electron.game.queue.Queue;
import lol.vifez.electron.profile.Profile;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class BoardRenderer {

    private final Practice plugin;
    private final BoardConfig boardConfig;
    private final BoardPlaceholders boardPlaceholders;

    public BoardRenderer(BoardConfig boardConfig, AnimationManager animationManager) {
        this.plugin = Practice.getInstance();
        this.boardConfig = boardConfig;
        this.boardPlaceholders = new BoardPlaceholders(animationManager, boardConfig);
    }

    public List<String> render(Player player, Profile profile) {
        Match match = profile.getMatch();
        Queue queue = plugin.getQueueManager().getQueue(profile.getUuid());

        List<String> template = getTemplate(match, queue);
        if (template.isEmpty()) {
            return Collections.emptyList();
        }

        return template.stream()
                .map(line -> boardPlaceholders.apply(line, player, profile, match, queue))
                .collect(Collectors.toList());
    }

    private List<String> getTemplate(Match match, Queue queue) {
        if (match != null) {
            return getMatchTemplate(match);
        }

        if (queue != null) {
            return boardConfig.getStringList("SCOREBOARD.IN-QUEUE.LINES");
        }

        return boardConfig.getStringList("SCOREBOARD.IN-LOBBY.LINES");
    }

    private List<String> getMatchTemplate(Match match) {
        MatchState state = match.getMatchState();
        switch (state) {
            case STARTING:
                return boardConfig.getStringList("SCOREBOARD.MATCH-STARTING.LINES");
            case STARTED:
                return match.getKit().getKitType() == KitType.BOXING
                        ? boardConfig.getStringList("SCOREBOARD.IN-BOXING.LINES")
                        : boardConfig.getStringList("SCOREBOARD.IN-GAME.LINES");
            default:
                return boardConfig.getStringList("SCOREBOARD.MATCH-ENDING.LINES");
        }
    }
}
