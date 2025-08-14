package dev.boxadactle.boxhud.widget;

import dev.boxadactle.boxhud.HudWidget;
import dev.boxadactle.boxlib.layouts.LayoutComponent;
import dev.boxadactle.boxlib.layouts.RenderingLayout;
import dev.boxadactle.boxlib.layouts.layout.ColumnLayout;
import dev.boxadactle.boxlib.layouts.layout.PaddingLayout;
import dev.boxadactle.boxlib.math.geometry.Dimension;
import net.minecraft.client.gui.GuiGraphics;

public interface VanillaWidget extends Widgets.Vanilla {

    @Override
    default boolean defaultEnabled() {
        return true;
    }

    void render(GuiGraphics graphics, int x, int y);
    void renderPlaceholder(GuiGraphics graphics, int x, int y);

    Dimension<Integer> getSize();

    default Dimension<Integer> getPlaceholderSize() {
        return getSize();
    }

    default void renderPositioned(GuiGraphics graphics, int x, int y, Runnable runnable) {
        // since our mixin moves the overlay to 0, 0,
        // we can use matrix to translate the position
        graphics.pose().pushMatrix();
        graphics.pose().translate(x, y);
        runnable.run();
        graphics.pose().popMatrix();
    }

    @Override
    default RenderingLayout createWidget(int x, int y) {
        ColumnLayout columnLayout = new ColumnLayout(0, 0, 0);

        columnLayout.addComponent(new LayoutComponent<>(null) {
            @Override
            public int getWidth() {
                return getSize().getWidth();
            }

            @Override
            public int getHeight() {
                return getSize().getHeight();
            }

            @Override
            public void render(GuiGraphics guiGraphics, int i, int i1) {
                VanillaWidget.this.render(guiGraphics, i, i1);
            }
        });

        return new PaddingLayout(x, y, padding(), columnLayout);
    }

    @Override
    default RenderingLayout createPlaceholderWidget(int x, int y) {
        ColumnLayout columnLayout = new ColumnLayout(0, 0, 0);

        columnLayout.addComponent(new LayoutComponent<>(null) {
            @Override
            public int getWidth() {
                return getPlaceholderSize().getWidth();
            }

            @Override
            public int getHeight() {
                return getPlaceholderSize().getHeight();
            }

            @Override
            public void render(GuiGraphics guiGraphics, int i, int i1) {
                VanillaWidget.this.renderPlaceholder(guiGraphics, i, i1);
            }
        });

        return new PaddingLayout(x, y, padding(), columnLayout);
    }

    @Override
    default HudCategory getCategory() {
        return HudCategory.VANILLA;
    }
}
