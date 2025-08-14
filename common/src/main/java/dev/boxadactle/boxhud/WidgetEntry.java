package dev.boxadactle.boxhud;

import dev.boxadactle.boxhud.util.ModUtil;
import dev.boxadactle.boxlib.layouts.RenderingLayout;
import dev.boxadactle.boxlib.math.geometry.Rect;
import dev.boxadactle.boxlib.util.RenderUtils;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

public class WidgetEntry<T extends HudWidget> {
    public T widget;
    public boolean enabled;
    public int x;
    public int y;
    public float scale;
    public boolean renderBackground;
    public PositionModifiers modifier;

    public WidgetEntry(T widget, boolean enabled, int x, int y, float scale, boolean renderBackground, PositionModifiers mod) {
        this.widget = widget;
        this.enabled = enabled;
        this.x = x;
        this.y = y;
        this.scale = scale;
        this.renderBackground = renderBackground;
        this.modifier = mod != null ? mod : widget.getDefaultModifier();
    }

    public RenderingLayout preRender() {
        return preRender(false);
    }

    public RenderingLayout preRender(boolean pl) {
        RenderingLayout layout = pl ? widget.createPlaceholderWidget(x, y) : widget.createWidget(x, y);
        Rect<Integer> rect = modifier.mod.translateRect(layout.calculateRect(), ModUtil.getWindowSize(scale));

        if (widget.overridePosition() != null) {
            var pos = widget.overridePosition();
            rect.setX(pos.getX());
            rect.setY(pos.getY());
        } else layout.setPosition(rect.getX(), rect.getY());

        return layout;
    }

    public Rect<Integer> render(GuiGraphics graphics) {
        graphics.pose().pushMatrix();
        graphics.pose().scale(scale, scale);
        RenderingLayout layout = preRender();
        if (renderBackground) {
            RenderUtils.drawSquare(graphics, layout.calculateRect(), Boxhud.getConfig().backgroundColor);
        }
        layout.render(graphics);
        graphics.pose().popMatrix();

        return layout.calculateRect();
    }

    public Rect<Integer> renderPlaceholder(GuiGraphics graphics) {
        graphics.pose().pushMatrix();
        graphics.pose().scale(scale, scale);
        RenderingLayout layout = preRender(true);
        if (renderBackground) {
            RenderUtils.drawSquare(graphics, layout.calculateRect(), Boxhud.getConfig().backgroundColor);
        }
        layout.render(graphics);
        graphics.pose().popMatrix();

        return layout.calculateRect();
    }

    public Component getName() {
        return widget.translation("name");
    }
}