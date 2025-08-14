package dev.boxadactle.boxhud.fabric;

import dev.boxadactle.boxhud.Bindings;
import dev.boxadactle.boxhud.Boxhud;
import dev.boxadactle.boxhud.fabric.mixin.GuiInvoker;
import dev.boxadactle.boxhud.util.ModUtil;
import dev.boxadactle.boxlib.keybind.KeybindHelper;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.impl.resource.loader.FabricResourcePackProfile;

public final class BoxhudFabric implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        Boxhud.init();

        ClientTickEvents.END_CLIENT_TICK.register(m -> {
            Boxhud.tick();
            Bindings.check();
        });

        ModUtil.packExclusionFilter = (pack) -> ((FabricResourcePackProfile) pack).fabric_isHidden() || pack.getId().equalsIgnoreCase("fabric");
        Bindings.register(KeybindHelper::registerKey);

        ModUtil.hotbarRenderer = (gui, graphics, delta) -> ((GuiInvoker) gui).invokeRenderHotbar(graphics, delta);
    }
}
