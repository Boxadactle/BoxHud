package dev.boxadactle.boxhud.neoforge;

import dev.boxadactle.boxhud.*;
import dev.boxadactle.boxhud.util.ModUtil;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

@Mod(Boxhud.MOD_ID)
public final class BoxhudNeoForge {
    public BoxhudNeoForge() {
        Boxhud.init();

        ModLoadingContext.get().registerExtensionPoint(IConfigScreenFactory.class, () ->
                (minecraft, screen) -> new WidgetListScreen(screen)
        );

        ModUtil.packExclusionFilter = (pack) -> pack.getId().startsWith("mod/") || pack.getId().equalsIgnoreCase("mod_resources");
    }

    @EventBusSubscriber(modid = Boxhud.MOD_ID, value = Dist.CLIENT)
    public static class ClientNeoforgeEvents {
        @SubscribeEvent
        public static void tick(ClientTickEvent.Pre event) {
            Boxhud.tick();
        }

        @SubscribeEvent
        public static void keyInput(InputEvent.Key e) {
            Bindings.check();
        }
    }

    @EventBusSubscriber(modid = Boxhud.MOD_ID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
    public static class ModForgeEvents {
        @SubscribeEvent
        public static void registerKeys(RegisterKeyMappingsEvent e) {
            Bindings.register(e::register);
        }
    }
}
