package dev.boxadactle.boxhud.fabric.mixin;

import dev.boxadactle.boxhud.BoxWidgets;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.LevelLoadingScreen;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public abstract class GuiMixin {

    @Shadow @Final private Minecraft minecraft;

    @Shadow protected abstract void renderCameraOverlays(GuiGraphics guiGraphics, DeltaTracker deltaTracker);

    @Shadow protected abstract void renderSleepOverlay(GuiGraphics guiGraphics, DeltaTracker deltaTracker);

    @Shadow protected abstract void renderDemoOverlay(GuiGraphics guiGraphics, DeltaTracker deltaTracker);

    @Shadow protected abstract void renderTitle(GuiGraphics guiGraphics, DeltaTracker deltaTracker);

    @Shadow protected abstract void renderChat(GuiGraphics guiGraphics, DeltaTracker deltaTracker);

    @Shadow protected abstract void renderTabList(GuiGraphics guiGraphics, DeltaTracker deltaTracker);

    @Shadow public abstract void renderSubtitleOverlay(GuiGraphics p_406760_, boolean p_422895_);

    @Inject(
            method = "render",
            at = @At("HEAD"),
            cancellable = true
    )
    public void removeRenderers1(GuiGraphics guiGraphics, DeltaTracker deltaTracker, CallbackInfo ci) {
        if (!(this.minecraft.screen instanceof LevelLoadingScreen)) {
            if (!this.minecraft.options.hideGui) {
                this.renderCameraOverlays(guiGraphics, deltaTracker);
                guiGraphics.nextStratum();
            }

            this.renderSleepOverlay(guiGraphics, deltaTracker);
            if (!this.minecraft.options.hideGui) {
                this.renderDemoOverlay(guiGraphics, deltaTracker);
                this.renderTitle(guiGraphics, deltaTracker);
                this.renderChat(guiGraphics, deltaTracker);
                this.renderTabList(guiGraphics, deltaTracker);
                this.renderSubtitleOverlay(guiGraphics, this.minecraft.screen == null || this.minecraft.screen.isInGameUi());

                guiGraphics.nextStratum();
                BoxWidgets.renderAll(guiGraphics);
            } else if (this.minecraft.screen != null && this.minecraft.screen.isInGameUi()) {
                this.renderSubtitleOverlay(guiGraphics, true);
            }

        }

        ci.cancel();
    }

}
