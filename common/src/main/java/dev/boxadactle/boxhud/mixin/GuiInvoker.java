package dev.boxadactle.boxhud.mixin;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.BossHealthOverlay;
import net.minecraft.network.chat.Component;
import net.minecraft.world.scores.Objective;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Gui.class)
public interface GuiInvoker {
    @Invoker("renderCrosshair")
    void invokeRenderCrosshair(GuiGraphics graphics, float tickDelta);

    @Invoker("renderHotbarAndDecorations")
    void invokeRenderHotbar(GuiGraphics guiGraphics, float partialTick);

    @Invoker("renderOverlayMessage")
    void invokeRenderOverlayMessage(GuiGraphics guiGraphics, float partialTick);

    @Invoker("renderScoreboardSidebar")
    void invokeRenderScoreboardSidebar(GuiGraphics guiGraphics, float partialTick);

    @Invoker("displayScoreboardSidebar")
    void invokeDrawScoreboardSidebar(GuiGraphics guiGraphics, Objective objective);

    @Accessor("bossOverlay")
    BossHealthOverlay getBossOverlay();

    @Accessor("overlayMessageString")
    Component getOverlayMessageString();
}
