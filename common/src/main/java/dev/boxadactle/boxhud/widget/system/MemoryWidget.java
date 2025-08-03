package dev.boxadactle.boxhud.widget.system;

import dev.boxadactle.boxhud.WidgetEntry;
import dev.boxadactle.boxhud.widget.SimpleTextWidget;
import dev.boxadactle.boxhud.widget.Widgets;
import dev.boxadactle.boxlib.gui.config.BOptionEntry;
import dev.boxadactle.boxlib.gui.config.widget.button.BBooleanButton;
import dev.boxadactle.boxlib.util.GuiUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import java.util.function.Consumer;

public class MemoryWidget implements Widgets.System, SimpleTextWidget {
    boolean showPercentage = true;
    boolean showMax = true;

    private long bytesToMegabytes(long l) {
        return l / 1024L / 1024L;
    }

    @Override
    public Component getText() {
        long max = Runtime.getRuntime().maxMemory();
        long total = Runtime.getRuntime().totalMemory();
        long free = Runtime.getRuntime().freeMemory();
        long using = total - free;

        MutableComponent text = Component.empty();

        if (showMax) {
            text.append(String.format("%03d/%03dMB ", bytesToMegabytes(using), bytesToMegabytes(max)));
        } else {
            text.append(String.format("%03dMB ", bytesToMegabytes(using)));
        }

        if (showPercentage) {
            long percentage = using * 100L / max;
            text.append(GuiUtils.parentheses(
                    Component.literal(String.format("%2d%%", percentage))
                            .withColor(percentage > 90 ? GuiUtils.RED : GuiUtils.WHITE)
            ));
        }

        return definition("text", text);
    }

    @Override
    public ConfigFactory<?> getConfigFactory() {
        return new ConfigFactory<MemoryWidget>() {
            @Override
            protected void addCustomConfigEntries(WidgetEntry<MemoryWidget> entry, Consumer<BOptionEntry<?>> consumer) {
                consumer.accept(new BBooleanButton(
                        "boxhud.widget.memory.showPercentage",
                        entry.widget.showPercentage,
                        value -> entry.widget.showPercentage = value
                ));

                consumer.accept(new BBooleanButton(
                        "boxhud.widget.memory.showMax",
                        entry.widget.showMax,
                        value -> entry.widget.showMax = value
                ));
            }
        };
    }

    @Override
    public String getNameKey() {
        return "memory";
    }

    @Override
    public int getDefaultX() {
        return 0;
    }

    @Override
    public int getDefaultY() {
        return 35;
    }

    @Override
    public boolean defaultEnabled() {
        return false;
    }
}
