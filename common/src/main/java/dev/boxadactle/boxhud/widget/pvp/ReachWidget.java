package dev.boxadactle.boxhud.widget.pvp;

import dev.boxadactle.boxhud.PositionModifiers;
import dev.boxadactle.boxhud.WidgetEntry;
import dev.boxadactle.boxhud.events.BoxEvents;
import dev.boxadactle.boxhud.widget.Widgets;
import dev.boxadactle.boxlib.gui.config.BOptionEntry;
import dev.boxadactle.boxlib.gui.config.widget.button.BBooleanButton;
import dev.boxadactle.boxlib.gui.config.widget.slider.BIntegerSlider;
import dev.boxadactle.boxlib.layouts.RenderingLayout;
import dev.boxadactle.boxlib.layouts.component.TextComponent;
import dev.boxadactle.boxlib.layouts.layout.ColumnLayout;
import dev.boxadactle.boxlib.layouts.layout.PaddingLayout;
import dev.boxadactle.boxlib.math.mathutils.NumberFormatter;
import dev.boxadactle.boxlib.util.GuiUtils;
import dev.boxadactle.boxlib.util.WorldUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ReachWidget implements Widgets.Pvp {

    // seconds
    public int resetAfter = 10;

    public boolean resetOnNewTarget = true;

    public int decimalPlaces = 2;

    public int textPadding = 2;

    public boolean showLast = true;
    public boolean showMax = true;
    public boolean showAverage = true;

    transient List<Double> reachDistances = new ArrayList<>();
    transient long lastHit = 0;
    transient int target = -1;

    @Override
    public String getNameKey() {
        return "reach";
    }

    @Override
    public void tick() {
        if (target != -1 && System.currentTimeMillis() - lastHit > resetAfter * 1000L) {
            reachDistances.clear();
            target = -1;
        }
    }

    @Override
    public void init() {
        BoxEvents.ENTITY_ATTACK.register(this::onAttack);
    }

    public void onAttack(Entity entity) {
        if (target == -1 || (resetOnNewTarget && entity.getId() != target)) {
            target = entity.getId();
            reachDistances.clear();
        } else {
            // this logic will be drastically improved later
            Entity me = WorldUtils.getCamera();
            reachDistances.add(Math.sqrt(me.distanceToSqr(entity)));
        }
        lastHit = System.currentTimeMillis();
    }

    public Component getColor(double reach) {
        if (reach <= 3.5) {
            return GuiUtils.colorize(Component.literal("G"), GuiUtils.GREEN);
        } else if (reach <= 4.0) {
            return GuiUtils.colorize(Component.literal("B"), GuiUtils.YELLOW);
        } else {
            return GuiUtils.colorize(Component.literal("S"), GuiUtils.RED);
        }
    }

    @Override
    public RenderingLayout createWidget(int x, int y) {
        ColumnLayout layout = new ColumnLayout(0, 0, textPadding);

        if (reachDistances.isEmpty()) {
            layout.addComponent(new TextComponent(definition("text", value("-"))));
            if (showLast) layout.addComponent(new TextComponent(definition("last", value("-"))));
            if (showMax) layout.addComponent(new TextComponent(definition("max", value("-"))));
            if (showAverage) layout.addComponent(new TextComponent(definition("avg", value("-"))));
        } else {
            NumberFormatter<Double> formatter = new NumberFormatter<>(decimalPlaces);

            Component sus = value(GuiUtils.parentheses(getColor(reachDistances.getLast())));
            Component val = Component.literal(formatter.formatDecimal(reachDistances.getLast()));
            layout.addComponent(new TextComponent(definition("text", value(val.copy().append(" ").append(sus)))));

            if (showLast) {
                String last = reachDistances.size() >= 2 ? formatter.formatDecimal(reachDistances.get(reachDistances.size() - 2)) : "-";
                layout.addComponent(new TextComponent(definition("last", value(last))));
            }

            if (showMax) {
                double max = reachDistances.stream().mapToDouble(Double::doubleValue).max().orElse(0);
                String maxStr = max > 0 ? formatter.formatDecimal(max) : "-";
                layout.addComponent(new TextComponent(definition("max", value(maxStr))));
            }

            if (showAverage) {
                double average = reachDistances.stream().mapToDouble(Double::doubleValue).average().orElse(0);
                String avgStr = average > 0 ? formatter.formatDecimal(average) : "-";
                layout.addComponent(new TextComponent(definition("avg", value(avgStr))));
            }
        }

        return new PaddingLayout(x, y, padding(), layout);
    }

    @Override
    public ConfigFactory<ReachWidget> getConfigFactory() {
        return new ConfigFactory<>() {
            @Override
            protected void addCustomConfigEntries(WidgetEntry<ReachWidget> entry, Consumer<BOptionEntry<?>> consumer) {
                consumer.accept(new BIntegerSlider(
                        "boxhud.widget.reach.resetAfter",
                        1, 60,
                        entry.widget.resetAfter,
                        value -> entry.widget.resetAfter = value
                ));

                consumer.accept(new BBooleanButton(
                        "boxhud.widget.reach.resetOnNewTarget",
                        entry.widget.resetOnNewTarget,
                        value -> entry.widget.resetOnNewTarget = value
                ));

                consumer.accept(new BIntegerSlider(
                        "boxhud.widget.reach.decimalPlaces",
                        0, 5,
                        entry.widget.decimalPlaces,
                        value -> entry.widget.decimalPlaces = value
                ));

                consumer.accept(new BIntegerSlider(
                        "boxhud.widget.reach.textPadding",
                        0, 4,
                        entry.widget.textPadding,
                        value -> entry.widget.textPadding = value
                ));

                consumer.accept(new BBooleanButton(
                        "boxhud.widget.reach.showLast",
                        entry.widget.showLast,
                        value -> entry.widget.showLast = value
                ));

                consumer.accept(new BBooleanButton(
                        "boxhud.widget.reach.showMax",
                        entry.widget.showMax,
                        value -> entry.widget.showMax = value
                ));

                consumer.accept(new BBooleanButton(
                        "boxhud.widget.reach.showAvg",
                        entry.widget.showAverage,
                        value -> entry.widget.showAverage = value
                ));
            }
        };
    }

    @Override
    public int getDefaultX() {
        return 10;
    }

    @Override
    public int getDefaultY() {
        return 0;
    }

    @Override
    public PositionModifiers getDefaultModifier() {
        return PositionModifiers.LEFT;
    }

    @Override
    public boolean defaultEnabled() {
        return false;
    }
}
