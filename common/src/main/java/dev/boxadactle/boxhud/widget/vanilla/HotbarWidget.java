package dev.boxadactle.boxhud.widget.vanilla;

import dev.boxadactle.boxhud.PositionModifiers;
import dev.boxadactle.boxhud.util.ModUtil;
import dev.boxadactle.boxhud.widget.VanillaWidget;
import dev.boxadactle.boxlib.math.geometry.Dimension;
import dev.boxadactle.boxlib.util.ClientUtils;
import dev.boxadactle.boxlib.util.GuiUtils;
import dev.boxadactle.boxlib.util.WorldUtils;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

public class HotbarWidget implements VanillaWidget {
    @Override
    public void render(GuiGraphicsExtractor graphics, int x, int y) {
        renderPositioned(graphics, x, y, () -> ModUtil.renderHotbar(ClientUtils.getClient().gui, graphics, getDummyTracker()));
    }

    @Override
    public void renderPlaceholder(GuiGraphicsExtractor graphics, int x, int y) {
        if (WorldUtils.getWorld() != null) {
            render(graphics, x, y);
        } else {
            renderPositioned(graphics, x, y, () -> {
                graphics.blitSprite(RenderPipelines.GUI_TEXTURED, Identifier.withDefaultNamespace("hud/hotbar"), 0, 40, 182, 22);
                graphics.blitSprite(RenderPipelines.GUI_TEXTURED, Identifier.withDefaultNamespace("hud/hotbar_selection"), 59, 39, 24, 23);

                renderFakeFlatItem(graphics, "oak_boat", 64, 44);
                graphics.centeredText(GuiUtils.getTextRenderer(), Component.translatable("item.minecraft.oak_boat"), 91, 25, GuiUtils.WHITE);
            });
        }
    }

    @Override
    public Dimension<Integer> getSize() {
        return new Dimension<>(182, 60);
    }

    @Override
    public String getNameKey() {
        return "hotbar";
    }

    @Override
    public int getDefaultX() {
        return 0;
    }

    @Override
    public int getDefaultY() {
        return 3;
    }

    @Override
    public PositionModifiers getDefaultModifier() {
        return PositionModifiers.BOTTOM;
    }
}
