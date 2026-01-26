package dev.boxadactle.boxhud.mixin;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import dev.boxadactle.boxhud.BoxWidgets;
import dev.boxadactle.boxhud.WidgetEntry;
import dev.boxadactle.boxhud.widget.vanilla.CrosshairWidget;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.Identifier;
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
                    target = "Lnet/minecraft/client/gui/GuiGraphics;blitSprite(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/resources/Identifier;IIII)V"
            )
    )
    public void redirectBlitSprite(GuiGraphics instance, RenderPipeline pipeline, Identifier sprite, int x, int y, int width, int height) {
        WidgetEntry<CrosshairWidget> entry = BoxWidgets.getWidgetEntryUnchecked("crosshair");

        assert entry != null;
        entry.widget.renderCrosshair(instance, 0, 0);
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
}
