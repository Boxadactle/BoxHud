package dev.boxadactle.boxhud.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.scores.Objective;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public class ScoreboardMixin {

    @Inject(
            method = "displayScoreboardSidebar",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;fill(IIIII)V", ordinal = 0)
    )
    public void startTranslation(GuiGraphics guiGraphics, Objective objective, CallbackInfo ci, @Local(ordinal = 1) int j) {
        guiGraphics.pose().pushPose();
        int j1 = objective.getScoreboard().listPlayerScores(objective).stream().filter(l -> !l.isHidden()).limit(15L).toArray().length * 9;
        int k1 = guiGraphics.guiHeight() / 2 + j1 / 3;
        int i3 = k1 - j1;
        guiGraphics.pose().translate(
                -guiGraphics.guiWidth() + 5.0F + j,
                -i3 + 10.0F,
                0.0F
        );
    }

    @Inject(
            method = "displayScoreboardSidebar",
            at = @At("TAIL")
    )
    public void endTranslation(GuiGraphics guiGraphics, Objective objective, CallbackInfo ci) {
        guiGraphics.pose().popPose();
    }

}
