package dev.boxadactle.boxhud.forge;

import dev.boxadactle.boxhud.*;
import dev.boxadactle.boxhud.util.ModUtil;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;

@Mod(Boxhud.MOD_ID)
public final class BoxhudForge {
    public BoxhudForge() {
        Boxhud.init();

        ModLoadingContext.get().registerExtensionPoint(ConfigScreenHandler.ConfigScreenFactory.class, () ->
                new ConfigScreenHandler.ConfigScreenFactory((minecraft, screen) -> new WidgetListScreen(screen))
        );

        ModUtil.packExclusionFilter = (pack) -> pack.getId().equalsIgnoreCase("mod_resources");
    }

    @Mod.EventBusSubscriber(modid = Boxhud.MOD_ID, value = Dist.CLIENT)
    public static class ClientForgeEvents {
        @SubscribeEvent
        public static void tick(TickEvent.ClientTickEvent.Pre event) {
            Boxhud.tick();
        }

        @SubscribeEvent
        public static void keyInput(InputEvent.Key e) {
            Bindings.check();
        }
    }

    @Mod.EventBusSubscriber(modid = Boxhud.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ModForgeEvents {
        @SubscribeEvent
        public static void registerKeys(RegisterKeyMappingsEvent e) {
            Bindings.register(e::register);
        }
    }
}
