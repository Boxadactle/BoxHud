package dev.boxadactle.boxhud;

import dev.boxadactle.boxlib.gui.config.widget.BSpacingEntry;
import dev.boxadactle.boxlib.gui.config.widget.button.BColorPickerButton;
import dev.boxadactle.boxlib.gui.config.widget.slider.BIntegerSlider;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class WidgetConfigScreen extends BoxhudConfigScreen {
    public WidgetConfigScreen(Screen parent) {
        super(parent, Component.translatable("boxhud.gui.globalsettings.title"));
    }

    @Override
    protected void initFooter(LinearLayout layout) {
        layout.addChild(createDoneButton(parent));
    }

    private ModConfig config() {
        return Boxhud.getConfig();
    }

    @Override
    protected void addOptions() {
        addConfigLine(new BColorPickerButton(
                "boxhud.gui.globalsettings.definitionColor",
                this,
                false,
                config().definitionColor,
                c -> config().definitionColor = c
        ));

        addConfigLine(new BColorPickerButton(
                "boxhud.gui.globalsettings.dataColor",
                this, false,
                config().dataColor,
                c -> config().dataColor = c
        ));

        addConfigLine(new BColorPickerButton(
                "boxhud.gui.globalsettings.backgroundColor",
                this, true,
                config().backgroundColor,
                c -> config().backgroundColor = c
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
