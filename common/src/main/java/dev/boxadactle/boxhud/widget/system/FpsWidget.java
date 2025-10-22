package dev.boxadactle.boxhud.widget.system;

import dev.boxadactle.boxhud.WidgetEntry;
import dev.boxadactle.boxhud.util.FpsTracker;
import dev.boxadactle.boxhud.widget.SimpleTextWidget;
import dev.boxadactle.boxhud.widget.Widgets;
import dev.boxadactle.boxlib.gui.config.BOptionEntry;
import dev.boxadactle.boxlib.gui.config.widget.button.BBooleanButton;
import dev.boxadactle.boxlib.util.ClientUtils;
import dev.boxadactle.boxlib.util.GuiUtils;
import net.minecraft.network.chat.Component;

import java.util.function.Consumer;

public class FpsWidget implements Widgets.System, SimpleTextWidget {

    public boolean showAdvanced = false;

    @Override
    public String getNameKey() {
        return "fps";
    }

    @Override
    public Component getText() {
        int fps = ClientUtils.getClient().getFps();
        FpsTracker.recent(fps);
        if (!showAdvanced) {
            return definition("text", value(Integer.toString(fps)));
        } else {
            int lagScore = FpsTracker.getLagScore();
            int color;
            if (lagScore < 10) {
                color = GuiUtils.GREEN;
            } else if (lagScore < 20) {
                color = GuiUtils.YELLOW;
            } else if (lagScore < 40) {
                color = 0xf5a045;
            } else if (lagScore < 80) {
                color = GuiUtils.RED;
            } else {
                color = GuiUtils.DARK_RED;
            }
            return definition("text_advanced",
                    value(Integer.toString(fps)),
                    value(Integer.toString(FpsTracker.getMinimum())),
                    value(Integer.toString(FpsTracker.getMs())),
                    value(Integer.toString(FpsTracker.getAverage())),
                    GuiUtils.colorize(Component.literal(Integer.toString(lagScore)), color)
            );
        }
    }

    @Override
    public ConfigFactory<?> getConfigFactory() {
        return new ConfigFactory<FpsWidget>() {
            @Override
            protected void addCustomConfigEntries(WidgetEntry<FpsWidget> entry, Consumer<BOptionEntry<?>> consumer) {
                consumer.accept(new BBooleanButton(
                        "boxhud.widget.fps.advanced",
                        entry.widget.showAdvanced,
                        v -> entry.widget.showAdvanced = v
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
        return 0;
    }

    @Override
    public boolean defaultEnabled() {
        return true;
    }
}
