package dev.boxadactle.boxhud.mixin;

import dev.boxadactle.boxlib.util.GuiUtils;
import net.minecraft.client.gui.Gui;
import net.minecraft.network.chat.Component;
import org.joml.Matrix3x2f;
import org.joml.Matrix3x2fStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Gui.class)
public class ActionbarMixin {

    @Shadow private Component overlayMessageString;

    @Redirect(
            method = "renderOverlayMessage",
            at = @At(value = "INVOKE", target = "Lorg/joml/Matrix3x2fStack;translate(FF)Lorg/joml/Matrix3x2f;"),
            remap = false
    )
    public Matrix3x2f dontTranslate(Matrix3x2fStack instance, float v, float i) {
        instance.translate((float) GuiUtils.getTextRenderer().width(overlayMessageString) / 2, 4.0F);
        return instance;
    }

}
