package dev.boxadactle.boxhud.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.boxadactle.boxlib.util.GuiUtils;
import net.minecraft.client.gui.Gui;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Gui.class)
public class ActionbarMixin {

    @Shadow private Component overlayMessageString;

    @Redirect(
            method = "renderOverlayMessage",
            at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;translate(FFF)V")
    )
    public void dontTranslate(PoseStack instance, float x, float y, float z) {
        instance.translate((float) GuiUtils.getTextRenderer().width(overlayMessageString) / 2, 4.0F, 0.0F);
    }

}
