package dev.boxadactle.boxhud.widget.vanilla;

import dev.boxadactle.boxhud.PositionModifiers;
import dev.boxadactle.boxhud.mixin.GuiInvoker;
import dev.boxadactle.boxhud.widget.VanillaWidget;
import dev.boxadactle.boxlib.math.geometry.Dimension;
import dev.boxadactle.boxlib.util.ClientUtils;
import dev.boxadactle.boxlib.util.GuiUtils;
import dev.boxadactle.boxlib.util.WorldUtils;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.numbers.NumberFormat;
import net.minecraft.network.chat.numbers.StyledFormat;
import net.minecraft.world.scores.*;
import net.minecraft.world.scores.criteria.ObjectiveCriteria;

public class ScoreboardWidget implements VanillaWidget {

    transient int width = 0;
    transient int height = 0;

    transient int plWidth = 0;
    transient int plHeight = 0;

    public int calculateSidebarScoreboardWidth(Objective objective) {
        Scoreboard scoreboard = objective.getScoreboard();
        NumberFormat numberFormat = objective.numberFormatOrDefault(StyledFormat.SIDEBAR_DEFAULT);

        // Helper record for display entry
        record DisplayEntry(Component name, Component score, int scoreWidth) {}

        DisplayEntry[] entries = scoreboard.listPlayerScores(objective).stream()
                .filter(scoreEntry -> !scoreEntry.isHidden())
                .limit(15L)
                .map(scoreEntry -> {
                    PlayerTeam team = scoreboard.getPlayersTeam(scoreEntry.owner());
                    Component name = PlayerTeam.formatNameForTeam(team, scoreEntry.ownerName());
                    Component score = scoreEntry.formatValue(numberFormat);
                    int scoreWidth = GuiUtils.getTextSize(score);
                    return new DisplayEntry(name, score, scoreWidth);
                })
                .toArray(DisplayEntry[]::new);

        Component displayName = objective.getDisplayName();
        int width = GuiUtils.getTextSize(displayName);
        int spacerWidth = GuiUtils.getTextRenderer().width(": ");

        for (DisplayEntry entry : entries) {
            int entryWidth = GuiUtils.getTextSize(entry.name()) +
                    (entry.scoreWidth() > 0 ? spacerWidth + entry.scoreWidth() : 0);
            width = Math.max(width, entryWidth);
        }

        return width + 4;
    }

    public int calculateSidebarScoreboardHeight(Objective objective) {
        Scoreboard scoreboard = objective.getScoreboard();
        // Count visible entries (max 15)
        long entryCount = scoreboard.listPlayerScores(objective).stream()
                .filter(scoreEntry -> !scoreEntry.isHidden())
                .limit(15L)
                .count();

        // Each entry is 9 pixels high, plus 9 for the title, plus 2 for top padding and 1 for bottom padding
        return (int)entryCount * 9 + 9 + 2 + 1;
    }

    @Override
    public void render(GuiGraphicsExtractor graphics, int x, int y) {
        renderPositioned(graphics, x, y, () -> ((GuiInvoker) ClientUtils.getClient().gui).invokeRenderScoreboardSidebar(graphics, getDummyTracker()));

        Scoreboard scoreboard = WorldUtils.getWorld().getScoreboard();
        Objective objective = null;
        PlayerTeam playerteam = scoreboard.getPlayersTeam(WorldUtils.getPlayer().getScoreboardName());
        if (playerteam != null) {
            DisplaySlot displayslot = DisplaySlot.teamColorToSlot(playerteam.getColor());
            if (displayslot != null) {
                objective = scoreboard.getDisplayObjective(displayslot);
            }
        }

        Objective objective1 = objective != null ? objective : scoreboard.getDisplayObjective(DisplaySlot.SIDEBAR);

        if (objective1 != null) {
            width = calculateSidebarScoreboardWidth(objective1);
            height = calculateSidebarScoreboardHeight(objective1);
        } else {
            width = 0;
            height = 0;
        }
    }

    @Override
    public void renderPlaceholder(GuiGraphicsExtractor graphics, int x, int y) {
        renderPositioned(graphics, x, y, () -> {
            Scoreboard s = new Scoreboard();
            Objective obj = s.addObjective(
                    "placeholder",
                    ObjectiveCriteria.DUMMY,
                    Component.literal("Placeholder Objective"),
                    ObjectiveCriteria.RenderType.INTEGER,
                    false,
                    null
            );
            s.getOrCreatePlayerScore(ScoreHolder.forNameOnly("Placeholder Player"), obj);
            s.getOrCreatePlayerScore(ScoreHolder.forNameOnly("Another Player"), obj);
            s.getOrCreatePlayerScore(ScoreHolder.forNameOnly("Third Player"), obj);
            s.getOrCreatePlayerScore(ScoreHolder.forNameOnly("Fourth Player"), obj);

            ((GuiInvoker) ClientUtils.getClient().gui).invokeDrawScoreboardSidebar(graphics, obj);

            plWidth = calculateSidebarScoreboardWidth(obj);
            plHeight = calculateSidebarScoreboardHeight(obj);
        });
    }

    @Override
    public Dimension<Integer> getSize() {
        return new Dimension<>(width, height);
    }

    @Override
    public Dimension<Integer> getPlaceholderSize() {
        return new Dimension<>(plWidth, plHeight);
    }

    @Override
    public String getNameKey() {
        return "scoreboard";
    }

    @Override
    public int getDefaultX() {
        return 0;
    }

    @Override
    public int getDefaultY() {
        return -10;
    }

    @Override
    public PositionModifiers getDefaultModifier() {
        return PositionModifiers.RIGHT;
    }
}
