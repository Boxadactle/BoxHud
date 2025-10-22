package dev.boxadactle.boxhud.neoforge.mixin;

import dev.boxadactle.boxhud.BoxWidgets;
import dev.boxadactle.boxhud.Boxhud;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.gui.GuiLayerManager;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.BooleanSupplier;

@Mixin(Gui.class)
public abstract class GuiMixin {

    @Shadow @Final private GuiLayerManager layerManager;

    @Shadow @Final private Minecraft minecraft;

    @Shadow protected abstract void renderCameraOverlays(GuiGraphics guiGraphics, DeltaTracker deltaTracker);

    @Shadow protected abstract void renderContextualInfoBarBackground(GuiGraphics p_316628_, DeltaTracker p_348543_);

    @Shadow protected abstract void renderSleepOverlay(GuiGraphics guiGraphics, DeltaTracker deltaTracker);

    @Shadow protected abstract void renderDemoOverlay(GuiGraphics guiGraphics, DeltaTracker deltaTracker);

    @Shadow protected abstract void renderTitle(GuiGraphics guiGraphics, DeltaTracker deltaTracker);

    @Shadow protected abstract void renderChat(GuiGraphics guiGraphics, DeltaTracker deltaTracker);

    @Shadow protected abstract void renderTabList(GuiGraphics guiGraphics, DeltaTracker deltaTracker);

    @Shadow public abstract void renderSubtitleOverlay(GuiGraphics p_406760_, boolean p_422895_);

    @Inject(
            method = "registerVanillaLayers",
            at = @At("HEAD"),
            cancellable = true
    )
    private void unRegisterVanillaLayers(CallbackInfo ci) {
        BooleanSupplier guiVisible = () -> !this.minecraft.options.hideGui;
        this.layerManager.add(VanillaGuiLayers.CAMERA_OVERLAYS, this::renderCameraOverlays, guiVisible);
        this.layerManager.add(VanillaGuiLayers.AFTER_CAMERA_DECORATIONS, (guiGraphics, deltaTracker) -> guiGraphics.nextStratum(), guiVisible);
        this.layerManager.add(VanillaGuiLayers.CONTEXTUAL_INFO_BAR_BACKGROUND, this::renderContextualInfoBarBackground, guiVisible);
        this.layerManager.add(VanillaGuiLayers.SLEEP_OVERLAY, this::renderSleepOverlay);
        this.layerManager.add(VanillaGuiLayers.DEMO_OVERLAY, this::renderDemoOverlay, guiVisible);
        this.layerManager.add(VanillaGuiLayers.TITLE, this::renderTitle, guiVisible);
        this.layerManager.add(VanillaGuiLayers.CHAT, this::renderChat, guiVisible);
        this.layerManager.add(VanillaGuiLayers.TAB_LIST, this::renderTabList, guiVisible);
        this.layerManager.add(VanillaGuiLayers.SUBTITLE_OVERLAY, (graphics, deltaTracker) -> {
            if (!this.minecraft.options.hideGui) {
                this.renderSubtitleOverlay(graphics, this.minecraft.screen == null || this.minecraft.screen.isInGameUi());
            } else if (this.minecraft.screen != null && this.minecraft.screen.isInGameUi()) {
                this.renderSubtitleOverlay(graphics, true);
            }

        });
        this.layerManager.add(ResourceLocation.fromNamespaceAndPath(Boxhud.MOD_ID, "widgets"), (graphics, deltaTracker) -> {
            graphics.nextStratum();
            BoxWidgets.renderAll(graphics);
        });

        ci.cancel();
    }

}
