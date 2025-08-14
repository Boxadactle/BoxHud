package dev.boxadactle.boxhud;

import dev.boxadactle.boxlib.gui.config.BOptionScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public abstract class BoxhudConfigScreen extends BOptionScreen {

    public BoxhudConfigScreen(Screen parent, Component name) {
        super(parent, name);
    }

    @Override
    public void onClose() {
        if (lastScreen instanceof WidgetPositionScreen) {
            ((WidgetPositionScreen) lastScreen).refresh();
        }

        super.onClose();
    }
}
