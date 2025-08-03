package dev.boxadactle.boxhud.widget.system;

import dev.boxadactle.boxhud.WidgetEntry;
import dev.boxadactle.boxhud.events.BoxEvents;
import dev.boxadactle.boxhud.util.ClickTracker;
import dev.boxadactle.boxhud.widget.SimpleTextWidget;
import dev.boxadactle.boxhud.widget.Widgets;
import dev.boxadactle.boxlib.gui.config.BOptionEntry;
import dev.boxadactle.boxlib.gui.config.widget.button.BBooleanButton;
import net.minecraft.network.chat.Component;

import java.util.function.Consumer;

public class CpsWidget implements Widgets.System, SimpleTextWidget {
    public boolean showRight = true;

    @Override
    public String getNameKey() {
        return "cps";
    }

    @Override
    public Component getText() {
        if (showRight) {
            return definition("all", value(Integer.toString(ClickTracker.getLeftCPS())), value(Integer.toString(ClickTracker.getRightCPS())));
        } else {
            return definition("left", value(Integer.toString(ClickTracker.getLeftCPS())));
        }
    }

    @Override
    public ConfigFactory<?> getConfigFactory() {
        return new ConfigFactory<CpsWidget>() {
            @Override
            protected void addCustomConfigEntries(WidgetEntry<CpsWidget> entry, Consumer<BOptionEntry<?>> consumer) {
                consumer.accept(new BBooleanButton(
                        "boxhud.widget.cps.showright",
                        entry.widget.showRight,
                        v -> entry.widget.showRight = v
                ));
            }
        };
    }

    @Override
    public void init() {
        BoxEvents.CLICK_LEFT_MOUSE.register((i, j) -> ClickTracker.clickLeft());
        BoxEvents.CLICK_RIGHT_MOUSE.register((i, j) -> ClickTracker.clickRight());
    }

    @Override
    public int getDefaultX() {
        return 0;
    }

    @Override
    public int getDefaultY() {
        return 10;
    }

    @Override
    public boolean defaultEnabled() {
        return false;
    }
}
