package dev.boxadactle.boxhud;

import dev.boxadactle.boxlib.gui.config.widget.BSpacingEntry;
import dev.boxadactle.boxlib.gui.config.widget.field.BHexField;
import dev.boxadactle.boxlib.gui.config.widget.label.BCenteredLabel;
import dev.boxadactle.boxlib.gui.config.widget.slider.BIntegerSlider;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class WidgetConfigScreen extends BoxhudConfigScreen {
    public WidgetConfigScreen(Screen parent) {
        super(parent);
    }

    @Override
    protected Component getName() {
        return Component.translatable("boxhud.gui.globalsettings.title");
    }

    @Override
    protected void initFooter(int i, int i1) {
        addRenderableWidget(createDoneButton(i, i1, parent));
    }

    private ModConfig config() {
        return Boxhud.getConfig();
    }

    @Override
    protected void initConfigButtons() {
        addConfigLine(new BCenteredLabel(Component.translatable("boxhud.gui.globalsettings.definitionColor")));

        addConfigLine(new BHexField(
                config().definitionColor,
                c -> config().definitionColor = c
        ));


        addConfigLine(new BCenteredLabel(Component.translatable("boxhud.gui.globalsettings.dataColor")));

        addConfigLine(new BHexField(
                config().dataColor,
                c -> config().dataColor = c
        ));

        addConfigLine(new BSpacingEntry());

        addConfigLine(new BIntegerSlider(
                "boxhud.gui.globalsettings.padding",
                0, 10,
                config().padding,
                v -> config().padding = v
        ));

        addConfigLine(new BSpacingEntry());
    }
}
