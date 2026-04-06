package dev.boxadactle.boxhud.mixin;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.BossHealthOverlay;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BossHealthOverlay.class)
public class BossHealthOverlayMixin {

    @Inject(
            method = "extractRenderState",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/util/profiling/ProfilerFiller;push(Ljava/lang/String;)V")
    )
    public void translateBar(GuiGraphicsExtractor p_283175_, CallbackInfo ci) {
        p_283175_.pose().pushMatrix();
        p_283175_.pose().translate((float) -p_283175_.guiWidth() / 2 + 91, -3);
    }

    @Inject(
            method = "extractRenderState",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/util/profiling/ProfilerFiller;pop()V")
    )
    public void popBar(GuiGraphicsExtractor p_283175_, CallbackInfo ci) {
        p_283175_.pose().popMatrix();
    }

}
