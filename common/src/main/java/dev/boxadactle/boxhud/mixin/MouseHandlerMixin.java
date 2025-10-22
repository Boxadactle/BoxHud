package dev.boxadactle.boxhud.mixin;

import dev.boxadactle.boxhud.events.BoxEvents;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.input.MouseButtonInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MouseHandler.class)
public class MouseHandlerMixin {

    @Inject(
            method = "onButton",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/KeyMapping;set(Lcom/mojang/blaze3d/platform/InputConstants$Key;Z)V"
            )
    )
    private void onPress(long p_window, MouseButtonInfo buttonInfo, int action, CallbackInfo ci) {
        switch (buttonInfo.button()) {
            case 0 -> BoxEvents.CLICK_LEFT_MOUSE.invoker().onClick(action, buttonInfo.modifiers());
            case 1 -> BoxEvents.CLICK_RIGHT_MOUSE.invoker().onClick(action, buttonInfo.modifiers());
        }
    }

}
