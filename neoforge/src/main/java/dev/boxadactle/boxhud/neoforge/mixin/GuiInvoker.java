package dev.boxadactle.boxhud.neoforge.mixin;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Gui.class)
public interface GuiInvoker {
    @Invoker("renderHotbar")
    void invokeRenderHotbar(GuiGraphics guiGraphics, DeltaTracker deltaTracker);

}
