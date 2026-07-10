package dev.boxadactle.boxhud.neoforge.mixin;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.Hud;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Hud.class)
public abstract class HotbarMixin {

    @Shadow @Final private Minecraft minecraft;

    @Shadow
    protected abstract void extractHealthLevel(GuiGraphicsExtractor graphics);

    @Shadow
    protected abstract void extractArmorLevel(GuiGraphicsExtractor graphics);

    @Shadow
    protected abstract void extractFoodLevel(GuiGraphicsExtractor graphics);

    @Shadow
    protected abstract void extractVehicleHealth(GuiGraphicsExtractor graphics);

    @Shadow
    protected abstract void extractAirLevel(GuiGraphicsExtractor graphics);

    @Shadow
    protected abstract void extractExperienceLevel(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker);

    @Shadow
    protected abstract void maybeExtractSelectedItemName(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker);

    @Shadow
    protected abstract void maybeExtractSpectatorTooltip(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker);

    @Inject(
            method = "extractHotbar",
            at = @At("HEAD")
    )
    public void startTranslate(GuiGraphicsExtractor guiGraphics, DeltaTracker deltaTracker, CallbackInfo ci) {
        guiGraphics.pose().pushMatrix();
        guiGraphics.pose().translate(-guiGraphics.guiWidth() / 2.0F + 91.0F, -guiGraphics.guiHeight() + 60);
    }

    @Inject(
            method = "extractHotbar",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Hud;extractItemHotbar(Lnet/minecraft/client/gui/GuiGraphicsExtractor;Lnet/minecraft/client/DeltaTracker;)V")
    )
    public void renderEverythingElse(GuiGraphicsExtractor guiGraphics, DeltaTracker deltaTracker, CallbackInfo ci) {
        if (this.minecraft.gameMode.canHurtPlayer()) {
            extractHealthLevel(guiGraphics);
            extractArmorLevel(guiGraphics);
            extractFoodLevel(guiGraphics);
            extractVehicleHealth(guiGraphics);
            extractAirLevel(guiGraphics);
        }

        extractExperienceLevel(guiGraphics, deltaTracker);
        maybeExtractSelectedItemName(guiGraphics, deltaTracker);
        maybeExtractSpectatorTooltip(guiGraphics, deltaTracker);
    }

    @Inject(
            method = "extractHotbar",
            at = @At("RETURN")
    )
    public void endTranslate(GuiGraphicsExtractor guiGraphics, DeltaTracker deltaTracker, CallbackInfo ci) {
        guiGraphics.pose().popMatrix();
    }
}
