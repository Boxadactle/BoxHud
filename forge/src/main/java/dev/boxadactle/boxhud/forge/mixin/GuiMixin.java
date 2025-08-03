package dev.boxadactle.boxhud.forge.mixin;

import dev.boxadactle.boxhud.BoxWidgets;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.client.gui.components.DebugScreenOverlay;
import net.minecraft.client.gui.components.SubtitleOverlay;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public abstract class GuiMixin {

    @Shadow @Final private LayeredDraw layers;

    @Shadow protected abstract void renderCameraOverlays(GuiGraphics guiGraphics, float partialTick);

    @Shadow protected abstract void renderEffects(GuiGraphics guiGraphics, float partialTick);

    @Shadow protected abstract void renderDemoOverlay(GuiGraphics guiGraphics, float partialTick);

    @Shadow @Final protected DebugScreenOverlay debugOverlay;

    @Shadow protected abstract void renderScoreboardSidebar(GuiGraphics guiGraphics, float partialTick);

    @Shadow protected abstract void renderTitle(GuiGraphics guiGraphics, float partialTick);

    @Shadow protected abstract void renderChat(GuiGraphics guiGraphics, float partialTick);

    @Shadow protected abstract void renderTabList(GuiGraphics guiGraphics, float partialTick);

    @Shadow @Final protected SubtitleOverlay subtitleOverlay;

    @Inject(
            method = "<init>",
            at = @At("RETURN")
    )
    public void init(CallbackInfo ci) {
        layers.add(((guiGraphics, f) -> BoxWidgets.renderAll(guiGraphics)));
    }

    @ModifyArg(
            method = "<init>",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/LayeredDraw;add(Lnet/minecraft/client/gui/LayeredDraw;Ljava/util/function/BooleanSupplier;)Lnet/minecraft/client/gui/LayeredDraw;",
                    ordinal = 0
            )
    )
    public LayeredDraw removeRenderers(LayeredDraw layeredDraw) {
        // we only add what we don't override so it still renders
        return (new LayeredDraw()).add(this::renderCameraOverlays).add(this::renderEffects);
    }

    @ModifyArg(
            method = "<init>",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/LayeredDraw;add(Lnet/minecraft/client/gui/LayeredDraw;Ljava/util/function/BooleanSupplier;)Lnet/minecraft/client/gui/LayeredDraw;",
                    ordinal = 1
            )
    )
    public LayeredDraw removeRenderers2(LayeredDraw layeredDraw) {
        // we only add what we don't override so it still renders
        return (new LayeredDraw()).add(this::renderDemoOverlay).add((guiGraphics, f) -> {
            if (this.debugOverlay.showDebugScreen()) {
                this.debugOverlay.render(guiGraphics);
            }
        }).add(this::renderTitle).add(this::renderChat).add(this::renderTabList).add((guiGraphics, f) -> this.subtitleOverlay.render(guiGraphics));
    }

}
