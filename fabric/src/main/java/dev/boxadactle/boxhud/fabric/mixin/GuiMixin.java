package dev.boxadactle.boxhud.fabric.mixin;

import dev.boxadactle.boxhud.BoxWidgets;
import dev.boxadactle.boxlib.util.ClientUtils;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.Hud;
import net.minecraft.client.gui.screens.LevelLoadingScreen;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Hud.class)
public abstract class GuiMixin {

    @Shadow @Final private Minecraft minecraft;

    @Shadow
    protected abstract void extractCameraOverlays(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker);

    @Shadow
    protected abstract void extractSleepOverlay(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker);

    @Shadow
    protected abstract void extractDemoOverlay(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker);

    @Shadow
    protected abstract void extractTitle(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker);

    @Shadow
    protected abstract void extractChat(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker);

    @Shadow
    protected abstract void extractTabList(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker);

    @Shadow
    protected abstract void extractSubtitleOverlay(GuiGraphicsExtractor graphics, boolean deferRendering);

    @Inject(
            method = "extractRenderState",
            at = @At("HEAD"),
            cancellable = true
    )
    public void removeRenderers1(GuiGraphicsExtractor guiGraphics, DeltaTracker deltaTracker, CallbackInfo ci) {
        if (!(ClientUtils.getCurrentScreen() instanceof LevelLoadingScreen)) {
            if (!this.minecraft.gui.hud.isHidden()) {
                this.extractCameraOverlays(guiGraphics, deltaTracker);
                guiGraphics.nextStratum();
            }

            this.extractSleepOverlay(guiGraphics, deltaTracker);
            if (!this.minecraft.gui.hud.isHidden()) {
                this.extractDemoOverlay(guiGraphics, deltaTracker);
                this.extractTitle(guiGraphics, deltaTracker);
                this.extractChat(guiGraphics, deltaTracker);
                this.extractTabList(guiGraphics, deltaTracker);
                this.extractSubtitleOverlay(guiGraphics, ClientUtils.getCurrentScreen() == null || ClientUtils.getCurrentScreen().isInGameUi());

                guiGraphics.nextStratum();
                BoxWidgets.renderAll(guiGraphics);
            } else if (ClientUtils.getCurrentScreen() != null && ClientUtils.getCurrentScreen().isInGameUi()) {
                this.extractSubtitleOverlay(guiGraphics, true);
            }

        }

        ci.cancel();
    }

}
