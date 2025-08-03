package dev.boxadactle.boxhud.mixin;

import dev.boxadactle.boxhud.util.CursorUtil;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MinecraftMixin {

    @Inject(
            method = "destroy",
            at = @At("HEAD")
    )
    public void onClose(CallbackInfo ci) {
        CursorUtil.destruct();
    }

    @Inject(
            method = "<init>",
            at = @At("RETURN")
    )
    public void onInit(CallbackInfo ci) {
        CursorUtil.init();
    }

}
