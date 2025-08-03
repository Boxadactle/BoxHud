package dev.boxadactle.boxhud.neoforge.mixin;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public abstract class HotbarMixin {

    @Shadow protected abstract void renderExperienceLevel(GuiGraphics guiGraphics, DeltaTracker deltaTracker);

    @Shadow protected abstract void renderAirLevel(GuiGraphics p_283143_);

    @Inject(
            method = "renderHotbarAndDecorations",
            at = @At("HEAD")
    )
    public void startTranslate(GuiGraphics guiGraphics, DeltaTracker deltaTracker, CallbackInfo ci) {
        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(-guiGraphics.guiWidth() / 2.0F + 91.0F, -guiGraphics.guiHeight() + 60, 0.0F);
    }

    @Inject(
            method = "maybeRenderExperienceBar",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Gui;renderExperienceBar(Lnet/minecraft/client/gui/GuiGraphics;I)V")
    )
    public void renderExperienceBar(GuiGraphics guiGraphics, DeltaTracker deltaTracker, CallbackInfo ci) {
        this.renderExperienceLevel(guiGraphics, deltaTracker);
    }

    @Inject(
            method = "renderHotbarAndDecorations",
            at = @At("RETURN")
    )
    public void endTranslate(GuiGraphics guiGraphics, DeltaTracker deltaTracker, CallbackInfo ci) {
        guiGraphics.pose().popPose();
    }
}
