package dev.boxadactle.boxhud.widget.vanilla;

import dev.boxadactle.boxhud.PositionModifiers;
import dev.boxadactle.boxhud.mixin.GuiInvoker;
import dev.boxadactle.boxhud.widget.VanillaWidget;
import dev.boxadactle.boxlib.math.geometry.Dimension;
import dev.boxadactle.boxlib.util.ClientUtils;
import dev.boxadactle.boxlib.util.GuiUtils;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;

public class ActionbarWidget implements VanillaWidget {
    transient Component defaultMessage = Component.literal("Actionbar Text");

    @Override
    public void render(GuiGraphicsExtractor graphics, int x, int y) {
        renderPositioned(graphics, x, y, () -> ((GuiInvoker) ClientUtils.getClient().gui.hud).invokeRenderOverlayMessage(graphics, getDummyTracker()));
    }

    @Override
    public void renderPlaceholder(GuiGraphicsExtractor graphics, int x, int y) {
        var comp = ((GuiInvoker) ClientUtils.getClient().gui.hud).getOverlayMessageString();
        graphics.text(GuiUtils.getTextRenderer(), comp != null ? comp : defaultMessage, x, y, GuiUtils.WHITE);
    }

    @Override
    public Dimension<Integer> getSize() {
        var comp = ((GuiInvoker) ClientUtils.getClient().gui.hud).getOverlayMessageString();
        return new Dimension<>(comp != null ? GuiUtils.getTextSize(comp) : GuiUtils.getTextSize(defaultMessage), GuiUtils.getTextHeight());
    }

    @Override
    public String getNameKey() {
        return "actionbar";
    }

    @Override
    public int getDefaultX() {
        return 0;
    }

    @Override
    public int getDefaultY() {
        return 68;
    }

    @Override
    public PositionModifiers getDefaultModifier() {
        return PositionModifiers.BOTTOM;
    }
}
