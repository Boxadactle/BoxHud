package dev.boxadactle.boxhud.mixin;

import com.mojang.blaze3d.platform.GlStateManager;
import dev.boxadactle.boxhud.BoxWidgets;
import dev.boxadactle.boxhud.WidgetEntry;
import dev.boxadactle.boxhud.widget.vanilla.CrosshairWidget;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.DebugScreenOverlay;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Gui.class)
public class CrosshairMixin {
    @Redirect(
            method = "renderCrosshair",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/GuiGraphics;blitSprite(Lnet/minecraft/resources/ResourceLocation;IIII)V",
                    ordinal = 0
            )
    )
    public void redirectBlitSprite(GuiGraphics guiGraphics, ResourceLocation resourceLocation, int x, int y, int width, int height) {
        WidgetEntry<CrosshairWidget> entry = BoxWidgets.getWidgetEntryUnchecked("crosshair");

        assert entry != null;
        entry.widget.renderCrosshair(guiGraphics, 0, 0);
    }

    @ModifyVariable(
            method = "renderCrosshair",
            at = @At("STORE"),
            ordinal = 1
    )
    public int modifyIndicatorY(int y) {
        return 15;
    }

    @ModifyVariable(
            method = "renderCrosshair",
            at = @At("STORE"),
            ordinal = 2
    )
    public int modifyIndicatorX(int x) {
        return 0;
    }

    @Redirect(
            method = "renderCrosshair",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/components/DebugScreenOverlay;showDebugScreen()Z",
                    ordinal = 0
            )
    )
    public boolean redirectIsReducedDebugInfo(DebugScreenOverlay instance) {
        WidgetEntry<CrosshairWidget> entry = BoxWidgets.getWidgetEntryUnchecked("crosshair");

        return instance.showDebugScreen() && entry.widget.allowDebugCrosshair;
    }

    @Redirect(
            method = "renderCrosshair",
            at = @At(
                    value = "INVOKE",
                    target = "Lorg/joml/Matrix4fStack;translate(FFF)Lorg/joml/Matrix4f;",
                    ordinal = 0
            ),
            remap = false
    )
    public Matrix4f redirectTranslate(Matrix4fStack instance, float x, float y, float z) {
        return instance.translate(10.0F, 10.0F, 0.0F);
    }

    @Redirect(
            method = "renderCrosshair",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/mojang/blaze3d/systems/RenderSystem;blendFuncSeparate(Lcom/mojang/blaze3d/platform/GlStateManager$SourceFactor;Lcom/mojang/blaze3d/platform/GlStateManager$DestFactor;Lcom/mojang/blaze3d/platform/GlStateManager$SourceFactor;Lcom/mojang/blaze3d/platform/GlStateManager$DestFactor;)V",
                    ordinal = 0
            )
    )
    public void blockBlending(GlStateManager.SourceFactor $$0, GlStateManager.DestFactor $$1, GlStateManager.SourceFactor $$2, GlStateManager.DestFactor $$3) {
        // we do our own blending
    }
}
