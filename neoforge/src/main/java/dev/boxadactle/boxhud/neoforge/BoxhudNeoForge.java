package dev.boxadactle.boxhud.neoforge;

import dev.boxadactle.boxhud.*;
import dev.boxadactle.boxhud.neoforge.mixin.GuiInvoker;
import dev.boxadactle.boxhud.util.ModUtil;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

@Mod(Boxhud.MOD_ID)
public final class BoxhudNeoForge {
    public BoxhudNeoForge() {
        ModLoadingContext.get().registerExtensionPoint(IConfigScreenFactory.class, () ->
                (minecraft, screen) -> new WidgetListScreen(screen)
        );

        ModUtil.packExclusionFilter = (pack) -> pack.getId().startsWith("mod/") || pack.getId().equalsIgnoreCase("mod_resources");
        ModUtil.hotbarRenderer = (gui, graphics, delta) -> ((GuiInvoker) gui).invokeRenderHotbar(graphics, delta);
    }

    @EventBusSubscriber(modid = Boxhud.MOD_ID, value = Dist.CLIENT)
    public static class ClientNeoforgeEvents {
        @SubscribeEvent
        public static void go(FMLClientSetupEvent e) {
            Boxhud.init();
        }

        @SubscribeEvent
        public static void tick(ClientTickEvent.Pre event) {
            Boxhud.tick();
        }

        @SubscribeEvent
        public static void keyInput(InputEvent.Key e) {
            Bindings.check();
        }

        @SubscribeEvent
        public static void registerKeys(RegisterKeyMappingsEvent e) {
            Bindings.register(e::register);
        }
    }
}
