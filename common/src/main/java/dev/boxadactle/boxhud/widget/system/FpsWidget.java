package dev.boxadactle.boxhud.widget.system;

import dev.boxadactle.boxhud.WidgetEntry;
import dev.boxadactle.boxhud.mixin.MinecraftAccessor;
import dev.boxadactle.boxhud.widget.SimpleTextWidget;
import dev.boxadactle.boxhud.widget.Widgets;
import dev.boxadactle.boxlib.gui.config.BOptionEntry;
import dev.boxadactle.boxlib.gui.config.widget.button.BBooleanButton;
import dev.boxadactle.boxlib.util.ClientUtils;
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
        if (!showAdvanced) {
            return definition("text", value(Integer.toString(((MinecraftAccessor) ClientUtils.getClient()).getCurrentFps())));
        } else {
            return value(((MinecraftAccessor)ClientUtils.getClient()).getFpsString());
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
