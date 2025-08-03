package dev.boxadactle.boxhud.neoforge.mixin;

import dev.boxadactle.boxhud.BoxWidgets;
import dev.boxadactle.boxhud.Boxhud;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.client.gui.components.DebugScreenOverlay;
import net.minecraft.client.gui.components.SubtitleOverlay;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.gui.GuiLayerManager;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@SuppressWarnings("UnstableApiUsage")
@Mixin(Gui.class)
public abstract class GuiMixin {
    @Shadow @Final private GuiLayerManager layerManager;

    @Shadow protected abstract void renderCameraOverlays(GuiGraphics guiGraphics, DeltaTracker deltaTracker);

    @Shadow protected abstract void maybeRenderSpectatorTooltip(GuiGraphics p_316628_, DeltaTracker deltaTracker);

    @Shadow protected abstract void renderEffects(GuiGraphics guiGraphics, DeltaTracker deltaTracker);

    @Shadow protected abstract void renderDemoOverlay(GuiGraphics guiGraphics, DeltaTracker deltaTracker);

    @Shadow @Final protected DebugScreenOverlay debugOverlay;

    @Shadow protected abstract void renderTitle(GuiGraphics guiGraphics, DeltaTracker deltaTracker);

    @Shadow protected abstract void renderChat(GuiGraphics guiGraphics, DeltaTracker deltaTracker);

    @Shadow protected abstract void renderTabList(GuiGraphics guiGraphics, DeltaTracker deltaTracker);

    @Shadow @Final protected SubtitleOverlay subtitleOverlay;

    @Shadow public abstract void renderSavingIndicator(GuiGraphics guiGraphics, DeltaTracker deltaTracker);

    @Inject(
            method = "<init>",
            at = @At("RETURN")
    )
    public void init(CallbackInfo ci) {
        layerManager.add(ResourceLocation.fromNamespaceAndPath(Boxhud.MOD_ID, "widgets"), ((guiGraphics, f) -> BoxWidgets.renderAll(guiGraphics)));
    }

    @ModifyArg(
            method = "<init>",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/neoforged/neoforge/client/gui/GuiLayerManager;add(Lnet/neoforged/neoforge/client/gui/GuiLayerManager;Ljava/util/function/BooleanSupplier;)Lnet/neoforged/neoforge/client/gui/GuiLayerManager;",
                    ordinal = 1
            )
    )
    public GuiLayerManager overrideManager(GuiLayerManager value) {
        // we only add what we aren't overriding so it still renders
        return (new GuiLayerManager())
                .add(VanillaGuiLayers.CAMERA_OVERLAYS, this::renderCameraOverlays)
                .add(VanillaGuiLayers.SPECTATOR_TOOLTIP, this::maybeRenderSpectatorTooltip)
                .add(VanillaGuiLayers.EFFECTS, this::renderEffects);
    }

    @ModifyArg(
            method = "<init>",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/neoforged/neoforge/client/gui/GuiLayerManager;add(Lnet/neoforged/neoforge/client/gui/GuiLayerManager;Ljava/util/function/BooleanSupplier;)Lnet/neoforged/neoforge/client/gui/GuiLayerManager;",
                    ordinal = 2
            )
    )
    public GuiLayerManager overrideManager2(GuiLayerManager value) {
        // we only add what we aren't overriding so it still renders
        return (new GuiLayerManager())
                .add(VanillaGuiLayers.DEMO_OVERLAY, this::renderDemoOverlay)
                .add(VanillaGuiLayers.DEBUG_OVERLAY, (p_315812_, p_315813_) -> {
                    if (this.debugOverlay.showDebugScreen()) {
                        this.debugOverlay.render(p_315812_);
                    }
                })
                .add(VanillaGuiLayers.TITLE, this::renderTitle)
                .add(VanillaGuiLayers.CHAT, this::renderChat)
                .add(VanillaGuiLayers.TAB_LIST, this::renderTabList)
                .add(VanillaGuiLayers.SUBTITLE_OVERLAY, (p_315816_, p_315817_) -> this.subtitleOverlay.render(p_315816_))
                .add(VanillaGuiLayers.SAVING_INDICATOR, this::renderSavingIndicator);
    }

}
