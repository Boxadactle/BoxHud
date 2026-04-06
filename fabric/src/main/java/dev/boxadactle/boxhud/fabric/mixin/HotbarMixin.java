package dev.boxadactle.boxhud.fabric.mixin;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public abstract class HotbarMixin {

    @Inject(
            method = "extractHotbarAndDecorations",
            at = @At("HEAD")
    )
    public void startTranslate(GuiGraphicsExtractor guiGraphics, DeltaTracker deltaTracker, CallbackInfo ci) {
        guiGraphics.pose().pushMatrix();
        guiGraphics.pose().translate(-guiGraphics.guiWidth() / 2.0F + 91.0F, -guiGraphics.guiHeight() + 60);
    }

    @Inject(
            method = "extractHotbarAndDecorations",
            at = @At("RETURN")
    )
    public void endTranslate(GuiGraphicsExtractor guiGraphics, DeltaTracker deltaTracker, CallbackInfo ci) {
        guiGraphics.pose().popMatrix();
    }
}
