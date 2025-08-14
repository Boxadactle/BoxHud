package dev.boxadactle.boxhud.neoforge.mixin;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public abstract class HotbarMixin {

    @Shadow protected abstract void renderExperienceLevel(GuiGraphics guiGraphics, DeltaTracker deltaTracker);

    @Shadow protected abstract void renderHealthLevel(GuiGraphics p_283143_);

    @Shadow @Final private Minecraft minecraft;

    @Shadow protected abstract void renderArmorLevel(GuiGraphics p_283143_);

    @Shadow protected abstract void renderFoodLevel(GuiGraphics p_283143_);

    @Shadow protected abstract void renderVehicleHealth(GuiGraphics guiGraphics);

    @Shadow protected abstract void renderAirLevel(GuiGraphics p_283143_);

    @Shadow protected abstract void maybeRenderSelectedItemName(GuiGraphics p_316628_, DeltaTracker p_348543_);

    @Shadow protected abstract void maybeRenderSpectatorTooltip(GuiGraphics p_316628_, DeltaTracker p_348543_);

    @Inject(
            method = "renderHotbar",
            at = @At("HEAD")
    )
    public void startTranslate(GuiGraphics guiGraphics, DeltaTracker deltaTracker, CallbackInfo ci) {
        guiGraphics.pose().pushMatrix();
        guiGraphics.pose().translate(-guiGraphics.guiWidth() / 2.0F + 91.0F, -guiGraphics.guiHeight() + 60);
    }

    @Inject(
            method = "renderHotbar",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Gui;renderItemHotbar(Lnet/minecraft/client/gui/GuiGraphics;Lnet/minecraft/client/DeltaTracker;)V")
    )
    public void renderEverythingElse(GuiGraphics guiGraphics, DeltaTracker deltaTracker, CallbackInfo ci) {
        if (this.minecraft.gameMode.canHurtPlayer()) {
            renderHealthLevel(guiGraphics);
            renderArmorLevel(guiGraphics);
            renderFoodLevel(guiGraphics);
            renderVehicleHealth(guiGraphics);
            renderAirLevel(guiGraphics);
        }

        renderExperienceLevel(guiGraphics, deltaTracker);
        maybeRenderSelectedItemName(guiGraphics, deltaTracker);
        maybeRenderSpectatorTooltip(guiGraphics, deltaTracker);
    }

    @Inject(
            method = "renderHotbar",
            at = @At("RETURN")
    )
    public void endTranslate(GuiGraphics guiGraphics, DeltaTracker deltaTracker, CallbackInfo ci) {
        guiGraphics.pose().popMatrix();
    }
}
