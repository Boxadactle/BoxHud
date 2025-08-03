package dev.boxadactle.boxhud;

import dev.boxadactle.boxlib.gui.config.BOptionScreen;
import net.minecraft.client.gui.screens.Screen;

public abstract class BoxhudConfigScreen extends BOptionScreen {
    public BoxhudConfigScreen(Screen parent) {
        super(parent);
    }

    @Override
    public void onClose() {
        if (parent instanceof WidgetPositionScreen) {
            ((WidgetPositionScreen) parent).refresh();
        }

        super.onClose();
    }
}
