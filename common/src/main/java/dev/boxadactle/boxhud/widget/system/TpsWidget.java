package dev.boxadactle.boxhud.widget.system;

import dev.boxadactle.boxhud.WidgetEntry;
import dev.boxadactle.boxhud.widget.SimpleTextWidget;
import dev.boxadactle.boxhud.widget.Widgets;
import dev.boxadactle.boxlib.gui.config.BOptionEntry;
import dev.boxadactle.boxlib.gui.config.widget.button.BBooleanButton;
import dev.boxadactle.boxlib.gui.config.widget.slider.BIntegerSlider;
import dev.boxadactle.boxlib.math.mathutils.NumberFormatter;
import dev.boxadactle.boxlib.util.GuiUtils;
import dev.boxadactle.boxlib.util.WorldUtils;
import net.minecraft.network.chat.Component;

import java.util.function.Consumer;

public class TpsWidget implements Widgets.System, SimpleTextWidget {

    public boolean colorWhenLow = true;
    public boolean colorWhenFrozen = true;

    public int decimalPlaces = 0;

    public int getColor(float tps, boolean frozen) {
        if (frozen && colorWhenFrozen) {
            return GuiUtils.DARK_RED;
        }

        if (tps < 10.0f) {
            if (tps < 5.0f) {
                return GuiUtils.RED;
            } else {
                return GuiUtils.YELLOW;
            }
        } else {
            return config().definitionColor;
        }
    }

    @Override
    public Component getText() {
        NumberFormatter<Float> formatter = new NumberFormatter<>(decimalPlaces);

        var tickrate = WorldUtils.getWorld().tickRateManager();
        float tps = tickrate.tickrate();
        boolean frozen = tickrate.isFrozen();

        return GuiUtils.colorize(translation("text", value(formatter.formatDecimal(tps))), getColor(tps, frozen));
    }

    @Override
    public Component getPlaceholderText() {
        return definition("text", value("20.0"));
    }

    @Override
    public String getNameKey() {
        return "tps";
    }

    @Override
    public ConfigFactory<TpsWidget> getConfigFactory() {
        return new ConfigFactory<>() {
            @Override
            protected void addCustomConfigEntries(WidgetEntry<TpsWidget> entry, Consumer<BOptionEntry<?>> consumer) {
                consumer.accept(new BBooleanButton(
                        "boxhud.widget.tps.colorWhenLow",
                        entry.widget.colorWhenLow,
                        value -> entry.widget.colorWhenLow = value
                ));

                consumer.accept(new BBooleanButton(
                        "boxhud.widget.tps.colorWhenFrozen",
                        entry.widget.colorWhenFrozen,
                        value -> entry.widget.colorWhenFrozen = value
                ));

                consumer.accept(new BIntegerSlider(
                        "boxhud.widget.tps.decimalPlaces",
                        0, 3,
                        entry.widget.decimalPlaces,
                        value -> entry.widget.decimalPlaces = value
                ));
            }
        };
    }

    @Override
    public int getDefaultX() {
        return 0;
    }

    @Override
    public int getDefaultY() {
        return 30;
    }

    @Override
    public boolean defaultEnabled() {
        return false;
    }
}
