package dev.boxadactle.boxhud.fabric.mixin;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.Hud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Hud.class)
public interface GuiInvoker {
    @Invoker("extractHotbarAndDecorations")
    void invokeRenderHotbar(GuiGraphicsExtractor guiGraphics, DeltaTracker deltaTracker);

}
