package dev.boxadactle.boxhud.widget.pvp;

import dev.boxadactle.boxhud.PositionModifiers;
import dev.boxadactle.boxhud.WidgetEntry;
import dev.boxadactle.boxhud.widget.SimpleTextWidget;
import dev.boxadactle.boxhud.widget.Widgets;
import dev.boxadactle.boxlib.gui.config.BOptionEntry;
import dev.boxadactle.boxlib.gui.config.widget.button.BBooleanButton;
import dev.boxadactle.boxlib.gui.config.widget.slider.BIntegerSlider;
import dev.boxadactle.boxlib.math.mathutils.NumberFormatter;
import dev.boxadactle.boxlib.util.WorldUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

import java.util.function.Consumer;

public class SpeedWidget implements Widgets.Pvp, SimpleTextWidget {

    public boolean showHorizonalVelocity = true;

    public int decimalPlaces = 2;

    transient NumberFormatter<Double> formatter;

    @Override
    public void init() {
        this.formatter = new NumberFormatter<>(decimalPlaces);
    }

    @Override
    public Component getText() {
        Entity entity = WorldUtils.getPlayer().getVehicle() != null ? WorldUtils.getPlayer().getVehicle() : WorldUtils.getPlayer();
        Vec3 vec = entity.getDeltaMovement();
        if (entity.onGround() && vec.y < 0) {
            vec = new Vec3(vec.x, 0, vec.z);
        }

        double speed = showHorizonalVelocity ? vec.horizontalDistance() : vec.length();

        return definition("text", value(formatter.formatDecimal(speed * 20.0)));
    }

    @Override
    public Component getPlaceholderText() {
        return definition("text", value(formatter.formatDecimal(0.0d)));
    }

    @Override
    public ConfigFactory<SpeedWidget> getConfigFactory() {
        return new ConfigFactory<>() {
            @Override
            protected void addCustomConfigEntries(WidgetEntry<SpeedWidget> entry, Consumer<BOptionEntry<?>> consumer) {
                consumer.accept(new BBooleanButton(
                        "boxhud.widget.speed.showHorizontalVelocity",
                        entry.widget.showHorizonalVelocity,
                        value -> entry.widget.showHorizonalVelocity = value
                ));

                consumer.accept(new BIntegerSlider(
                        "boxhud.widget.speed.decimalPlaces",
                        0, 5,
                        entry.widget.decimalPlaces,
                        value -> {
                            entry.widget.decimalPlaces = value;
                            entry.widget.formatter = new NumberFormatter<>(value);
                        }
                ));
            }
        };
    }

    @Override
    public String getNameKey() {
        return "speed";
    }

    @Override
    public int getDefaultX() {
        return 0;
    }

    @Override
    public int getDefaultY() {
        return 50;
    }

    @Override
    public PositionModifiers getDefaultModifier() {
        return PositionModifiers.BOTTOM_LEFT;
    }

    @Override
    public boolean defaultEnabled() {
        return false;
    }
}
