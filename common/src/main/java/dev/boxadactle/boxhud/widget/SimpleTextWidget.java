package dev.boxadactle.boxhud.widget;

import dev.boxadactle.boxhud.HudWidget;
import dev.boxadactle.boxlib.layouts.RenderingLayout;
import dev.boxadactle.boxlib.layouts.component.TextComponent;
import dev.boxadactle.boxlib.layouts.layout.ColumnLayout;
import dev.boxadactle.boxlib.layouts.layout.PaddingLayout;
import net.minecraft.network.chat.Component;

public interface SimpleTextWidget extends HudWidget {

    Component getText();

    default Component getPlaceholderText() {
        return getText();
    }

    @Override
    default RenderingLayout createWidget(int x, int y) {
        ColumnLayout columnLayout = new ColumnLayout(0, 0, 0);

        columnLayout.addComponent(new TextComponent(getText()));

        return new PaddingLayout(x, y, padding(), columnLayout);
    }

    @Override
    default RenderingLayout createPlaceholderWidget(int x, int y) {
        ColumnLayout columnLayout = new ColumnLayout(0, 0, 0);

        columnLayout.addComponent(new TextComponent(getPlaceholderText()));

        return new PaddingLayout(x, y, padding(), columnLayout);
    }
}
