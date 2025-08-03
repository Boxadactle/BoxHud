package dev.boxadactle.boxhud.widget.vanilla;

import dev.boxadactle.boxhud.PositionModifiers;
import dev.boxadactle.boxhud.mixin.BossHealthOverlayAccessor;
import dev.boxadactle.boxhud.mixin.GuiInvoker;
import dev.boxadactle.boxhud.widget.VanillaWidget;
import dev.boxadactle.boxlib.math.geometry.Dimension;
import dev.boxadactle.boxlib.util.ClientUtils;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.BossHealthOverlay;
import net.minecraft.client.gui.components.LerpingBossEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.BossEvent;

import java.util.UUID;

public class BossbarWidget implements VanillaWidget {
    transient BossHealthOverlay placeholder;

    @Override
    public void init() {
        this.placeholder = new BossHealthOverlay(ClientUtils.getClient());
        // add with a random UUID
        UUID thing = UUID.randomUUID();
        ((BossHealthOverlayAccessor)placeholder).getEvents().put(
                thing,
                new LerpingBossEvent(
                        thing,
                        Component.translatable("boxhud.widget.bossbar.name"),
                        0.5F,
                        BossEvent.BossBarColor.PURPLE,
                        BossEvent.BossBarOverlay.NOTCHED_12,
                        false, false, false
                )
        );
    }

    public void renderBar(GuiGraphics graphics, int x, int y, BossHealthOverlay overlay) {
        if (!((BossHealthOverlayAccessor)overlay).getEvents().isEmpty()) {
            renderPositioned(graphics, x, y, () -> overlay.render(graphics));
        }
    }

    @Override
    public Dimension<Integer> getSize() {
        return new Dimension<>(184, 12);
    }

    @Override
    public void render(GuiGraphics graphics, int x, int y) {
        renderBar(graphics, x, y, ((GuiInvoker) ClientUtils.getClient().gui).getBossOverlay());
    }

    @Override
    public void renderPlaceholder(GuiGraphics graphics, int x, int y) {
        renderBar(graphics, x, y, placeholder);
    }

    @Override
    public String getNameKey() {
        return "bossbar";
    }

    @Override
    public int getDefaultX() {
        return 0;
    }

    @Override
    public int getDefaultY() {
        return 12;
    }

    @Override
    public PositionModifiers getDefaultModifier() {
        return PositionModifiers.TOP;
    }
}
