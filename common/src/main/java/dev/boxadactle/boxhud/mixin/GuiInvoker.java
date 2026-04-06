package dev.boxadactle.boxhud.mixin;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.BossHealthOverlay;
import net.minecraft.network.chat.Component;
import net.minecraft.world.scores.Objective;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Gui.class)
public interface GuiInvoker {
    @Invoker("extractCrosshair")
    void invokeRenderCrosshair(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker);

    @Invoker("extractOverlayMessage")
    void invokeRenderOverlayMessage(GuiGraphicsExtractor guiGraphics, DeltaTracker deltaTracker);

    @Invoker("extractScoreboardSidebar")
    void invokeRenderScoreboardSidebar(GuiGraphicsExtractor guiGraphics, DeltaTracker deltaTracker);

    @Invoker("displayScoreboardSidebar")
    void invokeDrawScoreboardSidebar(GuiGraphicsExtractor guiGraphics, Objective objective);

    @Accessor("bossOverlay")
    BossHealthOverlay getBossOverlay();

    @Accessor("overlayMessageString")
    Component getOverlayMessageString();
}
