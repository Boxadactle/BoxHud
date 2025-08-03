package dev.boxadactle.boxhud.mixin;

import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Minecraft.class)
public interface MinecraftAccessor {

    @Accessor("fps")
    int getCurrentFps();

    @Accessor("fpsString")
    String getFpsString();
}
