package dev.boxadactle.boxhud.fabric;

import dev.boxadactle.boxhud.Bindings;
import dev.boxadactle.boxhud.Boxhud;
import dev.boxadactle.boxhud.fabric.mixin.GuiInvoker;
import dev.boxadactle.boxhud.util.ModUtil;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.network.chat.contents.TranslatableContents;

public final class BoxhudFabric implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        Boxhud.init();

        ClientTickEvents.END_CLIENT_TICK.register(m -> {
            Boxhud.tick();
            Bindings.check();
        });

        ModUtil.packExclusionFilter = (pack) -> ((TranslatableContents)(pack.getTitle().getContents())).getKey().contains("fabricMod") || pack.getId().equalsIgnoreCase("fabric");
        Bindings.register(KeyMappingHelper::registerKeyMapping);

        ModUtil.hotbarRenderer = (gui, graphics, delta) -> ((GuiInvoker) gui).invokeRenderHotbar(graphics, delta);
    }
}
