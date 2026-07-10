package dev.boxadactle.boxhud.fabric;

import dev.boxadactle.boxhud.Bindings;
import dev.boxadactle.boxhud.Boxhud;
import dev.boxadactle.boxhud.fabric.mixin.GuiInvoker;
import dev.boxadactle.boxhud.util.ModUtil;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.network.chat.ComponentContents;
import net.minecraft.network.chat.contents.TranslatableContents;

public final class BoxhudFabric implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        Boxhud.init();

        ClientTickEvents.END_CLIENT_TICK.register(m -> {
            Boxhud.tick();
            Bindings.check();
        });

        ModUtil.packExclusionFilter = (pack) -> {
            if (pack.getId().equalsIgnoreCase("fabric")) {
                return true;
            }

            ComponentContents titleContents = pack.getTitle().getContents();
            return titleContents instanceof TranslatableContents translatableContents
                    && translatableContents.getKey().contains("fabricMod");
        };
        Bindings.register(KeyMappingHelper::registerKeyMapping);

        ModUtil.hotbarRenderer = (gui, graphics, delta) -> ((GuiInvoker) gui.hud).invokeRenderHotbar(graphics, delta);
    }
}
